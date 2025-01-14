package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.Notification;
import com.example.blooddonor.domain.models.NotificationDTO;

import java.util.List;
import java.util.stream.Collectors;

public class NotificationMapper {
    public static NotificationDTO toDTO(Notification news) {
        return new NotificationDTO(news.getId(), news.getTitle(), news.getText(), news.getCreatedDate());
    }

    public static Notification toModel(NotificationDTO newsDTO) {
        return new Notification(newsDTO.getId(), newsDTO.getTitle(), newsDTO.getText(), newsDTO.getCreatedDate());
    }

    public static List<NotificationDTO> toDTO(List<Notification> newsList) {
        return newsList.stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Notification> toModel(List<NotificationDTO> newsDTOs) {
        return newsDTOs.stream()
                .map(NotificationMapper::toModel)
                .collect(Collectors.toList());
    }
}