package outout.controller;

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
    public ResponseEntity<Void> createAccount(@RequestBody AccountCredentials ac) {
        ResponseEntity<Void> r;
        String u = ac.getUsername();
        String p = ac.getPassword();
        if(!StringUtils.isEmpty(u) && u.length() > 5
                && !StringUtils.isEmpty(p) && p.length() > 10) {
            User user = new User();
            user.setUsername(u);
            user.setPassword(encoder.encode(p));
            entityManager.persist(user);
            r = new ResponseEntity<>(HttpStatus.OK);
        }
        else {
            r = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return r;
    }

}
