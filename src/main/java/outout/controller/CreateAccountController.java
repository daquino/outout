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
import outout.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/account/create")
public class CreateAccountController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AccountCreationResult> createAccount(@RequestBody AccountCredentials ac) {
      AccountCreationResult acr = this.userService.createUser(ac);
      if (acr.getErrors() != null) {
        return new ResponseEntity<>(acr, HttpStatus.UNPROCESSABLE_ENTITY);
      } else {
        return new ResponseEntity<>(acr, HttpStatus.OK);
      }
    }

}
