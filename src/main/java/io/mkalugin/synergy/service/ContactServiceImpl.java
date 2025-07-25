package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    @Setter
    private CacheManager cacheManager;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    @Cacheable("contacts")
    public List<Contact> findAll() {
        log.info("Fetching all contacts (not from cache)");
        return contactRepository.findAll();
    }

    @Override
    @Cacheable(value = "contact", key = "#id")
    public Optional<Contact> findById(Long id) {
        log.info("Fetching contact {} (not from cache)", id);
        return contactRepository.findById(id);
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    public void delete(Long id) {
        contactRepository.delete(id);
    }

    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    public void clearCache(){
        log.info("Cache cleared");
    }

}
