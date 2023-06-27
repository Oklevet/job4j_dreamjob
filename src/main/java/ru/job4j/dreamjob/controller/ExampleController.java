package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.repository.ExampleRepository;
import ru.job4j.dreamjob.repository.MemoryExampleRepository;

@Controller
@RequestMapping("/examples")
public class ExampleController {
    private final ExampleRepository exampleRepository = MemoryExampleRepository.getInstance();

    @GetMapping
    public String examples(Model model) {
        model.addAttribute("examples", exampleRepository.examples());
        return "example";
    }
}
