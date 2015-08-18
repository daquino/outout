package outout.dao.impl;

import org.springframework.stereotype.Repository;
import outout.dao.UserDao;
import outout.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author smcglathery
 */
@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Number findNumberOfUsersByUsername(String username) {
        Query query = entityManager.createQuery("select count(u) from User u where u.username = :username");
        query.setParameter("username", username);
        return (Number) query.getSingleResult();
    }

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public User findUserByUsername(String username) {
        Query q = entityManager.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", username);
        q.setMaxResults(1);
        List<User> users = q.getResultList();
        if (users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }
}
