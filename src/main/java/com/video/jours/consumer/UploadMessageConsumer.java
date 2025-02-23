package com.video.jours.consumer;

import com.rabbitmq.client.Channel;
import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.service.StatusService;
import com.video.jours.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadMessageConsumer {

    private final StatusService statusService;
    private final UploadService uploadService;

    @RabbitListener(queues = "video-queue", ackMode = "MANUAL")
    public void receiveMessage(ConvertRequest message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        System.out.println("Message Received: " + message);
        statusService.uploadStatus(message);
        uploadService.upload(message, channel, tag);
    }
}
