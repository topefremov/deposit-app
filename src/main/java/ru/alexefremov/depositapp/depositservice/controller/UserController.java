package ru.alexefremov.depositapp.depositservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexefremov.depositapp.depositservice.domain.Account;
import ru.alexefremov.depositapp.depositservice.domain.Email;
import ru.alexefremov.depositapp.depositservice.domain.Phone;
import ru.alexefremov.depositapp.depositservice.dto.*;
import ru.alexefremov.depositapp.depositservice.service.UserFacade;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    @GetMapping("search")
    public ResponseEntity<UserDataDto> search() {
        return ResponseEntity.ok().build();
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
