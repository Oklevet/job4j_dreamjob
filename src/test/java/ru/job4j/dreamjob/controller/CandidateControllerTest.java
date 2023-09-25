package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;

    private CityService cityService;

    private CandidateController candidateController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var candidate1 = new Candidate(1, "name1", "desc1", "worker", 1000, 2);
        var candidate2 = new Candidate(2, "name2", "desc2", "worker", 3000, 4);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCandidates = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidates).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var candidate = new Candidate(1, "name1", "desc1", "worker", 1000, 2);
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        when(candidateService.save(candidateArgumentCaptor.capture())).thenReturn(candidate);

        var view = candidateController.create(candidate);
        var actualCandidate = candidateArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any())).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = candidateController.create(new Candidate());
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenRequestVacancyListPageThenGetOneVacancy() {
        var candidate2 = new Candidate(2, "name2", "desc2", "worker", 3000, 4);
        when(candidateService.findById(2)).thenReturn(java.util.Optional.of(candidate2));

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 2);
        var actualCandidates = model.addAttribute("candidate");
        actualCandidates = model.addAttribute("cities");

        assertThat(view).isEqualTo("candidates/candidate");
        assertThat(actualCandidates.getAttribute("candidate")).isEqualTo(candidate2);
    }

    @Test
    public void whenSomeExceptionWhenUpdateVacancyThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.findById(3)).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = candidateController.getById(model, 3);
                    var actualExceptionMessage = model.getAttribute("message");
                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenSomeExceptionWhenUpdateThrownThenGetErrorPageWithMessage() throws Exception {
        var candidate = new Candidate(1, "name1", "desc1", "worker", 3000, 4);
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.update(candidate)).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = candidateController.update(candidate, model);
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenPostUpdVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var candidate1 = new Candidate(1, "name1", "desc1", "worker", 1000, 2);
        var candidateUpd = new Candidate(2, "name2", "desc2", "worker", 3000, 4);
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        when(candidateService.update(candidateArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidateUpd, model);
        var actualCandidate = candidateArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate.getName()).isEqualTo(candidateUpd.getName());
    }

    @Test
    public void whenNotDeleteThrownThenGetErrorPageWithMessage() throws Exception {
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.deletedId(2)).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = candidateController.delete(model, 2);
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenDeleteVacancyThenGetPageWithVacancies() throws IOException {
        var candidate1 = new Candidate(1, "name1", "desc1", "worker", 1000, 2);
        var candidate2 = new Candidate(2, "name2", "desc2", "worker", 3000, 4);
        var expectedCandidates = List.of(candidate2);
        when(candidateService.deletedId(1)).thenReturn(true);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);
        var viewAll = candidateController.getAll(model);
        var actualVacancies = model.getAttribute("candidates");

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualVacancies).isEqualTo(expectedCandidates);
    }
}