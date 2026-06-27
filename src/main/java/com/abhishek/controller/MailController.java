package com.abhishek.controller;

import com.abhishek.dto.MailDTO;
import com.abhishek.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/mails")
@RestController
public class MailController {

    private final MailService mailService;

    @PostMapping
    public ResponseEntity<String> sendMail(@RequestBody MailDTO mailDTO) {
        String response = mailService.sendEmail(mailDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
