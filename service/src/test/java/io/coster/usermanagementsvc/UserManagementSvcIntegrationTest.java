package io.coster.usermanagementsvc;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.coster.usermanagementsvc.contract.AuthenticationResponse;
import io.coster.usermanagementsvc.contract.ErrorResponse;
import io.coster.usermanagementsvc.contract.LoginRequest;
import io.coster.usermanagementsvc.contract.RegistrationRequest;
import io.coster.usermanagementsvc.contract.ValidationRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
public class UserManagementSvcIntegrationTest {

    private static WireMockServer wireMockServer;

    @LocalServerPort
    int port;

	@Autowired
    private TestRestTemplate restTemplate;

    @BeforeClass
    public static void startWireMock()  {
        configureFor("localhost", 10001);
        wireMockServer = new WireMockServer(10001);
        wireMockServer.start();
        stubFor(post(urlEqualTo("/notification/postregister")).willReturn(aResponse()
                .withStatus(200)));
    }

    @Test
	public void registerWithValidData_Receive200() {
        RegistrationRequest request = RegistrationRequest.builder()
                .emailAddr("test@test.com")
                .firstName("Mike")
                .lastName("Anderson")
                .password("test123").build();

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/register", port), request, AuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isTrue();
        assertThat(body.getUserId()).isEqualTo("test@test.com");
        assertThat(body.getAuthToken()).isNotBlank();
    }

    @Test
    public void registerWithEmailThatAlreadyExists_ReceiveBadRequest() {
        RegistrationRequest request = RegistrationRequest.builder()
                .emailAddr("testaccount@test.com")
                .firstName("Mike")
                .lastName("Anderson")
                .password("somethingelse").build();

        ResponseEntity<ErrorResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/register", port), request, ErrorResponse.class);

        assertBadRequestWithErrorMessageContaining(response, "already registered");
    }

    @Test
    public void registerWithInvalidEmail_ReceiveBadRequest() {
        RegistrationRequest request = RegistrationRequest.builder()
                .emailAddr("fdsfd")
                .firstName("Mike")
                .lastName("Anderson")
                .password("test123").build();

        ResponseEntity<ErrorResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/register", port), request, ErrorResponse.class);

        assertBadRequestWithErrorMessageContaining(response, "email.*well-formed");
    }

    @Test
    public void registerWithTooShortPassword_ReceiveBadRequest() {
        RegistrationRequest request = RegistrationRequest.builder()
                .emailAddr("fdsfd")
                .firstName("Mike")
                .lastName("Anderson")
                .password("21").build();

        ResponseEntity<ErrorResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/register", port), request, ErrorResponse.class);

        assertBadRequestWithErrorMessageContaining(response, "email.*well-formed", "password.*length");
    }

    @Test
    public void loginWithValidData_Receive200() {
        // given: user is registered
        RegistrationRequest registerReq = RegistrationRequest.builder()
                .emailAddr("registerfirst@test.com")
                .firstName("Mike")
                .lastName("Anderson")
                .password("$$lookatme").build();
        restTemplate.postForEntity(String.format("http://localhost:%d/auth/register", port), registerReq, AuthenticationResponse.class);

        // when: user tries to log in
        LoginRequest loginReq = LoginRequest.builder()
                .emailAddr("registerfirst@test.com")
                .password("$$lookatme").build();

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/login", port), loginReq, AuthenticationResponse.class);

        // then: it succeeds
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isTrue();
        assertThat(body.getUserId()).isEqualTo("registerfirst@test.com");
        assertThat(body.getAuthToken()).isNotBlank();
    }

    @Test
    public void loginWithNonRegisteredEmail_ReceiveBadRequest() {
        LoginRequest request = LoginRequest.builder()
                .emailAddr("onetwothree@test.com")
                .password("secretive123").build();

        ResponseEntity<ErrorResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/login", port), request, ErrorResponse.class);

        assertBadRequestWithErrorMessageContaining(response, "Email address is not registered.");
    }

    @Test
    public void loginWithRegisteredEmailButPasswordDoesNotMatch_ReceiveBadRequest() {
        LoginRequest request = LoginRequest.builder()
                .emailAddr("testaccount@test.com")
                .password("fakepwd").build();

        ResponseEntity<ErrorResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/login", port), request, ErrorResponse.class);

        assertBadRequestWithErrorMessageContaining(response, "Password provided is incorrect.");
    }

    private void assertBadRequestWithErrorMessageContaining(ResponseEntity<ErrorResponse> response, String... errorMessages) {
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        for (String errorMsg : errorMessages) {
            assertThat(body.getErrorMsg()).containsPattern(errorMsg);
        }
    }

    @Test
    public void validateValidCredentials_ReceiveTrueForValidity() {
        ValidationRequest request = new ValidationRequest("testaccount@test.com", "abcd-efgh1000");

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/validate", port), request, AuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isTrue();
        assertThat(body.getUserId()).isEqualTo("testaccount@test.com");
        assertThat(body.getAuthToken()).isEqualTo("abcd-efgh1000");
    }

    @Test
    public void validateExpiredToken_ReceiveFalseForValidity() {
        ValidationRequest request = new ValidationRequest("testaccount2@test.com", "xxxyyyzzz50");

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/validate", port), request, AuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isFalse();
    }

    @Test
    public void validateNonExistingToken_ReceiveFalseForValidity() {
        ValidationRequest request = new ValidationRequest("testaccount2@test.com", "abfgggf");

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/validate", port), request, AuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isFalse();
    }

    @Test
    public void validateNonExistingUser_ReceiveFalseForValidity() {
        ValidationRequest request = new ValidationRequest("testaccountFAKE@test.com", "abcd-efgh1000");

        ResponseEntity<AuthenticationResponse> response
                = restTemplate.postForEntity(String.format("http://localhost:%d/auth/validate", port), request, AuthenticationResponse.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        AuthenticationResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.isValid()).isFalse();
    }


}

