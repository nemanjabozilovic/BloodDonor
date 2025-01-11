package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.Faq;
import com.example.blooddonor.domain.models.FaqDTO;

import java.util.List;
import java.util.stream.Collectors;

public class FaqMapper {
    public static FaqDTO toDTO(Faq faq) {
        return new FaqDTO(faq.getId(), faq.getQuestion(), faq.getAnswer());
    }

    public static Faq toModel(FaqDTO faqDTO) {
        return new Faq(faqDTO.getId(), faqDTO.getQuestion(), faqDTO.getAnswer());
    }

    public static List<FaqDTO> toDTO(List<Faq> faqs) {
        return faqs.stream()
                .map(FaqMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Faq> toModel(List<FaqDTO> faqDTOs) {
        return faqDTOs.stream()
                .map(FaqMapper::toModel)
                .collect(Collectors.toList());
    }
}