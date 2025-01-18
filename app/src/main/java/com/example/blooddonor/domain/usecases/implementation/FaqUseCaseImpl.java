package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.Faq;
import com.example.blooddonor.data.repositories.FaqRepositoryImpl;
import com.example.blooddonor.domain.mappers.FaqMapper;
import com.example.blooddonor.domain.models.FaqDTO;
import com.example.blooddonor.domain.usecases.interfaces.FaqUseCase;

import java.util.ArrayList;
import java.util.List;

public class FaqUseCaseImpl implements FaqUseCase {
    private final FaqRepositoryImpl faqRepository;

    public FaqUseCaseImpl(FaqRepositoryImpl faqRepository) {
        this.faqRepository = faqRepository;
    }

    @Override
    public FaqDTO getFaqById(int faqId) {
        Faq faq = faqRepository.getFaqById(faqId);
        return faq != null ? FaqMapper.toDTO(faq) : null;
    }

    @Override
    public List<FaqDTO> getAllFaqs() {
        List<FaqDTO> faqDTOs = new ArrayList<>();
        List<Faq> faqs = faqRepository.getAllFaqs();
        for (Faq faq : faqs) {
            faqDTOs.add(FaqMapper.toDTO(faq));
        }
        return faqDTOs;
    }

    @Override
    public FaqDTO insertFaq(FaqDTO faqDTO) {
        Faq faq = FaqMapper.toModel(faqDTO);
        return faqRepository.insertFaq(faq) ? FaqMapper.toDTO(faq) : null;
    }

    @Override
    public FaqDTO updateFaq(FaqDTO faqDTO) {
        Faq faq = FaqMapper.toModel(faqDTO);
        return faqRepository.updateFaq(faq) ? FaqMapper.toDTO(faq) : null;
    }

    @Override
    public boolean deleteFaq(int faqId) {
        return faqRepository.deleteFaq(faqId);
    }
}