package outout.validator;

import outout.view.AccountCredentials;
import outout.model.User;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

public class AccountCredentialsValidator {

  public static List<String> checkErrors(AccountCredentials accountCredentials, User user) {

    if (!StringUtils.isEmpty(accountCredentials.getUsername()) && accountCredentials.getUsername().length() >= 5
        && !StringUtils.isEmpty(accountCredentials.getPassword()) && accountCredentials.getPassword().length() >= 10
        && user == null) {
      return null;
    } else {
      List<String> errors = new ArrayList<>();
      if(StringUtils.isEmpty(accountCredentials.getUsername()) || accountCredentials.getUsername().length() < 5) {
          errors.add("Username must be at least 5 characters");
      }
      if(StringUtils.isEmpty(accountCredentials.getPassword()) || accountCredentials.getPassword().length() < 10) {
          errors.add("Password must be at least 10 characters");
      }
      if(user != null) {
          errors.add("Username already in use.  Please enter another username.");
      }
      return errors;
    }

  }

}
