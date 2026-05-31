package homework2.dao;

import homework2.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class UserDaoImplTest {
    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("user_service_test")
            .withUsername("postgres")
            .withPassword("postgres");

    private static SessionFactory sessionFactory;
    private static UserDao userDao;

    @BeforeAll
    static void setUp() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl())
                .setProperty("hibernate.connection.username", POSTGRES.getUsername())
                .setProperty("hibernate.connection.password", POSTGRES.getPassword())
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "true")
                .buildSessionFactory();
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Test
    void createShouldSaveUser() {
        User createdUser = userDao.create(new User("Иван", "ivan@example.com", 25));

        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getCreatedAt());

        Optional<User> foundUser = userDao.findById(createdUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Иван", foundUser.get().getName());
        assertEquals("ivan@example.com", foundUser.get().getEmail());
        assertEquals(25, foundUser.get().getAge());
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserDoesNotExist() {
        Optional<User> user = userDao.findById(1000L);

        assertFalse(user.isPresent());
    }

    @Test
    void findAllShouldReturnUsersOrderedById() {
        User firstUser = userDao.create(new User("Иван", "ivan@example.com", 25));
        User secondUser = userDao.create(new User("Мария", "maria@example.com", 30));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertEquals(firstUser.getId(), users.get(0).getId());
        assertEquals(secondUser.getId(), users.get(1).getId());
    }

    @Test
    void updateShouldChangeExistingUser() {
        User user = userDao.create(new User("Иван", "ivan@example.com", 25));
        user.setName("Петр");
        user.setEmail("petr@example.com");
        user.setAge(31);

        User updatedUser = userDao.update(user);

        assertEquals(user.getId(), updatedUser.getId());
        Optional<User> foundUser = userDao.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Петр", foundUser.get().getName());
        assertEquals("petr@example.com", foundUser.get().getEmail());
        assertEquals(31, foundUser.get().getAge());
    }

    @Test
    void deleteByIdShouldRemoveExistingUser() {
        User user = userDao.create(new User("Иван", "ivan@example.com", 25));

        boolean deleted = userDao.deleteById(user.getId());

        assertTrue(deleted);
        assertFalse(userDao.findById(user.getId()).isPresent());
    }

    @Test
    void deleteByIdShouldReturnFalseWhenUserDoesNotExist() {
        boolean deleted = userDao.deleteById(1000L);

        assertFalse(deleted);
    }

    @Test
    void methodsShouldRejectNullArguments() {
        assertThrows(NullPointerException.class, () -> new UserDaoImpl(null));
        assertThrows(NullPointerException.class, () -> userDao.create(null));
        assertThrows(NullPointerException.class, () -> userDao.findById(null));
        assertThrows(NullPointerException.class, () -> userDao.update(null));
        assertThrows(NullPointerException.class, () -> userDao.deleteById(null));
    }
}
