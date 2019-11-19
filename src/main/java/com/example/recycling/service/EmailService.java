package com.example.recycling.service;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.entity.Response;
import com.example.recycling.entity.User;
import org.springframework.beans.factory.annotation.Value;
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

    // Get value from application.properties
    private @Value("${recycling.email.from}") String fromAddress;

    // Initialize with the email sender
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendVerificationEmail(User user) {
        // Get the verification URL
        String verificationUrl = ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("/verify/{verify}")
                .buildAndExpand(user.getEmailSettings().getVerification()).toUriString();
        // Send the email
        sendEmail(
                user.getEmail(),
                "You have signed up for recycling",
                "Thanks for signing up to recycling, verify at " + verificationUrl
        );
    }

    public void sendQuestionEmail(Item item, Question question) {
        // If user is notified on questions
        if (item.getUser().getEmailSettings().isNotifiedOnQuestion()) {
            // Send email
            sendEmail(
                    item.getUser().getEmail(),
                    "A question has been asked",
                    "Please answer " + question.getMessage()
            );
        }
    }

    public void sendResponseEmail(Question question, Response response) {
        // If user is notified on responses
        if (question.getSentBy().getEmailSettings().isNotifiedOnResponse()) {
            // Send email
            sendEmail(
                    question.getSentBy().getEmail(),
                    "Your question has been answered",
                    response.getMessage()
            );
        }
    }

    private void sendEmail(String email, String subject, String message) {
        // Ignore any errors with emails
        try {
            // Create address from users email
            Address emailAddress = new InternetAddress(email);
            // Create message and set values
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            mimeMessage.setFrom(fromAddress);
            mimeMessage.addRecipient(Message.RecipientType.TO, emailAddress);
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);
            // Send message
            emailSender.send(mimeMessage);
        } catch (MessagingException ignored) {
        }
    }
}
