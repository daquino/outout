package outout.service;

import org.springframework.stereotype.Service;
import outout.model.User;
import outout.view.AccountCredentials;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User findUserFromCredentials(AccountCredentials credentials) {
        Query q = entityManager.createQuery("select u from User u where u.username = :username");
        q.setParameter("username", credentials.getUsername());
        q.setMaxResults(1);
        List<User> uList = q.getResultList();
        return uList.isEmpty() ? null : uList.get(0);
    }
}
