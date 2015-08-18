package outout.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import outout.dao.UserDao;
import outout.model.User;
import outout.service.OutoutUserService;
import outout.view.AccountCreationResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author smcglathery
 */

@Service
public class OutoutUserServiceImpl implements OutoutUserService{

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    @Transactional
    public AccountCreationResult createAccount(String username, String password) {
        AccountCreationResult accountCreationResult = new AccountCreationResult();
        List<String> errors = validateAccount(username, password);
        if (errors.isEmpty()) {
            createAndSaveUser(username, password);
            accountCreationResult.setSuccessful(true);
        } else {
            accountCreationResult.setErrors(errors);
            accountCreationResult.setSuccessful(false);
        }
        return accountCreationResult;
    }

    private void createAndSaveUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        userDao.saveUser(user);
    }

    private List<String> validateAccount(String username, String password) {
        List<String> errors = new ArrayList<String>();
        if(StringUtils.isEmpty(username) || username.length() < 5) {
            errors.add("Username must be at least 5 characters");
        }
        if(StringUtils.isEmpty(password) || password.length() < 10) {
            errors.add("Password must be at least 10 characters");
        }
        if(doesUserExist(username)) {
            errors.add("Username already in use.  Please enter another username.");
        }
        return errors;
    }

    private boolean doesUserExist(String username) {
        Number count = userDao.findNumberOfUsersByUsername(username);
        if (count.intValue() > 0) {
            return true;
        }

        return false;
    }
}
