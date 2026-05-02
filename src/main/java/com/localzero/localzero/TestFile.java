package com.localzero.localzero;

import com.localzero.localzero.model.Message;
import com.localzero.localzero.service.MessageService;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestFile {

    @Bean
    CommandLineRunner run(MessageService messageService) {
        return args -> {

            messageService.register("user1");
            messageService.register("user2");

            Message msg = new Message();
            msg.setId("1");
            msg.setSenderId("user1");
            msg.setReceiverId("user2");
            msg.setContent("Hello!");

            messageService.sendMessage(msg);
        };
    }

}

