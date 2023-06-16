package com.mantas.twotler.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("twotlersecret@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if(list != null && list.size() > 0) message.setCc(getCcArray(list));

        emailSender.send(message);
    }

    private String[] getCcArray(List<String> list) {
        String[] cc = new String[list.size()];
        int i = 0;
        for(String s : list) {
            cc[i] = s;
            i++;
        }

        return cc;
    }
}
