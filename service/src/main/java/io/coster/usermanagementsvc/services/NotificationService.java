package io.coster.usermanagementsvc.services;

import contract.domain.ForgotPasswordInfo;
import contract.domain.WelcomeInfo;
import io.coster.usermanagementsvc.domain.AuthToken;
import io.coster.usermanagementsvc.domain.User;
import io.coster.usermanagementsvc.repositories.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class NotificationService {

    @Value("frontend.root.url")
    private String frontendRootUrl;

    @Autowired
    private RestTemplate restTemplate;

    private final TokenRepository tokenRepository;
    private final String postRegisterUrl;
    private final String forgotPasswordUrl;

    public NotificationService(TokenRepository tokenRepository,
                               @Value("${notification.service.url}") String notificationServiceUrl) {
        this.tokenRepository = tokenRepository;
        postRegisterUrl = notificationServiceUrl + "/notification/postregister";
        forgotPasswordUrl = notificationServiceUrl + "/notification/forgotpwd";
    }

    public void sendPostRegistrationMessage(User user) {
        WelcomeInfo welcomeInfo = new WelcomeInfo();
        welcomeInfo.setEmailAddress(user.getEmailAddr());
        welcomeInfo.setFirstName(user.getFirstName());
        restTemplate.postForObject(postRegisterUrl, welcomeInfo, ResponseEntity.class);
    }

    public void sendForgotPasswordMessage(User user) {
        ForgotPasswordInfo forgotPasswordInfo = new ForgotPasswordInfo();
        forgotPasswordInfo.setEmailAddress(user.getEmailAddr());
        forgotPasswordInfo.setFirstName(user.getFirstName());

        String url = generatePasswordResetUrl(user.getEmailAddr());
        forgotPasswordInfo.setPasswordResetUrl(url);
        restTemplate.postForObject(forgotPasswordUrl, forgotPasswordInfo, ResponseEntity.class);
    }

    private String generatePasswordResetUrl(String userId) {
        String uuid = UUID.randomUUID().toString();
        String url = frontendRootUrl + "/pwdreset?id=" + userId + "&token=" + uuid;
        AuthToken token =  AuthToken.builder()
                .authToken(uuid)
                .userId(userId)
                .issued(LocalDateTime.now())
                .expiry(LocalDateTime.now().plus(3, ChronoUnit.DAYS))
                .build();
        tokenRepository.saveAndFlush(token);
        return url;
    }

}
