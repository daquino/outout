package outout.integration.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import outout.OutoutApplication;
import outout.util.DatabaseCleanup;
import outout.util.TestApplicationPaths;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OutoutApplication.class)
@WebIntegrationTest
public class AuthenticationControllerIntegrationTest {
    private RestTemplate client;
    private AccountCredentials accountCredentials;
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private DataSource dataSource;

    @Value("${token.secret}")
    private String tokenSecret;

    @Before
    public void setUp() throws Exception {
        client = new RestTemplate();
        databaseCleanup = new DatabaseCleanup(dataSource);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        accountCredentials = null;
        databaseCleanup.deleteUsers();
    }

    @Test
    public void authenticatingWithValidCredentialsReturnsAuthenticationToken() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testme");
        accountCredentials.setPassword("passwordpassword");

        client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH,
                accountCredentials, Void.class);

        ResponseEntity<AuthenticationToken> response = client.postForEntity(TestApplicationPaths.AUTHENTICATION_PATH,
                accountCredentials, AuthenticationToken.class);
        AuthenticationToken authenticationToken = response.getBody();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(authenticationToken, is(not(nullValue())));
        assertThat(authenticationToken.getToken(), is(buildToken(accountCredentials)));
    }

    private String buildToken(final AccountCredentials accountCredentials) {
        return Jwts.builder().signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setSubject(accountCredentials.getUsername())
                .compact();
    }

    @Test
    public void authenticatingWithInvalidUsernameReturnsNotFound() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("anothertestaccount");
        accountCredentials.setPassword("passwordpassword");

        try {
            ResponseEntity<AuthenticationToken> response = client.postForEntity(TestApplicationPaths.AUTHENTICATION_PATH,
                    accountCredentials, AuthenticationToken.class);
        }
        catch(HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.NOT_FOUND));
        }
        catch(Exception exc) {
            fail("Should have thrown return Http Status 404");
        }
    }

    @Test
    public void authenticatingWithWrongPasswordReturnsNotFound() throws Exception {
        accountCredentials = new AccountCredentials();
        accountCredentials.setUsername("testme");
        accountCredentials.setPassword("passwordpassword");
        AccountCredentials badCredentials = new AccountCredentials();
        badCredentials.setUsername(accountCredentials.getUsername());
        badCredentials.setPassword("thisisthewrongpassword");

        client.postForEntity(TestApplicationPaths.CREATE_ACCOUNT_PATH,
                accountCredentials, Void.class);

        try {
            client.postForEntity(TestApplicationPaths.AUTHENTICATION_PATH,
                    badCredentials, AuthenticationToken.class);
            fail("Should return 404 with wrong password.");
        }
        catch(HttpClientErrorException exc) {
            assertThat(exc.getStatusCode(), is(HttpStatus.NOT_FOUND));
        }
        catch(Exception exc) {
            fail("Should have thrown return Http Status 404 with the wrong password");
        }
    }
}
