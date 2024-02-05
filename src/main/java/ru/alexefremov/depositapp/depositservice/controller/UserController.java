package ru.alexefremov.depositapp.depositservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alexefremov.depositapp.depositservice.dto.*;

@RestController
@RequestMapping("users")
public class UserController {
    @GetMapping("search")
    public ResponseEntity<UserDataDto> search() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("{userId}/phones")
    public ResponseEntity<PhoneDto> addPhone(@PathVariable("userId") Long userId, @RequestBody AddChangePhoneRequest request) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("{userId}/phones/{phoneId}")
    public ResponseEntity<PhoneDto> changePhone(@PathVariable("userId") Long userId,
                                                @PathVariable("phoneId") Long phoneId,
                                                @RequestBody AddChangePhoneRequest request) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{userId}/phones/{phoneId}")
    public ResponseEntity<PhoneDto> deletePhone(@PathVariable("userId") Long userId, @PathVariable("phoneId") Long phoneId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("{userId}/emails")
    public ResponseEntity<EmailDto> addEmail(@PathVariable("userId") Long userId, @RequestBody AddChangeEmailRequest request) {
        return ResponseEntity.ok().build();
    }

    @PutMapping("{userId}/emails/{emailId}")
    public ResponseEntity<EmailDto> changeEmail(@PathVariable("userId") Long userId,
                                                @PathVariable("emailId") Long emailId,
                                                @RequestBody AddChangeEmailRequest request) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{userId}/emails/{emailId}")
    public ResponseEntity<EmailDto> deleteEmail(@PathVariable("userId") Long userId, @PathVariable("emailId") Long emailId) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("{userId}/transfer")
    public ResponseEntity<?> transferMoney(@PathVariable("userId") Long userId, @RequestBody TransferRequestDto transferRequestDto) {
        return ResponseEntity.ok().build();
    }
}
