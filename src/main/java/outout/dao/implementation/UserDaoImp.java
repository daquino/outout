package outout.dao.implementation;

import org.springframework.stereotype.Repository;
import outout.dao.UserDao;
import outout.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class UserDaoImp implements UserDao {
    @PersistenceContext
    private EntityManager entityManager;

    public User getUser(String username){
        Query query = getQuery(username);
        List<User> users = query.getResultList();
        User user = users.isEmpty() ? null : users.get(0);
        return user;
    }

    private Query getQuery(String username) {
        Query query = entityManager.createQuery("select u from User u where u.username = :username");
        query.setParameter("username", username);
        query.setMaxResults(1);
        return query;
    }
}
