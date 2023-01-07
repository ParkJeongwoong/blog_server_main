package io.github.parkjeongwoong.application.blog.service;

import io.github.parkjeongwoong.application.blog.dto.MailSendDto;
import io.github.parkjeongwoong.application.blog.usecase.MailingUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailingService implements MailingUsecase {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendMail(MailSendDto mailSendDto) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("dvlprjw@gmail.com");
        message.setTo(mailSendDto.getAddress());
        message.setSubject(mailSendDto.getTitle());
        message.setText(mailSendDto.getContent());

        javaMailSender.send(message);

    }

}
