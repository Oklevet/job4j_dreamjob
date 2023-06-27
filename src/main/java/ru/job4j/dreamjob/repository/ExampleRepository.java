package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Example;
import java.util.Collection;
import java.util.Optional;

public interface ExampleRepository {
    Example save(Example example);

    boolean deletedId(int id);

    boolean update(Example example);

    Optional<Example> findById(int id);

    Collection<Example> examples();
}
