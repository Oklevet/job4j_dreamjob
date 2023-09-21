package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteByEmailAndPassword(user.getEmail(), user.getPassword());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(
                new User(0, "q@gmail.com", "name", "password")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository
                .save(new User(0, "q1@gmail.com", "name", "password")).get();
        var user2 = sql2oUserRepository
                .save(new User(0, "q2@gmail.com", "name", "password")).get();
        var user3 = sql2oUserRepository
                .save(new User(0, "q3@gmail.com", "name", "password")).get();
        var result = sql2oUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findByEmailAndPassword("q@gmail.com", "password"))
                .isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var user = sql2oUserRepository
                .save(new User(0, "q@gmail.com", "name", "password")).get();
        var isDeleted = sql2oUserRepository.deleteByEmailAndPassword(user.getEmail(), user.getPassword());
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteByEmailAndPassword("q@gmail.com", "password")).isFalse();
    }

    @Test
    public void whenSaveSimilarEmailsThenSavedOnlyFirst() {
        var user1 = sql2oUserRepository
                .save(new User(0, "q@gmail.com", "name", "password")).get();
        var user2 = sql2oUserRepository
                .save(new User(1, "q@gmail.com", "name", "password")).orElse(null);
        var savedUser = sql2oUserRepository.findByEmailAndPassword("q@gmail.com", "password").get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user1);
        assertThat(savedUser).usingRecursiveComparison().isNotEqualTo(user2);
    }

    @Test
    public void whenSaveSimilarEmailsThenGetErrText() {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        var user1 = sql2oUserRepository
                .save(new User(0, "q@gmail.com", "name", "password")).get();
        var user2 = sql2oUserRepository
                .save(new User(1, "q@gmail.com", "name", "password")).orElse(null);
        assertThat(("Пользователь с таким почтовым адресом уже зарегистрирован").contains(outContent.toString()));
    }
}