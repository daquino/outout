package outout.service;

import outout.view.AccountCredentials;
import outout.model.User;
import outout.view.AccountCreationResult;
import outout.view.AuthenticationToken;

public interface UserService {

  public AccountCreationResult createUser(AccountCredentials accountCredentials);

  public AuthenticationToken authenticate(AccountCredentials accountCredentials);

}
