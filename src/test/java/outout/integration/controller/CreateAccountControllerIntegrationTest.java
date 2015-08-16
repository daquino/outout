package outout.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import outout.OutoutApplication;
import outout.model.User;
import outout.util.DataLoader;
import outout.util.DatabaseCleanup;
import outout.util.Nuke;
import outout.util.TestApplicationPaths;
import outout.view.AccountCreationResult;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OutoutApplication.class)
@WebIntegrationTest
public class CreateAccountControllerIntegrationTest {
    private RestTemplate client;
    private AccountCredentials accountCredentials;
    private ResponseEntity<AccountCreationResult> response;
    private ObjectMapper mapper;
    private DataLoader loader;
    private DatabaseCleanup databaseCleanup;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() throws Exception {
        client = new RestTemplate();
        mapper = new ObjectMapper();
        loader = new DataLoader(dataSource, passwordEncoder);
        databaseCleanup = new DatabaseCleanup(dataSource);
    }

    @After
    public void tearDown() throws Exception {
        databaseCleanup.deleteUsers();
        Nuke.nukeFields(this);
    }

    @Test
    public void creatingAccountWithValidCredentialsStoresAccountInDatabase() {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testme");
        accountCredentials.setPassword("passwordpassword");

        response = client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, AccountCreationResult.class);
        User user = findUserByUsername(accountCredentials.getUsername());
        AccountCreationResult accountCreationResult = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(accountCreationResult.isSuccessful(), is(true));
        assertThat(user.getUsername(), is(accountCredentials.getUsername()));
    }

    private User findUserByUsername(String username) {
        Query query = entityManager.createQuery("select u from User u where u.username = :username");
        query.setParameter("username", username);
        query.setMaxResults(1);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @Test
    public void creatingAccountWithInvalidUsernameReturnsInvalidUserNameError() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("test");
        accountCredentials.setPassword("passwordpassword");
        AccountCreationResult accountCreationResult;
        HttpStatus statusCode;

        try {
            response = client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, AccountCreationResult.class);
            accountCreationResult = response.getBody();
            statusCode = response.getStatusCode();
        }
        catch(HttpClientErrorException exc) {
            accountCreationResult = mapper.readValue(exc.getResponseBodyAsString(), AccountCreationResult.class);
            statusCode = exc.getStatusCode();
        }
        User user = findUserByUsername(accountCredentials.getUsername());
        assertThat(statusCode, is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(accountCreationResult.isSuccessful(), is(false));
        assertThat(accountCreationResult.getErrors().get(0), is("Username must be at least 5 characters"));
        assertThat(user, is(nullValue()));
    }

    @Test
    public void creatingAccountWithInvalidPasswordReturnsInvalidPasswordError() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testmealot");
        accountCredentials.setPassword("password");
        AccountCreationResult accountCreationResult;
        HttpStatus statusCode;

        try {
            response = client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, AccountCreationResult.class);
            accountCreationResult = response.getBody();
            statusCode = response.getStatusCode();
        }
        catch(HttpClientErrorException exc) {
            accountCreationResult = mapper.readValue(exc.getResponseBodyAsString(), AccountCreationResult.class);
            statusCode = exc.getStatusCode();
        }

        User user = findUserByUsername(accountCredentials.getUsername());
        assertThat(statusCode, is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(accountCreationResult.isSuccessful(), is(false));
        assertThat(accountCreationResult.getErrors().get(0), is("Password must be at least 10 characters"));
        assertThat(user, is(nullValue()));
    }

    @Test
    public void creatingAccountWithExistingUsernameReturnsUnprocessableEntity() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testmealot");
        accountCredentials.setPassword("password123");
        loader.insertUser(accountCredentials.getUsername(), accountCredentials.getPassword());
        AccountCreationResult accountCreationResult;
        HttpStatus statusCode;

        try {
            response = client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, AccountCreationResult.class);
            accountCreationResult = response.getBody();
            statusCode = response.getStatusCode();
        }
        catch(HttpClientErrorException exc) {
            accountCreationResult = mapper.readValue(exc.getResponseBodyAsString(), AccountCreationResult.class);
            statusCode = exc.getStatusCode();
        }

        assertThat(statusCode, is(HttpStatus.UNPROCESSABLE_ENTITY));
        assertThat(accountCreationResult.isSuccessful(), is(false));
        assertThat(accountCreationResult.getErrors().get(0), is("Username already in use.  Please enter another username."));
    }
}
