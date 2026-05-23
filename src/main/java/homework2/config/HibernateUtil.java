package homework2.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (!SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
            configuration.setProperty("hibernate.connection.url", getEnv("DB_URL", "jdbc:postgresql://localhost:5432/user_service"));
            configuration.setProperty("hibernate.connection.username", getEnv("DB_USER", "postgres"));
            configuration.setProperty("hibernate.connection.password", getEnv("DB_PASSWORD", "postgres"));
            return configuration.buildSessionFactory();
        } catch (RuntimeException e) {
            logger.error("Failed to create Hibernate SessionFactory", e);
            throw new IllegalStateException("Failed to initialize Hibernate", e);
        }
    }

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
