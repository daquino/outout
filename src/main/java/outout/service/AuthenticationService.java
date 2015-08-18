package outout.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import outout.model.User;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

public interface AuthenticationService {
    boolean isAuthenticated(AccountCredentials credentials, User user);
    AuthenticationToken createAuthenticationToken(AccountCredentials credentials);
    ResponseEntity<AuthenticationToken> createFailedAuthenticationResponse();
    ResponseEntity<AuthenticationToken> createSuccessfulAuthenticationResponse(AccountCredentials credentials);
}
