package io.coster.usermanagementsvc.controllers;

import io.coster.usermanagementsvc.contract.AuthenticationResponse;
import io.coster.usermanagementsvc.contract.ErrorResponse;
import io.coster.usermanagementsvc.contract.LoginRequest;
import io.coster.usermanagementsvc.contract.PasswordResetRequest;
import io.coster.usermanagementsvc.contract.RegistrationRequest;
import io.coster.usermanagementsvc.contract.ValidationRequest;
import io.coster.usermanagementsvc.domain.User;
import io.coster.usermanagementsvc.services.AuthService;
import io.coster.usermanagementsvc.services.NotificationService;
import io.coster.usermanagementsvc.services.exceptions.InvalidCredentials;
import io.coster.usermanagementsvc.services.exceptions.UserAlreadyExists;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final NotificationService notificationService;

    @PostMapping("/validate")
    public AuthenticationResponse validate(@RequestBody @Valid ValidationRequest request) {
        boolean valid = authService.validate(request.getUserId(), request.getAuthToken());
        return AuthenticationResponse.builder()
                .valid(valid)
                .userId(request.getUserId())
                .authToken(request.getAuthToken()).build();
    }

    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody @Valid RegistrationRequest request) {
        String authToken = authService.register(request);
        return AuthenticationResponse.builder()
                .valid(true)
                .userId(request.getEmailAddr())
                .authToken(authToken).build();
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody @Valid LoginRequest request) {
        String authToken = authService.login(request);
        return AuthenticationResponse.builder()
                .valid(true)
                .userId(request.getEmailAddr())
                .authToken(authToken).build();
    }

    @GetMapping("/forgotpwd")
    public ResponseEntity forgotPassword(@RequestParam("id") String emailAddr) {
        Optional<User> optUser = authService.doesUserExist(emailAddr);
        optUser.ifPresent(notificationService::sendForgotPasswordMessage);
        return optUser.isPresent() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/pwdreset")
    public AuthenticationResponse passwordReset(@RequestBody @Valid PasswordResetRequest request) {
        String authToken = authService.resetPassword(request);
        return AuthenticationResponse.builder()
                .valid(true)
                .userId(request.getUserId())
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
