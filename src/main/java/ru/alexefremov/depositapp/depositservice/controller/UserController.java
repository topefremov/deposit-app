package ru.alexefremov.depositapp.depositservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.dto.*;
import ru.alexefremov.depositapp.depositservice.search.SearchFilter;
import ru.alexefremov.depositapp.depositservice.search.UserData;
import ru.alexefremov.depositapp.depositservice.service.UserFacade;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@SecurityRequirement(name = "token")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDataDto>> search(@Valid @ParameterObject SearchRequestDto searchRequestDto,
                                                    @ParameterObject Pageable pageable) {
        var searchFilter = SearchFilter.builder()
                .name(searchRequestDto.getName())
                .email(searchRequestDto.getEmail())
                .phone(searchRequestDto.getPhone())
                .dateOfBirth(searchRequestDto.getDateOfBirth())
                .pageable(pageable)
                .build();
        SearchPage<UserData> search = userFacade.search(searchFilter);
        Page<UserDataDto> result = search.map(userDataSearchHit -> getUserDataDto(userDataSearchHit.getContent()));
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "{userId}/phones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhoneDto> addPhone(@PathVariable("userId") Long userId, @Valid @RequestBody AddChangePhoneRequest request) {
        Phone phone = userFacade.addPhone(userId, request.getNumber());
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @PutMapping(value = "{userId}/phones/{phoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhoneDto> changePhone(@PathVariable("userId") Long userId,
                                                @PathVariable("phoneId") Long phoneId,
                                                @Valid @RequestBody AddChangePhoneRequest request) {
        Phone phone = userFacade.changePhone(userId, phoneId, request.getNumber());
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @DeleteMapping(value = "{userId}/phones/{phoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PhoneDto> deletePhone(@PathVariable("userId") Long userId, @PathVariable("phoneId") Long phoneId) {
        Phone phone = userFacade.deletePhone(userId, phoneId);
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @PostMapping(value = "{userId}/emails", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailDto> addEmail(@PathVariable("userId") Long userId,
                                             @Valid @RequestBody AddChangeEmailRequest request) {
        Email email = userFacade.addEmail(userId, request.getEmail());
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @PutMapping(value = "{userId}/emails/{emailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailDto> changeEmail(@PathVariable("userId") Long userId,
                                                @PathVariable("emailId") Long emailId,
                                                @Valid @RequestBody AddChangeEmailRequest request) {
        Email email = userFacade.changeEmail(userId, emailId, request.getEmail());
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @DeleteMapping(value = "{userId}/emails/{emailId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailDto> deleteEmail(@PathVariable("userId") Long userId, @PathVariable("emailId") Long emailId) {
        Email email = userFacade.deleteEmail(userId, emailId);
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @PostMapping(value = "{userId}/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> transferMoney(@PathVariable("userId") Long userId,
                                           @Valid @RequestBody TransferRequestDto transferRequestDto) {
        Account account = userFacade.transferMoney(userId, transferRequestDto.getValue());
        var accountDto = AccountDto.builder()
                .id(account.getId())
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getBalance())
                .build();
        return ResponseEntity.ok(accountDto);
    }

    private static UserDataDto getUserDataDto(UserData userData) {
        return UserDataDto.builder()
                .id(userData.getId())
                .name(userData.getName())
                .dateOfBirth(userData.getDateOfBirth())
                .account(AccountDto.builder().id(userData.getId()).currentBalance(userData.getAccount().getBalance()).build())
                .phones(userData.getPhones().stream().map(p -> PhoneDto.builder().id(p.getId()).number(p.getNumber()).build()).collect(Collectors.toList()))
                .emails(userData.getEmails().stream().map(e -> EmailDto.builder().id(e.getId()).email(e.getEmail()).build()).collect(Collectors.toList()))
                .build();
    }
}
