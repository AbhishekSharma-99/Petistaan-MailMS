package com.abhishek.service;

import com.abhishek.dto.MailDTO;

public interface MailService {

    String sendEmail(MailDTO mailDTO);
}
