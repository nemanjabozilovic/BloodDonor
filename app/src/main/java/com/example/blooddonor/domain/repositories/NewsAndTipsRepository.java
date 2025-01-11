package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.NewsAndTips;

import java.util.List;

public interface NewsAndTipsRepository {
    NewsAndTips getNewsAndTipsById(int id);
    List<NewsAndTips> getAllNewsAndTips();
    boolean insertNewsAndTips(NewsAndTips newsAndTips);
    boolean updateNewsAndTips(NewsAndTips newsAndTips);
    boolean deleteNewsAndTips(int id);
}