package outout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import outout.service.OutoutUserService;
import outout.view.AccountCreationResult;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Controller
@RequestMapping("/account/create")
public class CreateAccountController {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OutoutUserService outoutUserService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AccountCreationResult> createAccount(@RequestBody AccountCredentials ac) {
        AccountCreationResult accountCreationResult =
                outoutUserService.createAccount(ac.getUsername(), ac.getPassword());

        if (accountCreationResult.isSuccessful()) {
            return new ResponseEntity<>(accountCreationResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(accountCreationResult, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
