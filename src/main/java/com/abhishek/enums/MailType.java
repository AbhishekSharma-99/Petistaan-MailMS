package com.abhishek.enums;

import lombok.Getter;

@Getter
public enum MailType {

    WELCOME("Welcome to Petistaan", "welcome.ftlh"),
    MODIFY("Your data in Petistaan has been modified", "modify.ftlh"),
    EXIT("Thanks for visiting Petistaan", "exit.ftlh");

    private final String subject;
    private final String templateFileName;

    MailType(String subject, String templateFileName) {
        this.subject = subject;
        this.templateFileName = templateFileName;
    }

}
