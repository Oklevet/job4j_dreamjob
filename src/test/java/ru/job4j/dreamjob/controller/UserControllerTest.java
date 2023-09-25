package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegisterPageThenGetPageWithRegister() {
        when(userService.findAll()).thenReturn(null);

        var model = new ConcurrentModel();
        var view = userController.getRegistrationPage(model);
        var actualUsers = model.getAttribute("users");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualUsers).isEqualTo(null);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws Exception {
        var user = new User(1, "e@gmail.com", "name", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(java.util.Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(model, user);
        var actualUser = userArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Пользователь с такой почтой уже существует");
        when(userService.save(any())).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    var model = new ConcurrentModel();
                    var view = userController.register(model, new User());
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("errors/404");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }

    @Test
    public void whenRequestLoginPageThenGetPageWithLogin() {
        when(userService.findAll()).thenReturn(null);

        var model = new ConcurrentModel();
        var view = userController.getLoginPage(model);
        var actualUsers = model.getAttribute("users");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualUsers).isEqualTo(null);
    }

    @Test
    public void whenPostLoginUserThenSameDataAndRedirectToVacanciesPage() {
        var user = new User(1, "e@gmail.com", "name", "password");
        var expectedException = new RuntimeException("Почта или пароль введены неверно");
        when(userService.findByEmailAndPassword(any(), any())).thenThrow(expectedException);

        assertThrows(
                RuntimeException.class,
                () -> {
                    HttpServletRequest http = null;
                    var model = new ConcurrentModel();
                    var view = userController.loginUser(user, model, http);
                    var actualExceptionMessage = model.getAttribute("message");

                    assertThat(view).isEqualTo("users/login");
                    assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
                }
        );
    }
}