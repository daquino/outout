package outout.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.model.User;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Controller
@RequestMapping("/authenticate")
public class AuthenticationController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${token.secret}")
    private String tokenSecret;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthenticationToken> authenticate(@RequestBody AccountCredentials accountCredentials) {
        if(areCredentialsValid(accountCredentials)) {
            AuthenticationToken authenticationToken = new AuthenticationToken();
            authenticationToken.setToken(buildToken(accountCredentials));
            return new ResponseEntity<>(authenticationToken, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<AuthenticationToken>(HttpStatus.NOT_FOUND);
        }
    }

    private boolean areCredentialsValid(final AccountCredentials accountCredentials) {
        User user = findUserByUsername(accountCredentials.getUsername());
        return user != null && passwordEncoder.matches(accountCredentials.getPassword(), user.getPassword());
    }

    private User findUserByUsername(String username) {
        Query query = entityManager.createQuery("select u from User u where u.username = :username");
        query.setParameter("username", username);
        query.setMaxResults(1);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    private String buildToken(final AccountCredentials accountCredentials) {
        return Jwts.builder().signWith(SignatureAlgorithm.HS512, tokenSecret)
                .setSubject(accountCredentials.getUsername())
                .compact();
    }

}
