package outout.integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.model.User;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Controller
@RequestMapping("/account/create")
public class CreateAccountController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder encoder;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Transactional
    public ResponseEntity<Void> createAccount(@RequestBody AccountCredentials accountCredentials) {
        ResponseEntity<Void> response;
        String username = accountCredentials.getUsername();
        String password = accountCredentials.getPassword();
        if(!StringUtils.isEmpty(username) && username.length() > 5
                && !StringUtils.isEmpty(password) && password.length() > 10) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(encoder.encode(password));
            entityManager.persist(user);
            response = new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return response;
    }

}
