package outout.integration.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import outout.OutoutApplication;
import outout.model.User;
import outout.util.TestApplicationPaths;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OutoutApplication.class)
@WebIntegrationTest
public class CreateAccountControllerIntegrationTest {
    private RestTemplate client;
    private AccountCredentials accountCredentials;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        client = new RestTemplate();
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        accountCredentials = null;
    }

    @Test
    public void creatingAccountWithValidCredentialsStoresAccountInDatabase() {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testme");
        accountCredentials.setPassword("passwordpassword");

        ResponseEntity<Void> response = client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, Void.class);
        User user = findUserByUsername(accountCredentials.getUsername());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(user.getUsername(), is(accountCredentials.getUsername()));
    }

    private User findUserByUsername(String username) {
        Query query = entityManager.createQuery("select u from User u where u.username = :username");
        query.setParameter("username", accountCredentials.getUsername());
        query.setMaxResults(1);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    @Test
    public void creatingAccountWithInvalidUsernameReturnsBadRequest() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("test");
        accountCredentials.setPassword("passwordpassword");

        try {
            client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, Void.class);
        }
        catch (HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        }
        catch(Exception exc) {
            fail("Should have caught HttpClientException");
        }
        User user = findUserByUsername(accountCredentials.getUsername());
        assertThat(user, is(nullValue()));
    }

    @Test
    public void creatingAccountWithInvalidPasswordReturnsBadRequest() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testmealot");
        accountCredentials.setPassword("password");

        try {
            client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH, accountCredentials, Void.class);
        }
        catch (HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        }
        catch(Exception exc) {
            fail("Should have caught HttpClientException");
        }
        User user = findUserByUsername(accountCredentials.getUsername());
        assertThat(user, is(nullValue()));
    }
}
