package outout.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import outout.model.User;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${token.secret}")
    private String tokenSecret;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean isAuthenticated(AccountCredentials credentials, User user) {
        return user != null && passwordEncoder.matches(credentials.getPassword(), user.getPassword());
    }

    @Override
    public AuthenticationToken createAuthenticationToken(AccountCredentials credentials) {
        AuthenticationToken authenticationToken = new AuthenticationToken();
        String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setSubject(credentials.getUsername())
                .compact();
        authenticationToken.setToken(jwt);
        return authenticationToken;
    }

    @Override
    public ResponseEntity<AuthenticationToken> createFailedAuthenticationResponse() {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<AuthenticationToken> createSuccessfulAuthenticationResponse(AccountCredentials credentials) {
        AuthenticationToken authenticationToken = createAuthenticationToken(credentials);
        return new ResponseEntity<>(authenticationToken, HttpStatus.OK);
    }
}
