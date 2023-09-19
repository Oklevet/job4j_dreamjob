package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Optional<User> save(User user);

    Optional<User> findByEmailAndPassword(String email, String password);

    boolean deleteByEmailAndPassword(String email, String password);

    Collection<User> findAll();
}
