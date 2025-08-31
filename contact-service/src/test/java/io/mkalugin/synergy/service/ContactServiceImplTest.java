package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.exception.TransactionalException;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Service tests")
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
    @DisplayName("Preparing test data")
    void setUp() {
        cacheManager = new ConcurrentMapCacheManager("contacts", "contact");
        contactService.setCacheManager(cacheManager);

        contact1 = new Contact(1L, "Ivan",
                "Ivanov",
                "+7-912-345-67-89");
        contact2 = new Contact(2L,
                "Petr",
                "Petrov",
                "+7-923-456-78-90");
        contactDto1 = new ContactDto(1L,
                "Ivan",
                "Ivanov",
                "+7-912-345-67-89");
        contactDto2 = new ContactDto(2L,
                "Petr",
                "Petrov",
                "+7-923-456-78-90");
    }

    @Test
    @Order(1)
    @DisplayName("Find all works")
    void findAll_ShouldReturnAllContacts() {
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        List<ContactDto> result = contactService.findAll();
        assertEquals(2, result.size());
        assertEquals("Ivan", result.get(0).getFirstName());
        assertEquals("Ivanov", result.get(0).getLastName());
        assertEquals("Petr", result.get(1).getFirstName());
        assertEquals("Petrov", result.get(1).getLastName());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    @Order(2)
    @DisplayName("Find all transaction error")
    void findAll_ShouldThrowTransactionalExceptionOnError() {
        when(contactRepository.findAll()).thenThrow(
                new EmptyResultDataAccessException("Database error", 1));
        assertThrows(TransactionalException.class, () -> contactService.findAll());
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    @Order(3)
    @DisplayName("Find By Id works")
    void findById_ShouldReturnContactWhenExists() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));
        Optional<ContactDto> result = contactService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Ivan", result.get().getFirstName());
        assertEquals("Ivanov", result.get().getLastName());
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    @Order(4)
    @DisplayName("Find By Id works - not exist id")
    void findById_ShouldReturnEmptyWhenNotExists() {
        when(contactRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<ContactDto> result = contactService.findById(999L);
        assertFalse(result.isPresent());
        verify(contactRepository, times(1)).findById(999L);
    }

    @Test
    @Order(5)
    @DisplayName("Find By Id transaction error")
    void findById_ShouldThrowTransactionalExceptionOnError() {
        when(contactRepository.findById(1L)).thenThrow(
                new EmptyResultDataAccessException("Database error", 1));

        assertThrows(TransactionalException.class, () -> contactService.findById(1L));
        verify(contactRepository, times(1)).findById(1L);
    }

    @Test
    @Order(6)
    @DisplayName("Save works")
    void save_ShouldPersistNewContact() {
        ContactDto newContactDto = new ContactDto(null,
                "Sergey",
                "Sidorov",
                "+7-934-567-89-01");

        Contact savedContact = new Contact(3L,
                "Sergey",
                "Sidorov",
                "+7-934-567-89-01");

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        ContactDto result = contactService.save(newContactDto);
        assertNotNull(result.getId());
        assertEquals("Sergey", result.getFirstName());
        assertEquals("Sidorov", result.getLastName());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @Order(7)
    @DisplayName("Save updating")
    void save_ShouldUpdateExistingContact() {
        ContactDto updatedContactDto = new ContactDto(1L,
                "Ivan",
                "Ivanov Updated",
                "+7-999-999-99-99");

        Contact updatedContact = new Contact(1L,
                "Ivan",
                "Ivanov Updated",
                "+7-999-999-99-99");

        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        ContactDto result = contactService.save(updatedContactDto);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov Updated", result.getLastName());
        assertEquals("+7-999-999-99-99", result.getPhone());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @Order(8)
    @DisplayName("Save transaction error")
    void save_ShouldThrowTransactionalExceptionOnError() {
        ContactDto contactDto = new ContactDto(null,
                "Test",
                "Testov",
                "+7-000-000-00-00");

        when(contactRepository.save(any(Contact.class))).thenThrow(
                new EmptyResultDataAccessException("Database error", 1));

        assertThrows(TransactionalException.class, () -> contactService.save(contactDto));
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    @Order(9)
    @DisplayName("Delete works")
    void delete_ShouldRemoveContact() {
        doNothing().when(contactRepository).deleteById(1L);
        contactService.delete(1L);
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    @Order(10)
    @DisplayName("Delete transaction error")
    void delete_ShouldThrowTransactionalExceptionOnError() {
        doThrow(new EmptyResultDataAccessException(
                "Database error", 1)).when(contactRepository).deleteById(1L);
        assertThrows(TransactionalException.class, () -> contactService.delete(1L));
        verify(contactRepository, times(1)).deleteById(1L);
    }

    @Test
    @Order(11)
    @DisplayName("Clear cache works")
    void clearCache_ShouldClearAllCaches() {
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll();
        contactService.clearCache();
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll();
        verify(contactRepository, times(2)).findAll();
    }

    @Test
    @Order(12)
    @DisplayName("Convert to DTO works")
    void convertToDto_ShouldConvertCorrectly() {
        ContactDto result = contactService.convertToDto(contact1);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("+7-912-345-67-89", result.getPhone());
    }

    @Test
    @Order(13)
    @DisplayName("Convert to DTO works")
    void convertToEntity_ShouldConvertCorrectly() {
        Contact result = contactService.convertToEntity(contactDto1);
        assertEquals(1L, result.getId());
        assertEquals("Ivan", result.getFirstName());
        assertEquals("Ivanov", result.getLastName());
        assertEquals("+7-912-345-67-89", result.getPhone());
    }

    @Test
    @Order(14)
    @DisplayName("Convert to DTO null converting")
    void convertToEntity_ShouldHandleNullIdForNewContact() {
        ContactDto newContactDto = new ContactDto(null,
                "New",
                "Contact",
                "+7-000-000-00-00");

        Contact result = contactService.convertToEntity(newContactDto);
        assertNull(result.getId());
        assertEquals("New", result.getFirstName());
        assertEquals("Contact", result.getLastName());
        assertEquals("+7-000-000-00-00", result.getPhone());
    }

    @Test
    @Order(15)
    @Disabled("Cache does not work in test")
    @DisplayName("Cache Find All works")
    void caching_ShouldWorkForFindAll() {
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        List<ContactDto> result1 = contactService.findAll();

        reset(contactRepository);
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));

        List<ContactDto> result2 = contactService.findAll();

        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
        verify(contactRepository, never()).findAll();
    }

    @Test
    @Order(16)
    @Disabled("Cache does not work in test")
    @DisplayName("Cache Find By Id works")
    void caching_ShouldWorkForFindById() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));
        Optional<ContactDto> result1 = contactService.findById(1L);

        reset(contactRepository);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        Optional<ContactDto> result2 = contactService.findById(1L);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        verify(contactRepository, never()).findById(1L);
    }

    @Test
    @Order(17)
    @DisplayName("Cache Save works")
    void save_ShouldClearCache() {
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll();
        when(contactRepository.save(any(Contact.class))).thenReturn(contact1);
        contactService.save(contactDto1);
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        contactService.findAll();
        verify(contactRepository, times(2)).findAll();
    }

    @Test
    @Order(18)
    @Disabled("Cache does not work in test")
    @DisplayName("Caching does not interfere methods")
    void caching_ShouldNotInterfereWithDifferentMethods() {
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        contactService.findAll();
        contactService.findById(1L);

        reset(contactRepository);
        when(contactRepository.findAll()).thenReturn(List.of(contact1, contact2));
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact1));

        contactService.findAll();
        contactService.findById(1L);

        verify(contactRepository, never()).findAll();
        verify(contactRepository, never()).findById(1L);
    }
}
