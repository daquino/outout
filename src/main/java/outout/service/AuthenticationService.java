package outout.service;

import org.springframework.security.core.AuthenticationException;
import outout.model.User;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

public interface AuthenticationService {
    AuthenticationToken getAuthenticationToken(AccountCredentials accountCredentials);
}
