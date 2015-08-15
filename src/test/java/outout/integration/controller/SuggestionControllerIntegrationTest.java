package outout.integration.controller;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import outout.OutoutApplication;
import outout.model.Suggestion;
import outout.util.DatabaseCleanup;
import outout.util.Nuke;
import outout.util.TestApplicationPaths;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;
import outout.view.RestaurantSuggestion;
import outout.view.RestaurantSuggestions;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.exparity.hamcrest.date.DateMatchers.sameDay;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OutoutApplication.class)
@WebIntegrationTest
public class SuggestionControllerIntegrationTest {
    private RestTemplate client;
    private AccountCredentials accountCredentials;
    private String authenticationToken;
    private String suggestedRestaurant;
    private DatabaseCleanup databaseCleanup;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        client = new RestTemplate();
        databaseCleanup = new DatabaseCleanup(dataSource);
    }

    @After
    public void tearDown() throws Exception {
        databaseCleanup.deleteUsers();
        databaseCleanup.deleteSuggestions();
        Nuke.nukeFields(this);
    }

    @Test
    public void suggestingRestaurantPersistsSuggestion() throws Exception {
        initializeStateForMakingSuggestion();
        client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.POST, buildSuggestionEntity(), Void.class);

        Suggestion suggestion = findUserSuggestion();
        assertThat(suggestion, is(not(nullValue())));
        assertThat(suggestion.getSuggestion(), is(suggestedRestaurant));
        assertThat(suggestion.getSuggestedDate(), is(sameDay(new Date())));
    }

    private Suggestion findUserSuggestion() {
        Query query = entityManager.createQuery("select s from Suggestion s where s.suggestedBy = :user");
        query.setParameter("user", accountCredentials.getUsername());
        query.setMaxResults(1);
        List<Suggestion> suggestions = query.getResultList();
        return !suggestions.isEmpty() ? suggestions.get(0) : null;
    }

    private void initializeStateForMakingSuggestion() {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testme");
        accountCredentials.setPassword("passwordpassword");

        client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH,
                accountCredentials, Void.class);

        ResponseEntity<AuthenticationToken> response = client.postForEntity(TestApplicationPaths.AUTHENTICATION_PATH,
                accountCredentials, AuthenticationToken.class);
        authenticationToken = response.getBody().getToken();
        suggestedRestaurant = "Five Points Pizza";
    }

    private HttpEntity<RestaurantSuggestion> buildSuggestionEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-TOKEN", authenticationToken);
        headers.add("Content-Type", "application/json");
        HttpEntity<RestaurantSuggestion> entity = new HttpEntity<>(new RestaurantSuggestion(suggestedRestaurant), headers);
        return entity;
    }

    @Test
    public void unauthorizedUserSuggestionIsForbidden() throws Exception {
        initializeStateForMakingSuggestion();
        try {
            client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.POST, buildSuggestionEntityWithoutAuthorization(), Void.class);
            fail("Should have return an HTTP 403 status code");
        }
        catch (HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.FORBIDDEN));
        }
        catch (Exception exc) {
            fail("Should have return an HTTP 403 status code");
        }
    }

    private HttpEntity<RestaurantSuggestion> buildSuggestionEntityWithoutAuthorization() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<RestaurantSuggestion> entity = new HttpEntity<>(new RestaurantSuggestion(suggestedRestaurant), headers);
        return entity;
    }

    @Test
    public void restaurantsCanOnlyBeSuggestedOncePerDay() throws Exception {
        initializeStateForMakingSuggestion();
        insertSuggestion(DateTime.now().toDate());

        try {
            client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.POST, buildSuggestionEntity(), Void.class);
            fail("Should have thrown returned a HTTP 422 status code");
        }
        catch (HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        }
        catch (Exception exc) {
            exc.printStackTrace();
            fail("Should have thrown returned a HTTP 422 status code");
        }
    }

    private void insertSuggestion(final Date suggestedDate) {
        try(Connection connection = dataSource.getConnection()){
            connection.setAutoCommit(true);
            PreparedStatement statement = connection.prepareStatement("insert into suggestion(id,suggested_by,suggestion,suggested_date)" +
                    " values(suggestion_seq.nextval,?,?,?)");
            statement.setString(1, accountCredentials.getUsername());
            statement.setString(2, suggestedRestaurant);
            statement.setDate(3, new java.sql.Date(suggestedDate.getTime()));
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void restaurantsCanBeSuggestedAgainOnADifferentDay() throws Exception {
        initializeStateForMakingSuggestion();
        insertSuggestion(DateTime.now().minusDays(1).toDate());

        client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.POST, buildSuggestionEntity(), Void.class);
        Suggestion suggestion = findUserSuggestionByDate(DateTime.now().toDate());
        assertThat(suggestion, is(not(nullValue())));
        assertThat(suggestion.getSuggestion(), is(suggestedRestaurant));
        assertThat(suggestion.getSuggestedDate(), is(sameDay(new Date())));
    }

    private Suggestion findUserSuggestionByDate(Date suggestedDate) {
        Query query = entityManager.createQuery("select s from Suggestion s where s.suggestedBy = :user " +
                "and trunc(s.suggestedDate) = trunc(:suggestedDate)");
        query.setParameter("user", accountCredentials.getUsername());
        query.setParameter("suggestedDate", suggestedDate);
        query.setMaxResults(1);
        List<Suggestion> suggestions = query.getResultList();
        return !suggestions.isEmpty() ? suggestions.get(0) : null;
    }

    @Test
    public void userCanOnlyMakeTwoSuggestionsPerDay() throws Exception {
        initializeStateForMakingSuggestion();
        insertSuggestion();
        suggestedRestaurant = "Hattie B's";
        insertSuggestion();
        suggestedRestaurant = "Big Al's Deli";

        try {
            client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.POST, buildSuggestionEntity(), Void.class);
            fail("Should have thrown returned a HTTP 422 status code");
        }
        catch (HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        }
        catch (Exception exc) {
            exc.printStackTrace();
            fail("Should have thrown returned a HTTP 422 status code");
        }
    }

    private void insertSuggestion() {
        insertSuggestion(DateTime.now().toDate());
    }

    @Test
    public void userCanRetrieveSuggestionsForToday() throws Exception {
        initializeStateForMakingSuggestion();
        insertSuggestion();
        suggestedRestaurant = "Hattie B's";
        insertSuggestion();

        ResponseEntity<RestaurantSuggestions> restaurantSuggestionsEntity =
                client.exchange(TestApplicationPaths.SUGGESTION_PATH, HttpMethod.GET, buildGetSuggestionEntity(), RestaurantSuggestions.class);
        RestaurantSuggestions restaurantSuggestions = restaurantSuggestionsEntity.getBody();
        assertThat(restaurantSuggestionsEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(restaurantSuggestions.getRestaurantSuggestions().size(), is(2));

    }

    private HttpEntity<RestaurantSuggestion> buildGetSuggestionEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-AUTH-TOKEN", authenticationToken);
        headers.add("Content-Type", "application/json");
        HttpEntity<RestaurantSuggestion> entity = new HttpEntity<>(headers);
        return entity;
    }
}
