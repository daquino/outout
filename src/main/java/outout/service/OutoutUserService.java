package outout.service;

import outout.view.AccountCreationResult;

/**
 * @author smcglathery
 */
public interface OutoutUserService {
    AccountCreationResult createAccount (String username, String password);
}
