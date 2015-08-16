package outout.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import outout.integration.controller.AuthenticationControllerIntegrationTest;
import outout.integration.controller.CreateAccountControllerIntegrationTest;
import outout.integration.controller.SuggestionControllerIntegrationTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateAccountControllerIntegrationTest.class,
        AuthenticationControllerIntegrationTest.class,
        SuggestionControllerIntegrationTest.class
})
public class IntegrationTestSuite {
}
