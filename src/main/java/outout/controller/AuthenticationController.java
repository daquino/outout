package outout.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.model.User;
import outout.service.AuthenticationService;
import outout.view.AccountCredentials;
import outout.view.AuthenticationToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Controller
@RequestMapping("/authenticate")
public class AuthenticationController {




    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AuthenticationToken> authenticate(@RequestBody AccountCredentials accountCredentials) {

        try{
            AuthenticationToken authenticationToken = authenticationService.getAuthenticationToken(accountCredentials);

            return new ResponseEntity<>(authenticationToken, HttpStatus.OK);
        }
        catch(BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
