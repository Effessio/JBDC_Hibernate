package ivan.jdbcExample;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageService {

    private final SessionFactory sessionFactory;
    private final MessageDAO messageDAO;

    public MessageService(final SessionFactory sessionFactory, final MessageDAO messageDAO) {
        this.sessionFactory = checkNotNull(sessionFactory);
        this.messageDAO = checkNotNull(messageDAO);
    }

    public void save(final Message message) {
        inTransaction(() -> messageDAO.save(message));
    }

    public Optional<Message> get(final int messageID) {
        return inTransaction(() -> messageDAO.get(messageID));
    }

    public Set<Message> getAll() {
        return inTransaction(messageDAO::getAll);
    }


    public Set<Message> getByUserID(Integer userID) {
        return inTransaction(() -> messageDAO.getByUserID(userID));
    }


    public void update(final Message message) {
        inTransaction(() -> messageDAO.update(message));
    }

    public void changeText(final int messageID, final String text) {
        inTransaction(() -> {
            final Optional<Message> optionalMessage = messageDAO.get(messageID);
            if (!optionalMessage.isPresent()) {
                throw new IllegalArgumentException("there is no user with id " + messageID);
            }
            optionalMessage.get().text(text);
        });
    }

    public void changeUser(final int messageID, final String text, final Integer userID, final UserDAO userDAO) {
        inTransaction(() -> {
            final Optional<Message> optionalMessage = messageDAO.get(messageID);
            if (!optionalMessage.isPresent()) {
                throw new IllegalArgumentException("there is no message with id " + messageID);
            }
            final Optional<User> optionalUser = userDAO.get(userID);
            if (!optionalUser.isPresent()){
                throw new IllegalArgumentException("there is no user with id " + userID);
            }
            optionalMessage.get().setUserID(userID);
        });
    }

    public void delete(final int messageID) {
        inTransaction(() -> messageDAO.delete(messageID));
    }

    private <T> T inTransaction(final Supplier<T> supplier) {
        final Optional<Transaction> transaction = beginTransaction();
        try {
            final T result = supplier.get();
            transaction.ifPresent(Transaction::commit);
            return result;
        } catch (RuntimeException e) {
            transaction.ifPresent(Transaction::rollback);
            throw e;
        }
    }

    private void inTransaction(final Runnable runnable) {
        inTransaction(() -> {
            runnable.run();
            return null;
        });
    }

    private Optional<Transaction> beginTransaction() {
        final Transaction transaction = sessionFactory.getCurrentSession().getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
            return Optional.of(transaction);
        }
        return Optional.empty();
    }
}