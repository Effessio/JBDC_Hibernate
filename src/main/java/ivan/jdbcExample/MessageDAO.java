package ivan.jdbcExample;

import com.google.common.collect.ImmutableSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageDAO {

    private final SessionFactory sessionFactory;

    public MessageDAO(final SessionFactory sessionFactory) {
        this.sessionFactory = checkNotNull(sessionFactory);
    }

    public void save(final Message message) {

        if (message.id() != 0 ) {
            throw new IllegalArgumentException("can not save " + message + " with assigned id");
        }
        if (message.userID() == 0){
            throw new IllegalArgumentException("can not save" + message + "not assigned to user");
        }
        session().save(message);
    }

    public Optional<Message> get(final int messageID) {
        final Message Message = (Message) session().get(Message.class, messageID);
        return Optional.ofNullable(Message);
    }

    public Set<Message> getAll() {
        final Criteria criteria = session().createCriteria(Message.class);
        @SuppressWarnings("unchecked")
        final List<Message> users = criteria.list();
        return ImmutableSet.copyOf(users);
    }

    public Set<Message> getByUserID(Integer userID) {
        final Criteria criteria = session().createCriteria(Message.class);
        criteria.add(Restrictions.eq("userID", userID));
        @SuppressWarnings("unchecked")
        final List<Message> users = criteria.list();
        return ImmutableSet.copyOf(users);
    }

    public void update(final Message message) {
        session().update(message);
    }

    public void delete(final int messageID) {
        session().createQuery("DELETE Message WHERE id = :id") // HQL
                .setInteger("id", messageID)
                .executeUpdate();
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }
}

