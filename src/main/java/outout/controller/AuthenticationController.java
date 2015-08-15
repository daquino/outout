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
    private EntityManager em;

    @Autowired
    private PasswordEncoder pe;

    @Value("${token.secret}")
    private String ts;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthenticationToken> authenticate(@RequestBody AccountCredentials ac) {
        Query q = em.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", ac.getUsername());
        q.setMaxResults(1);
        List<User> uList = q.getResultList();
        User user = uList.isEmpty() ? null : uList.get(0);
        if(user != null && pe.matches(ac.getPassword(), user.getPassword())) {
            AuthenticationToken authenticationToken = new AuthenticationToken();
            String jwt = Jwts.builder().signWith(SignatureAlgorithm.HS512, ts)
                    .setSubject(ac.getUsername())
                    .compact();
            authenticationToken.setToken(jwt);
            return new ResponseEntity<>(authenticationToken, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
