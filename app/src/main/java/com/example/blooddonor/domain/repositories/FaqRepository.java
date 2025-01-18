package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.Faq;

import java.util.List;

public interface FaqRepository {
    Faq getFaqById(int id);

    List<Faq> getAllFaqs();

    boolean insertFaq(Faq faq);

    boolean updateFaq(Faq faq);

    boolean deleteFaq(int id);
}