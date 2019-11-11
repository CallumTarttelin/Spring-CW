package com.example.recycling.service;

import com.example.recycling.entity.Item;
import com.example.recycling.entity.Question;
import com.example.recycling.entity.Response;
import com.example.recycling.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSenderImpl mailSender;
    @InjectMocks
    private EmailService service;

    private User notify;
    private User notNotify;

    @BeforeEach
    void setUp() {
        notify = new User().setEmail("user@example-domain.com");
        notify.getEmailSettings().setVerified(true);
        notNotify = new User().setEmail("user@example-domain.com");
    }

    @Test
    void sendQuestionEmail_doNotify() {
        when(mailSender.createMimeMessage()).thenCallRealMethod();
        service.sendQuestionEmail(Item.offeredItem().setUser(notify), new Question());
        verify(mailSender).send(isA(MimeMessage.class));
    }

    @Test
    void sendQuestionEmail_doNotNotify() {
        service.sendQuestionEmail(Item.offeredItem().setUser(notNotify), new Question());
        verify(mailSender, never()).send(isA(MimeMessage.class));
    }

    @Test
    void sendResponseEmail_doNotify() {
        when(mailSender.createMimeMessage()).thenCallRealMethod();
        service.sendResponseEmail(new Question().setSentBy(notify), new Response().setMessage("Hello World"));
        verify(mailSender).send(isA(MimeMessage.class));
    }

    @Test
    void sendResponseEmail_doNotNotify() {
        service.sendResponseEmail(new Question().setSentBy(notNotify), new Response());
        verify(mailSender, never()).send(isA(MimeMessage.class));
    }

    @Test
    void sendResponseEmail_invalidEmail() {
        service.sendResponseEmail(new Question().setSentBy(notify.setEmail("Invalid Email")), new Response());
        verify(mailSender, never()).send(isA(MimeMessage.class));
    }
}
