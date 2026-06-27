package com.abhishek.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorDTO(String message, HttpStatus httpStatus, int value, LocalDateTime now) {
}
