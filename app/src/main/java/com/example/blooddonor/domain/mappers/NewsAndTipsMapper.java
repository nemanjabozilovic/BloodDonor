package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.NewsAndTips;
import com.example.blooddonor.domain.models.NewsAndTipsDTO;

import java.util.List;
import java.util.stream.Collectors;

public class NewsAndTipsMapper {
    public static NewsAndTipsDTO toDTO(NewsAndTips news) {
        return new NewsAndTipsDTO(news.getId(), news.getTitle(), news.getText(), news.getCreatedDate());
    }

    public static NewsAndTips toModel(NewsAndTipsDTO newsDTO) {
        return new NewsAndTips(newsDTO.getId(), newsDTO.getTitle(), newsDTO.getText(), newsDTO.getCreatedDate());
    }

    public static List<NewsAndTipsDTO> toDTO(List<NewsAndTips> newsList) {
        return newsList.stream()
                .map(NewsAndTipsMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<NewsAndTips> toModel(List<NewsAndTipsDTO> newsDTOs) {
        return newsDTOs.stream()
                .map(NewsAndTipsMapper::toModel)
                .collect(Collectors.toList());
    }
}