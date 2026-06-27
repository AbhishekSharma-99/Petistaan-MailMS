package com.abhishek.dto;

import com.abhishek.enums.MailType;

public record MailDTO(
        String to,

        String firstName,

        String lastName,

        MailType category) {
}
