package outout.service;

import outout.model.User;
import outout.view.AccountCredentials;

public interface UserService {
    User findUserFromCredentials(AccountCredentials credentials);
}
