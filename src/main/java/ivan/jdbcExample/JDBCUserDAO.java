package ivan.jdbcExample;

import com.google.common.collect.ImmutableSet;
import com.mysql.fabric.jdbc.FabricMySQLDriver;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.Set;


public class JDBCUserDAO implements UserDAO {

    private final String username;
    private final String password;
    private final String url;

    public JDBCUserDAO(String url, String username, String password) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void insert(final User user) {

        if (user.getId() != 0) {
            throw new IllegalArgumentException("can not save " + user + " with already assigned id");
        }

        try (final Connection connection = DriverManager.getConnection(url, username, password)) {

            final String query = "INSERT INTO users (name) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(query,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.execute();
            try (final ResultSet generatedKeys = statement.getGeneratedKeys()) {
                generatedKeys.next();
                user.setId(generatedKeys.getInt(1));  // problem: what if user is already in some set?
            }

        } catch (SQLException e) {
            throw new RuntimeException("failed to persist " + user, e);
        }
    }

    @Override
    public Optional<User> get(int userId) {
        try(final Connection connection = DriverManager.getConnection(url, username, password)){
            final String query = "SELECT * FROM users WHERE id = ?";
            final PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            try (final ResultSet resultSet = statement.executeQuery()) {
                final boolean userExists = resultSet.next();
                if (!userExists) {
                    return Optional.empty();
                }
                User result = User.create(resultSet.getString("name"));
                result.setId(resultSet.getInt("id"));
                return Optional.of(result);
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to get user by id " + userId, e);
        }
    }

    @Override
    public Set<User> getAll() {
        try (final Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "select * from users";
            final PreparedStatement statement = connection.prepareStatement(query);
            try (final ResultSet resultSet = statement.executeQuery(query)) {

                final ImmutableSet.Builder<User> usersBuilder = ImmutableSet.<User>builder();
                while (resultSet.next()) {
                    final User user = User.create(resultSet.getString("name"));
                    user.setId(resultSet.getInt("id"));
                    usersBuilder.add(user);
                }
                return usersBuilder.build();
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to get users", e);
        }
    }

    @Override
    public void update(User user) {
        try (final Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "UPDATE users SET name = ? where id = ?";
            final PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getName());
            statement.setInt(2, user.getId());
            statement.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int userId, MessageService messageService) {
            try (final Connection connection = DriverManager.getConnection(url, username, password)) {
                Set<Message> messageSet= messageService.getByUserID(userId);
                for (Message message: messageSet){
                    messageService.delete(message.id());
                }
                String query = "DELETE FROM users where id = ?";
                final PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                statement.execute();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
    }
}

