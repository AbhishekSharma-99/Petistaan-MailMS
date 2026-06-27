package com.abhishek.service.impl;

import com.abhishek.dto.MailDTO;
import com.abhishek.enums.MailType;
import com.abhishek.service.MailService;
import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final Configuration templateConfig;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${success.message}")
    private String successMessage;
    @Value("${failure.message}")
    private String failureMessage;

    @Override
    public String sendEmail(MailDTO mailDTO) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    true);
            mimeMessageHelper.setFrom(senderEmail);
            mimeMessageHelper.setTo(mailDTO.to());
            mimeMessageHelper.setSubject(mailDTO.category().getSubject());
            mimeMessageHelper.setText(buildMailBodyWithTemplate(mailDTO.category(),
                            mailDTO.firstName().concat(" ").concat(mailDTO.lastName())),
                    true);
            mailSender.send(message);
            return String.format(successMessage,
                    mailDTO.to(),
                    LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error occurred while sending email to {}", mailDTO.to(), e);
            return String.format(failureMessage,
                    mailDTO.to(),
                    LocalDateTime.now());
        }
    }

    private String buildMailBodyWithTemplate(MailType category, String ownerName) {
        Map<String, String> dataModel = new HashMap<>();
        dataModel.put("ownerName",
                ownerName);
        try(Writer writer = new StringWriter()) {
            templateConfig.getTemplate(category.getTemplateFileName())
                    .process(dataModel,
                            writer);
            return writer.toString();
        } catch (Exception e) {
            log.error("Error occurred while processing email template for {}", ownerName, e);
            return ownerName;
        }
    }

}
