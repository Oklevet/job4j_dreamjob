package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.User;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Collection<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetch(User.class);
        }
    }

    @Override
    public boolean deleteByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users WHERE email = :email and password"
                    + " = :password");
            query.addParameter("email", email);
            query.addParameter("password", password);
            var deletedRows = query.executeUpdate().getResult();
            return deletedRows > 0;
        }
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> optionalUser = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                      INSERT INTO users(email, name, password) VALUES (:email, :name, :password)""";
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            optionalUser = Optional.of(user);
        } catch (Exception e) {
            LOG.error("Добавление пользователя с повторяющимся почтовым адресом " + user.getEmail());
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email and password"
                    + " = :password");
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user = query.setColumnMappings(User.COLUMN_MAPPING).executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }
}
