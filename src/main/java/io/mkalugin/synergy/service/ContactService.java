package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.ContactDto;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    List<ContactDto> findAll();
    Optional<ContactDto> findById(Long id);
    ContactDto save(ContactDto contact);
    void delete(Long id);
}
