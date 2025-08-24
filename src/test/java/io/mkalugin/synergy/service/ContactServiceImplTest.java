package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.exception.TransactionalException;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private CacheManager cacheManager;

    private Contact contact1;
    private Contact contact2;
    private ContactDto contactDto1;
    private ContactDto contactDto2;

    @BeforeEach
    void setUp() {
        cacheManager = new ConcurrentMapCacheManager("contacts", "contact");
        contactService.setCacheManager(cacheManager);

        contact1 = new Contact(1L, "Ivanov", "Ivan", "+7-912-345-67-89");
        contact2 = new Contact(2L, "Petrov", "Petr", "+7-923-456-78-90");

        contactDto1 = new ContactDto(1L, "Ivanov", "Ivan", "+7-912-345-67-89");
        contactDto2 = new ContactDto(2L, "Petrov", "Petr", "+7-923-456-78-90");
    }

    @Test
    void findAll_ShouldReturnAllContacts() {
        // Arrange
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));

        // Act
        List<ContactDto> result = contactService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Ivanov", result.get(0).getLastName());
        assertEquals("Petrov", result.get(1).getLastName());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldThrowTransactionalExceptionOnError() {
        when(contactRepository.findAll()).thenThrow(new EmptyResultDataAccessException("Database error", 1));

        assertThrows(TransactionalException.class, () -> contactService.findAll());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnContactWhenExists() {
        // Arrange
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // Act
        Optional<ContactDto> result = contactService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Ivanov", result.get().getLastName());
        assertEquals("Ivan", result.get().getFirstName());
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        // Arrange
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<ContactDto> result = contactService.findById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(contactRepository, times(1)).findById(999L);
    }

    @Test
    void findById_ShouldThrowTransactionalExceptionOnError() {
        // Arrange
        when(contactRepository.findById(1L)).thenThrow(new EmptyResultDataAccessException("Database error", 1));

        // Act & Assert
        assertThrows(TransactionalException.class, () -> contactService.findById(1L));
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    void save_ShouldPersistNewContact() {
        // Arrange
        ContactDto newContactDto = new ContactDto(null, "Sidorov", "Sergey", "+7-934-567-89-01");
        Contact savedContact = new Contact(3L, "Sidorov", "Sergey", "+7-934-567-89-01");

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // Act
        ContactDto result = contactService.save(newContactDto);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Sidorov", result.getLastName());
        assertEquals("Sergey", result.getFirstName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void save_ShouldUpdateExistingContact() {
        // Arrange
        ContactDto updatedContactDto = new ContactDto(1L, "Ivanov", "Ivan Updated", "+7-999-999-99-99");
        Contact updatedContact = new Contact(1L, "Ivanov", "Ivan Updated", "+7-999-999-99-99");

        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        // Act
        ContactDto result = contactService.save(updatedContactDto);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Ivan Updated", result.getFirstName());
        assertEquals("+7-999-999-99-99", result.getPhone());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void save_ShouldThrowTransactionalExceptionOnError() {
        // Arrange
        ContactDto contactDto = new ContactDto(null, "Test", "Test", "+7-000-000-00-00");
        when(contactRepository.save(any(Contact.class))).thenThrow(new EmptyResultDataAccessException("Database error", 1));

        // Act & Assert
        assertThrows(TransactionalException.class, () -> contactService.save(contactDto));
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void delete_ShouldRemoveContact() {
        // Arrange
        doNothing().when(contactRepository).deleteById(1L);

        // Act
        contactService.delete(1L);

        // Assert
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowTransactionalExceptionOnError() {
        // Arrange
        doThrow(new EmptyResultDataAccessException("Database error", 1)).when(contactRepository).deleteById(1L);

        // Act & Assert
        assertThrows(TransactionalException.class, () -> contactService.delete(1L));
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    void clearCache_ShouldClearAllCaches() {
        // Arrange - сначала заполняем кеш
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll(); // Заполняем кеш

        // Act
        contactService.clearCache();

        // Assert - после очистки кеша должен быть вызван репозиторий снова
        contactService.findAll();
        verify(contactRepository, times(2)).findAll(); // Два вызова: до и после очистки кеша
    }

    @Test
    void convertToDto_ShouldConvertCorrectly() {
        // Act
        ContactDto result = contactService.convertToDto(contact1);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("+7-912-345-67-89", result.getPhone());
    }

    @Test
    void convertToEntity_ShouldConvertCorrectly() {
        // Act
        Contact result = contactService.convertToEntity(contactDto1);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("+7-912-345-67-89", result.getPhone());
    }

    @Test
    void convertToEntity_ShouldHandleNullIdForNewContact() {
        // Arrange
        ContactDto newContactDto = new ContactDto(null, "New", "Contact", "+7-000-000-00-00");

        // Act
        Contact result = contactService.convertToEntity(newContactDto);

        // Assert
        assertNull(result.getId());
        assertEquals("New", result.getLastName());
        assertEquals("Contact", result.getFirstName());
        assertEquals("+7-000-000-00-00", result.getPhone());
    }

    @Test
    void caching_ShouldWorkForFindAll() {
        // Arrange
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));

        // Act - первый вызов (должен сохранить в кеш)
        List<ContactDto> result1 = contactService.findAll();
        // Второй вызов (должен взять из кеша)
        List<ContactDto> result2 = contactService.findAll();

        // Assert
        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
        verify(contactRepository, times(1)).findAll(); // Только один вызов к репозиторию
    }

    @Test
    void caching_ShouldWorkForFindById() {
        // Arrange
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        // Act - первый вызов (должен сохранить в кеш)
        Optional<ContactDto> result1 = contactService.findById(1L);
        // Второй вызов (должен взять из кеша)
        Optional<ContactDto> result2 = contactService.findById(1L);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        verify(contactRepository, times(1)).findById(1L); // Только один вызов к репозиторию
    }

    @Test
    void save_ShouldClearCache() {
        // Arrange - сначала заполняем кеш
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll(); // Заполняем кеш

        when(contactRepository.save(any(Contact.class))).thenReturn(contact1);

        // Act - сохраняем новый контакт (должен очистить кеш)
        contactService.save(contactDto1);

        // Assert - после сохранения кеш очищен, поэтому должен быть новый вызов к репозиторию
        contactService.findAll();
        verify(contactRepository, times(2)).findAll(); // Два вызова: до и после сохранения
    }
}
