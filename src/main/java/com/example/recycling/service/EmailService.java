package com.example.recycling.service;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.entity.Response;
import com.example.recycling.entity.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendVerificationEmail(User user) {
        String verificationUrl = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/verify/{verify}")
                .buildAndExpand(user.getEmailSettings().getVerification()).toUriString();
        sendEmail(
                user.getEmail(),
                "You have signed up for recycling",
                "Thanks for signing up to recycling, verify at " + verificationUrl
        );
    }

    public void sendQuestionEmail(Item item, Question question) {
        if (item.getUser().getEmailSettings().isNotifiedOnQuestion()) {
            sendEmail(
                    item.getUser().getEmail(),
                    "A question has been asked",
                    "Please answer " + question.getMessage()
            );
        }
    }

    public void sendResponseEmail(Question question, Response response) {
        if (question.getSentBy().getEmailSettings().isNotifiedOnResponse()) {
            sendEmail(
                    question.getSentBy().getEmail(),
                    "Your question has been answered",
                    response.getMessage()
            );
        }
    }

    private void sendEmail(String email, String subject, String message) {
        try {
            Address emailAddress = new InternetAddress(email);
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            mimeMessage.setFrom("recycling@callumtarttelin.com");
            mimeMessage.addRecipient(Message.RecipientType.TO, emailAddress);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            emailSender.send(mimeMessage);
        } catch (MessagingException ignored) {
        }
    }
}
