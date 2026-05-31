package homework2;

import homework2.config.HibernateUtil;
import homework2.console.ConsoleMenu;
import homework2.dao.UserDao;
import homework2.dao.UserDaoImpl;
import homework2.service.UserService;
import homework2.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting user-service");

        try {
            UserDao userDao = new UserDaoImpl(HibernateUtil.getSessionFactory());
            UserService userService = new UserServiceImpl(userDao);
            new ConsoleMenu(userService).run();
        } finally {
            HibernateUtil.shutdown();
            logger.info("User-service stopped");
        }
    }
}
