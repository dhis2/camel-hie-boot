package org.hisp.hieboot;

import org.apache.camel.support.MessageHelper;
import org.hisp.hieboot.camel.spi.MessageRepository;
import org.hisp.hieboot.camel.spi.RepositoryMessage;
import org.hisp.hieboot.messagerepo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReplayLogController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/replayLog/messages")
    public List<Message> getReplayLogMessages() {
        List<RepositoryMessage> repositoryMessages = messageRepository.retrieve("*", 0, 100);
        List<Message> apiMessages = new ArrayList<>();
        for (RepositoryMessage repositoryMessage : repositoryMessages) {
            Message apiMessage = new Message();
            if (repositoryMessage.getMessage().getBody() != null) {
                apiMessage.setBody(MessageHelper.extractBodyAsString(repositoryMessage.getMessage()));
            }
            apiMessage.setHeaders(repositoryMessage.getMessage().getHeaders());
            String key = repositoryMessage.getKey();
            apiMessage.setStatus(Message.StatusEnum.fromValue(key.substring(0, key.indexOf(":"))));
            apiMessages.add(apiMessage);
        }

        return apiMessages;
    }
}
