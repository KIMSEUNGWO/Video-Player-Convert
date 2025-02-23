package com.video.jours.dto;

import com.rabbitmq.client.Channel;
import com.video.jours.dto.serializable.ConvertRequest;

public record UploadStatus(ConvertRequest message, Channel channel, long tag) {
}
