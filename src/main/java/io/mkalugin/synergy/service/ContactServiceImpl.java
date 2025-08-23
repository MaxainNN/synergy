package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public List<ContactDto> findAll() {
        log.info("Fetching all contacts (not from cache)");
        return contactRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "contact", key = "#id")
    @Transactional(readOnly = true)
    public Optional<ContactDto> findById(Long id) {
        log.info("Fetching contact {} (not from cache)", id);
        return contactRepository.findById(id)
                .map(this::convertToDto);
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    @Transactional
    public ContactDto save(ContactDto contactDto) {
        Contact contact = convertToEntity(contactDto);
        Contact savedContact = contactRepository.save(contact);
        return convertToDto(savedContact);
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    @Transactional
    public void delete(Long id) {
        contactRepository.deleteById(id);
    }

    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    public void clearCache(){
        log.info("Cache cleared");
    }

    private ContactDto convertToDto(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getLastName(),
                contact.getFirstName(),
                contact.getPhone()
        );
    }

    private Contact convertToEntity(ContactDto dto) {
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setLastName(dto.getLastName());
        contact.setFirstName(dto.getFirstName());
        contact.setPhone(dto.getPhone());
        return contact;
    }
}