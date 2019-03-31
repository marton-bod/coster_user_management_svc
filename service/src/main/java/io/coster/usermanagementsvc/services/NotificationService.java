package io.coster.usermanagementsvc.services;

import contract.domain.WelcomeInfo;
import io.coster.usermanagementsvc.contract.AuthenticationResponse;
import io.coster.usermanagementsvc.contract.ValidationRequest;
import io.coster.usermanagementsvc.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    private final String postRegisterUrl;
    private final String forgotPasswordUrl;

    public NotificationService(@Value("${notification.service.url}") String notificationServiceUrl) {
        postRegisterUrl = notificationServiceUrl + "/notification/postregister";
        forgotPasswordUrl = notificationServiceUrl + "/notification/forgotpwd";
    }

    public void sendPostRegistrationMessage(User user) {
        WelcomeInfo welcomeInfo = new WelcomeInfo();
        welcomeInfo.setEmailAddress(user.getEmailAddr());
        welcomeInfo.setFirstName(user.getFirstName());
        restTemplate.postForObject(postRegisterUrl, welcomeInfo, ResponseEntity.class);
    }

}
