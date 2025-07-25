package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    List<Contact> findAll();
    Optional<Contact> findById(Long id);
    Contact save(Contact contact);
    void delete(Long id);
}
