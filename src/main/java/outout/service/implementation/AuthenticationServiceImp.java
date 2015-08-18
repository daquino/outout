package outout.service.implementation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import outout.dao.UserDao;
import outout.model.User;
import outout.service.AuthenticationService;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

@Service
public class AuthenticationServiceImp implements AuthenticationService{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${token.secret}")
    private String ts;

    @Autowired
    private UserDao userDao;

    @Override
    public AuthenticationToken getAuthenticationToken(AccountCredentials accountCredentials) throws AuthenticationException {
        User user = userDao.getUser(accountCredentials.getUsername());

        if(isUserAuthenticated(accountCredentials, user)){
            AuthenticationToken authenticationToken = new AuthenticationToken();
            String jsonWebToken = getJsonWebToken(accountCredentials);
            authenticationToken.setToken(jsonWebToken);
            return authenticationToken;
        } else {
            throw new BadCredentialsException("Invalid User");
        }

    }

    private boolean isUserAuthenticated(AccountCredentials accountCredentials, User user) {
        return user != null && passwordEncoder.matches(accountCredentials.getPassword(), user.getPassword());
    }

    private String getJsonWebToken(AccountCredentials accountCredentials) {
        return Jwts.builder().signWith(SignatureAlgorithm.HS512, ts)
                .setSubject(accountCredentials.getUsername())
                .compact();
    }
}
