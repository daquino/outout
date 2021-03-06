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
import outout.view.AccountCreationResult;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<AccountCreationResult> createAccount(@RequestBody AccountCredentials ac) {
        ResponseEntity<AccountCreationResult> r;
        String u = ac.getUsername();
        String p = ac.getPassword();
        Query query = entityManager.createQuery("select count(u) from User u where u.username = :username");
        query.setParameter("username", ac.getUsername());
        Number count = (Number) query.getSingleResult();
        if(!StringUtils.isEmpty(u) && u.length() >= 5
                && !StringUtils.isEmpty(p) && p.length() >= 10
                && count.intValue() == 0) {
            User user = new User();
            user.setUsername(u);
            user.setPassword(encoder.encode(p));
            entityManager.persist(user);
            AccountCreationResult acr = new AccountCreationResult();
            acr.setSuccessful(true);
            r = new ResponseEntity<>(acr, HttpStatus.OK);
        }
        else {
            AccountCreationResult acr = new AccountCreationResult();
            List<String> errors = new ArrayList<>();
            if(StringUtils.isEmpty(u) || u.length() < 5) {
                errors.add("Username must be at least 5 characters");
            }
            if(StringUtils.isEmpty(p) || p.length() < 10) {
                errors.add("Password must be at least 10 characters");
            }
            if(count.intValue() > 0) {
                errors.add("Username already in use.  Please enter another username.");
            }
            acr.setErrors(errors);
            r = new ResponseEntity<>(acr, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return r;
    }

}
