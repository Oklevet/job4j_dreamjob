package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VacancyControllerTest {

    private VacancyService vacancyService;

    private CityService cityService;

    private VacancyController vacancyController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actualVacancies).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestVacancyListPageThenGetOneVacancy() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        when(vacancyService.findById(2)).thenReturn(java.util.Optional.of(vacancy2));

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, 2);
        var actualVacancy = model.addAttribute("vacancy");
        actualVacancy = model.addAttribute("cities");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(actualVacancy.getAttribute("vacancy")).isEqualTo(vacancy2);
    }

    @Test
    public void whenSomeExceptionWhenUpdateVacancyThrownThenGetErrorPageWithMessage() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedException = new RuntimeException("Вакансия с указанным идентификатором не найдена");
        when(vacancyService.findById(3)).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = vacancyController.getById(model, 3);
                    var actualExceptionMessage = model.getAttribute("message");
                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenSomeExceptionWhenUpdateThrownThenGetErrorPageWithMessage() throws Exception {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var expectedException = new RuntimeException("Вакансия с указанным идентификатором не найдена");
        when(vacancyService.update(vacancy1, fileDto)).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy1, testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenPostUpdVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancyUpd = new Vacancy(1, "test2", "desc2", now(), false, 2, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancyUpd, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy.getTitle()).isEqualTo(vacancyUpd.getTitle());
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenNotDeleteThrownThenGetErrorPageWithMessage() throws Exception {
        var expectedException = new RuntimeException("Вакансия с указанным идентификатором не найдена");
        when(vacancyService.deleteById(2)).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = vacancyController.delete(model, 2);
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenDeleteVacancyThenGetPageWithVacancies() throws IOException {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy2);
        when(vacancyService.deleteById(1)).thenReturn(true);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, 1);
        var viewAll = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancies).isEqualTo(expectedVacancies);
    }
}