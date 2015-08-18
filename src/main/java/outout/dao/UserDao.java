package outout.dao;

import outout.model.User;

/**
 * @author smcglathery
 */
public interface UserDao {
    Number findNumberOfUsersByUsername(String username);

    void saveUser(User user);

    User findUserByUsername(String username);
}
