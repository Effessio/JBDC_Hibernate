package ivan.jdbcExample;

import org.hibernate.SessionFactory;

import java.sql.*;
import java.util.Optional;
import java.util.Set;

public class Main {
    private static final String username = "root";
    private static final String password = "";
    private static final String url = "jdbc:mysql://localhost:3306/test";
    public static void main(String[] args) throws SQLException {
        final SessionFactory sessionFactory = getSessionFactory();
        final MessageService messageService = getMessageService(sessionFactory);
        UserDAO userDAO = new JDBCUserDAO(Main.url, username, password);
        play(userDAO, messageService);

    }

    private static void play(final UserDAO userDAO, final MessageService messageService) {

        User user1 = User.create("Ivan");
        userDAO.insert(user1);
        System.out.println("First user inserted");
        System.out.println(user1);
        User user2 = User.create("Alex");
        userDAO.insert(user2);
        System.out.println("Second user inserted");
        Set<User> allUsers = userDAO.getAll();
        System.out.println(allUsers);
        System.out.println("Now len's try to get user by id");
        Integer user1id = user1.getId();
        Optional<User> user3 = userDAO.get(user1id);
        if (user3.isPresent()){
            System.out.println(user3.get());
        }
        System.out.print("Now let's delete one user");
        System.out.print("Number of users before deletion" + userDAO.getAll().size());
        userDAO.delete(user1id, messageService);
        System.out.println("Number of users after deletion" + userDAO.getAll().size());
        System.out.println("Now let's play with messages");
        Message message1 = new Message("Hello", user2.getId());
        messageService.save(message1);
        System.out.println(message1);
        System.out.println("Updating this message");
        Integer message1ID = message1.id();
        message1.text("Goodbye");
        messageService.update(message1);
        messageService.get(message1ID);
        System.out.println(message1);
        System.out.println("Now creating one more user and 5 message from her");
        User user4 = User.create("Olga");
        userDAO.insert(user4);
        for (Integer i=1; i<6;  i++){
            String message_text = String.format("Text of message %d", i);
            messageService.save(new Message(message_text, user4.getId()));
        }
        System.out.println(messageService.getAll());
        System.out.println("Retrieving messages from that user");
        System.out.println(messageService.getByUserID(user4.getId()));
        System.out.println("And finally let's delete user and see what we have in database");
        userDAO.delete(user4.getId(), messageService);
        System.out.println(userDAO.getAll());
        System.out.println(messageService.getAll());

    }

    private static SessionFactory getSessionFactory() {
        return HibernateConfig.prod().buildSessionFactory();
    }

    private static MessageService getMessageService(final SessionFactory sessionFactory) {

        final MessageDAO messageDAO = new MessageDAO(sessionFactory);
        return new MessageService(sessionFactory, messageDAO);
    }
}
