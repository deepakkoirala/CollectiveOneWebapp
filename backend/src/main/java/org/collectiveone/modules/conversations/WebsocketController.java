package org.collectiveone.modules.conversations;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    private final SimpMessagingTemplate template;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

   
    
    @Autowired
    WebsocketController(SimpMessagingTemplate template){
        this.template = template;
    }

   
    
    @Scheduled(fixedRate = 5000)
    public void run() {
        String time = LocalTime.now().format(TIME_FORMAT);
        template.convertAndSend("/topic/chat", "Current time is " + time);
    }
    
    
    
}
