package homework2.console;

import homework2.dao.UserDao;
import homework2.entity.User;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleMenu {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleMenu.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.!#$%&'*+/=?`{|}~^-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private final UserDao userDao;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(UserDao userDao) {
        this.userDao = userDao;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            if (!scanner.hasNextLine()) {
                System.out.println("Поток ввода закрыт");
                return;
            }
            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> printAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестная команда");
                }
            } catch (HibernateException e) {
                System.out.println("Ошибка базы данных: " + e.getMessage());
                logger.error("Database command failed", e);
            } catch (RuntimeException e) {
                System.out.println("Ошибка приложения: " + e.getMessage());
                logger.error("Console command failed", e);
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Сервис пользователей");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по id");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createUser() {
        String name = readRequiredString("Имя: ");
        String email = readEmail("Email: ");
        int age = readPositiveInt("Возраст: ");

        User user = userDao.create(new User(name, email, age));
        System.out.println("Пользователь создан: " + user);
    }

    private void findUserById() {
        long id = readLong("Id пользователя: ");
        Optional<User> user = userDao.findById(id);
        System.out.println(user.map(Object::toString).orElse("Пользователь не найден"));
    }

    private void printAllUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены");
            return;
        }
        users.forEach(System.out::println);
    }

    private void updateUser() {
        long id = readLong("Id пользователя: ");
        Optional<User> existingUser = userDao.findById(id);
        if (existingUser.isEmpty()) {
            System.out.println("Пользователь не найден");
            return;
        }

        User user = existingUser.get();
        user.setName(readRequiredString("Новое имя: "));
        user.setEmail(readEmail("Новый email: "));
        user.setAge(readPositiveInt("Новый возраст: "));

        System.out.println("Пользователь обновлен: " + userDao.update(user));
    }

    private void deleteUser() {
        long id = readLong("Id пользователя: ");
        boolean deleted = userDao.deleteById(id);
        System.out.println(deleted ? "Пользователь удален" : "Пользователь не найден");
    }

    private String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Поток ввода закрыт");
            }
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) {
                return value;
            }
            System.out.println("Значение не может быть пустым");
        }
    }

    private String readEmail(String prompt) {
        while (true) {
            String email = readRequiredString(prompt);
            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            }
            System.out.println("Введите корректный email");
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Поток ввода закрыт");
            }
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Значение должно быть больше 0");
        }
    }

    private long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Поток ввода закрыт");
            }
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите корректный id");
            }
        }
    }
}
