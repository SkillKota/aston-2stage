package homework2.dao;

import homework2.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User create(User user) {
        return executeInTransaction(session -> {
            session.persist(user);
            logger.info("Created user with email={}", user.getEmail());
            return user;
        });
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.find(User.class, id));
        } catch (HibernateException e) {
            logger.error("Failed to find user by id={}", id, e);
            throw e;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User order by id", User.class).list();
        } catch (HibernateException e) {
            logger.error("Failed to find users", e);
            throw e;
        }
    }

    @Override
    public User update(User user) {
        return executeInTransaction(session -> {
            User updated = session.merge(user);
            logger.info("Updated user with id={}", updated.getId());
            return updated;
        });
    }

    @Override
    public boolean deleteById(Long id) {
        return executeInTransaction(session -> {
            User user = session.find(User.class, id);
            if (user == null) {
                return false;
            }
            session.remove(user);
            logger.info("Deleted user with id={}", id);
            return true;
        });
    }

    private <T> T executeInTransaction(TransactionCallback<T> callback) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            T result = callback.execute(session);
            transaction.commit();
            return result;
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Database transaction failed", e);
            throw e;
        }
    }

    @FunctionalInterface
    private interface TransactionCallback<T> {
        T execute(Session session);
    }
}
