package ru.alexefremov.depositapp.depositservice.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import ru.alexefremov.depositapp.depositservice.dto.*;
import ru.alexefremov.depositapp.depositservice.entity.AccountEntity;
import ru.alexefremov.depositapp.depositservice.entity.EmailDataEntity;
import ru.alexefremov.depositapp.depositservice.repository.AccountEntityRepository;
import ru.alexefremov.depositapp.depositservice.repository.EmailDataEntityRepository;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AccountEntityRepository accountsRepository;
    @Autowired
    private EmailDataEntityRepository emailsRepository;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:14-alpine"
    );

    static ElasticsearchContainer elastic = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.17")
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        elastic.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        elastic.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.elasticsearch.uris", () -> "http://" + elastic.getHttpHostAddress());
    }

    @Order(1)
    @Test
    void shouldTransferMoneyFromOneAccountToTheOther() {
        Optional<AccountEntity> account1 = accountsRepository.findById(1L);
        Optional<AccountEntity> account2 = accountsRepository.findById(2L);
        assertTrue(account1.isPresent());
        assertTrue(account2.isPresent());

        var account1Balance = account1.get().getBalance();
        var account2Balance = account2.get().getBalance();

        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        var transferAmount = new BigDecimal("10");

        HttpEntity<TransferRequestDto> request = new HttpEntity<>(TransferRequestDto.builder()
                .value(transferAmount).build(), headers);

        restTemplate.exchange("/users/2/transfer", HttpMethod.POST, request, AccountDto.class);


        account1 = accountsRepository.findById(1L);
        account2 = accountsRepository.findById(2L);

        assertTrue(account1.isPresent());
        assertTrue(account2.isPresent());

        var account1NewBalance = account1.get().getBalance();
        var account2NewBalance = account2.get().getBalance();

        assertEquals(account1NewBalance, account1Balance.subtract(transferAmount));
        assertEquals(account2NewBalance, account2Balance.add(transferAmount));
    }

    @Order(2)
    @Test
    void shouldNotTransferMoneyFromOneAccountToTheOtherIfInsufficientFunds() {
        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        var transferAmount = new BigDecimal("1000");

        HttpEntity<TransferRequestDto> request = new HttpEntity<>(TransferRequestDto.builder()
                .value(transferAmount).build(), headers);

        ResponseEntity<ErrorDto> response = restTemplate.exchange("/users/2/transfer", HttpMethod.POST, request, ErrorDto.class);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Can't transfer amount of 1000. Insufficient funds", response.getBody().getDescription());
    }

    @Order(3)
    @Test
    void shouldNotTransferNegativeAmount() {
        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        var transferAmount = new BigDecimal("-1");

        HttpEntity<TransferRequestDto> request = new HttpEntity<>(TransferRequestDto.builder()
                .value(transferAmount).build(), headers);

        ResponseEntity<ErrorDto> response = restTemplate.exchange("/users/2/transfer", HttpMethod.POST, request, ErrorDto.class);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wrong input", response.getBody().getDescription());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Rejected field: value. Rejected value: -1. Reason: must be greater than 0", response.getBody().getDetails().get(0));
    }

    @Order(4)
    @Test
    void shouldAddEmail() {
        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<AddChangeEmailRequest> request = new HttpEntity<>(AddChangeEmailRequest.builder()
                .email("test@mail.com").build(), headers);

        ResponseEntity<EmailDto> response = restTemplate.exchange("/users/1/emails", HttpMethod.POST, request, EmailDto.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@mail.com", response.getBody().getEmail());

        Optional<EmailDataEntity> email = emailsRepository.findByEmail("test@mail.com");
        assertTrue(email.isPresent());
        assertEquals(1, email.get().getUser().getId());
    }

    @Order(4)
    @Test
    void shouldNotAddEmailForOtherUsers() {
        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<AddChangeEmailRequest> request = new HttpEntity<>(AddChangeEmailRequest.builder()
                .email("test@mail.com").build(), headers);

        ResponseEntity<ErrorDto> response = restTemplate.exchange("/users/2/emails", HttpMethod.POST, request, ErrorDto.class);


        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Access denied", response.getBody().getDescription());
    }

    @Order(5)
    @Test
    void shouldNotAddEmailWithWrongFormat() {
        String token = authenticate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        HttpEntity<AddChangeEmailRequest> request = new HttpEntity<>(AddChangeEmailRequest.builder()
                .email("not_email").build(), headers);

        ResponseEntity<ErrorDto> response = restTemplate.exchange("/users/1/emails", HttpMethod.POST, request, ErrorDto.class);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wrong input", response.getBody().getDescription());
        assertNotNull(response.getBody().getDetails());
        assertEquals(1, response.getBody().getDetails().size());
        assertEquals("Rejected field: email. Rejected value: not_email. Reason: must be a well-formed email address", response.getBody().getDetails().get(0));
    }

    private String authenticate() {
        HttpEntity<LoginDto> request = new HttpEntity<>(LoginDto.builder().username("john@mail.com").password("password").build());
        ResponseEntity<TokenDto> response = restTemplate.exchange("/auth", HttpMethod.POST, request, TokenDto.class);
        return Objects.requireNonNull(response.getBody()).getToken();
    }

}