package ru.alexefremov.depositapp.depositservice.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.dto.*;
import ru.alexefremov.depositapp.depositservice.search.SearchFilter;
import ru.alexefremov.depositapp.depositservice.search.UserData;
import ru.alexefremov.depositapp.depositservice.service.UserFacade;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

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

    @GetMapping("search")
    public ResponseEntity<Page<UserDataDto>> search(@RequestParam(value = "name", required = false) String name,
                                                    @RequestParam(value = "email", required = false) String email,
                                                    @RequestParam(value = "phone", required = false) String phone,
                                                    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
                                                    @RequestParam(value = "dateOfBirth", required = false) LocalDate dateOfBirth,
                                                    Pageable pageable) {
        var searchFilter = SearchFilter.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .dateOfBirth(dateOfBirth)
                .pageable(pageable)
                .build();
        SearchPage<UserData> search = userFacade.search(searchFilter);
        Page<UserDataDto> result = search.map(userDataSearchHit -> getUserDataDto(userDataSearchHit.getContent()));
        return ResponseEntity.ok(result);
    }

    @PostMapping("{userId}/phones")
    public ResponseEntity<PhoneDto> addPhone(@PathVariable("userId") Long userId, @RequestBody AddChangePhoneRequest request) {
        Phone phone = userFacade.addPhone(userId, request.getNumber());
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @PutMapping("{userId}/phones/{phoneId}")
    public ResponseEntity<PhoneDto> changePhone(@PathVariable("userId") Long userId,
                                                @PathVariable("phoneId") Long phoneId,
                                                @RequestBody AddChangePhoneRequest request) {
        Phone phone = userFacade.changePhone(userId, phoneId, request.getNumber());
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @DeleteMapping("{userId}/phones/{phoneId}")
    public ResponseEntity<PhoneDto> deletePhone(@PathVariable("userId") Long userId, @PathVariable("phoneId") Long phoneId) {
        Phone phone = userFacade.deletePhone(userId, phoneId);
        var phoneDto = PhoneDto.builder().id(phone.getId()).number(phone.getNumber()).build();
        return ResponseEntity.ok(phoneDto);
    }

    @PostMapping("{userId}/emails")
    public ResponseEntity<EmailDto> addEmail(@PathVariable("userId") Long userId, @RequestBody AddChangeEmailRequest request) {
        Email email = userFacade.addEmail(userId, request.getEmail());
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @PutMapping("{userId}/emails/{emailId}")
    public ResponseEntity<EmailDto> changeEmail(@PathVariable("userId") Long userId,
                                                @PathVariable("emailId") Long emailId,
                                                @RequestBody AddChangeEmailRequest request) {
        Email email = userFacade.changeEmail(userId, emailId, request.getEmail());
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @DeleteMapping("{userId}/emails/{emailId}")
    public ResponseEntity<EmailDto> deleteEmail(@PathVariable("userId") Long userId, @PathVariable("emailId") Long emailId) {
        Email email = userFacade.deleteEmail(userId, emailId);
        var emailDto = EmailDto.builder().id(email.getId()).email(email.getEmail()).build();
        return ResponseEntity.ok(emailDto);
    }

    @PostMapping("{userId}/transfer")
    public ResponseEntity<?> transferMoney(@PathVariable("userId") Long userId, @RequestBody TransferRequest transferRequest) {
        Account account = userFacade.transferMoney(userId, transferRequest.getValue());
        var accountDto = AccountDto.builder()
                .id(account.getId())
                .initialBalance(account.getInitialBalance())
                .currentBalance(account.getBalance())
                .build();
        return ResponseEntity.ok(accountDto);
    }
}
