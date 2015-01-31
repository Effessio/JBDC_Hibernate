package ivan.jdbcExample;

import java.util.Optional;
import java.util.Set;


public interface UserDAO {

    void insert(User user);

    Optional<User> get(int userId);

    Set<User> getAll();

    void update(User user);

    void delete(int userId, MessageService messageService);
}
