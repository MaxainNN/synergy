package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.exception.TransactionalException;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Setter
    private CacheManager cacheManager;

    @Override
    @Cacheable("contacts")
    @Transactional(
            readOnly = true,
            propagation = Propagation.SUPPORTS, // Поддерживает текущую транзакцию, если она существует
            isolation = Isolation.READ_COMMITTED, // Чтение только закоммиченных данных
            timeout = 30, // Таймаут операции в секундах
            rollbackFor = TransactionalException.class, // Откат транзакции при получении исключения
            noRollbackFor = IllegalArgumentException.class // Не откатывать при некритичных исключениях
    )
    public List<ContactDto> findAll() {
        log.info("Fetching all contacts (not from cache)");
        try {
            return contactRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new TransactionalException("Error fetching all contacts", e);
        }
    }

    @Override
    @Cacheable(value = "contact", key = "#id")
    @Transactional(
            readOnly = true,
            propagation = Propagation.REQUIRES_NEW, // Всегда создает новую транзакцию, приостанавливая текущую
            isolation = Isolation.READ_COMMITTED, // Чтение только закоммиченных данных
            timeout = 30, // Таймаут операции в секундах
            rollbackFor = TransactionalException.class, // Откат транзакции при получении исключения
            noRollbackFor = {IllegalArgumentException.class, IllegalStateException.class} // Не откатывать
            // при валидационных ошибках
    )
    public Optional<ContactDto> findById(Long id) {
        log.info("Fetching contact {} (not from cache)", id);
        try {
            return contactRepository.findById(id)
                    .map(this::convertToDto);
        } catch (DataAccessException e) {
            throw new TransactionalException("Error fetching contact with id: " + id, e);
        }
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    @Transactional(
            propagation = Propagation.REQUIRED, // Использует текущую транзакцию или создает новую если нет
            isolation = Isolation.SERIALIZABLE, // Наивысший уровень изоляции для операций записи
            timeout = 60, // Таймаут операции в секундах
            rollbackFor = TransactionalException.class, // Откат транзакции при получении исключения
            noRollbackFor = {IllegalArgumentException.class} // Не откатывать
            // при валидационных ошибках
    )
    public ContactDto save(ContactDto contactDto) {
        try {
            Contact contact = convertToEntity(contactDto);
            Contact savedContact = contactRepository.save(contact);
            return convertToDto(savedContact);
        } catch (DataAccessException e) {
            throw new TransactionalException("Error saving contact", e);
        }
    }

    @Override
    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    @Transactional(
            propagation = Propagation.REQUIRED, // Использует текущую транзакцию или создает новую если нет
            isolation = Isolation.REPEATABLE_READ, // Гарантирует, что данные не изменятся во время транзакции
            timeout = 45, // Таймаут операции в секундах
            rollbackFor = TransactionalException.class // Откат транзакции при получении исключения
    )
    public void delete(Long id) {
        try {
            contactRepository.deleteById(id);
        } catch (DataAccessException e) {
            throw new TransactionalException("Error deleting contact with id: " + id, e);
        }
    }

    @CacheEvict(value = {"contacts", "contact"}, allEntries = true)
    @Transactional(
            propagation = Propagation.NOT_SUPPORTED, // Выполняется без транзакции
            timeout = 10 // Таймаут операции в секундах
    )
    public void clearCache(){
        log.info("Cache cleared");
    }

    public ContactDto convertToDto(Contact contact) {
        return new ContactDto(
                contact.getId(),
                contact.getLastName(),
                contact.getFirstName(),
                contact.getPhone()
        );
    }

    public Contact convertToEntity(ContactDto dto) {
        Contact contact = new Contact();
        contact.setId(dto.getId());
        contact.setLastName(dto.getLastName());
        contact.setFirstName(dto.getFirstName());
        contact.setPhone(dto.getPhone());
        return contact;
    }
}
