package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Example;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryExampleRepository implements ExampleRepository {
    private static final MemoryExampleRepository INSTANCE = new MemoryExampleRepository();
    private int nextId = 0;
    private final Map<Integer, Example> examples = new HashMap<>();

    private MemoryExampleRepository() {
        save(new Example(0, "http://localhost:8080/example",
                "ExampleController.examples(Model model);"));
        save(new Example(0, "http://localhost:8080/index",
                "IndexController.getIndex();"));
        save(new Example(0, "http://localhost:8080/vacancies",
                "VacancyController.getAll(Model model);"));
        save(new Example(0, "http://localhost:8080/vacancies/create",
                "VacancyController.getCreationPage();"));
        save(new Example(0, "http://localhost:8080/candidates/create",
                "CandidateController.getAll(Model model);"));
        save(new Example(0, "http://localhost:8080/candidates",
                "CandidateController.getCreationPage();"));
    }

    public static MemoryExampleRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Example save(Example example) {
        example.setId(nextId++);
        examples.put(example.getId(), example);
        return example;
    }

    @Override
    public boolean deletedId(int id) {
        return examples.remove(id) != null;
    }

    @Override
    public boolean update(Example example) {
        return examples.computeIfPresent(example.getId(), (id, oldExample) ->
                new Example(oldExample.getId(), example.getLink(), example.getController())) != null;
    }

    @Override
    public Optional<Example> findById(int id) {
        return Optional.ofNullable(examples.get(id));
    }

    @Override
    public Collection<Example> examples() {
        return examples.values();
    }
}
