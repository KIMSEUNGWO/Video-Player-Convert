package com.video.jours.entity;

import com.video.jours.enums.ProcessingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class VideoStatus {

    @Id
    private String statusKey;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    private String thumbnail;
    private String originalVideo;
    private String title;

    private String errorMessage;

    public void updateStatus(ProcessingStatus status) {
        this.status = status;
    }

    public void setError(String errorMessage) {
        this.status = ProcessingStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
