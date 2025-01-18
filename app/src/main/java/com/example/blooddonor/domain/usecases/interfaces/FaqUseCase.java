package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.domain.models.FaqDTO;

import java.util.List;

public interface FaqUseCase {
    FaqDTO getFaqById(int faqId);

    List<FaqDTO> getAllFaqs();

    FaqDTO insertFaq(FaqDTO faq);

    FaqDTO updateFaq(FaqDTO faq);

    boolean deleteFaq(int faqId);
}