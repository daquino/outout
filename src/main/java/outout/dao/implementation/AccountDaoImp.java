package outout.dao.implementation;

import outout.dao.AccountDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class AccountDaoImp implements AccountDao{

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Boolean doesUserExist(String username) {
        Query query = entityManager.createQuery("select count(u) from User u where u.username = :username");
        query.setParameter("username", username);
        Number count = (Number) query.getSingleResult();

        return count.intValue()==0;
    }
}
