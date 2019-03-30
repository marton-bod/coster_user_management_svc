package io.coster.usermanagementsvc.controllers;

import io.coster.usermanagementsvc.contract.AuthenticationResponse;
import io.coster.usermanagementsvc.contract.ErrorResponse;
import io.coster.usermanagementsvc.contract.LoginRequest;
import io.coster.usermanagementsvc.contract.RegistrationRequest;
import io.coster.usermanagementsvc.contract.ValidationRequest;
import io.coster.usermanagementsvc.services.AuthService;
import io.coster.usermanagementsvc.services.exceptions.InvalidCredentials;
import io.coster.usermanagementsvc.services.exceptions.UserAlreadyExists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/validate")
    public AuthenticationResponse validate(@RequestBody @Valid ValidationRequest request) {
        boolean valid = authService.validate(request.getUserId(), request.getAuthToken());
        return AuthenticationResponse.builder()
                .valid(valid)
                .userId(request.getUserId())
                .authToken(request.getAuthToken()).build();
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody @Valid RegistrationRequest request, HttpServletResponse response) {
        String authToken = authService.register(request);
        return AuthenticationResponse.builder()
                .valid(true)
                .userId(request.getEmailAddr())
                .authToken(authToken).build();
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody @Valid LoginRequest request, HttpServletResponse response) {
        String authToken = authService.login(request);
        return AuthenticationResponse.builder()
                .valid(true)
                .userId(request.getEmailAddr())
                .authToken(authToken).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(MethodArgumentNotValidException e) {
        return new ErrorResponse(buildErrorMsgFrom(e.getBindingResult()));
    }

    private String buildErrorMsgFrom(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("\n"));
    }

    @ExceptionHandler(InvalidCredentials.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidCredentials(InvalidCredentials e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExists.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserAlreadyExists(UserAlreadyExists e) {
        return new ErrorResponse(e.getMessage());
    }
}
