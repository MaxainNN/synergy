package io.mkalugin.synergy.service;

import io.mkalugin.synergy.dto.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceImplTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private CacheManager cacheManager;

    @BeforeEach
    void setUp(){
        cacheManager = new ConcurrentMapCacheManager("contacts", "contact");
        contactService.setCacheManager(cacheManager);

        cacheManager.getCache("contacts").clear();
        cacheManager.getCache("contact").clear();
    }

    @Test
    void findAllReturnContacts(){
        List<Contact> expectedContacts = Arrays.asList(
                new Contact(1L, "Alexey", "Leonov", "79990190299"),
                new Contact(2L, "Oleg", "Ivanov", "79530191296"),
                new Contact(3L, "Petr", "Stepanov", "79881233245"),
                new Contact(4L, "Alexandr", "Petrakov", "79532111296"),
                new Contact(5L, "Oksana", "Ivanova", "79522191213")
        );

        when(contactRepository.findAll()).thenReturn(expectedContacts);

        List<Contact> result = contactService.findAll();

        assertEquals(expectedContacts, result);
        verify(contactRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnContactWhenExists() {
        Long id = 1L;
        Contact expectedContact = new Contact(id, "Alexey", "Leonov", "79990190299");
        when(contactRepository.findById(id)).thenReturn(Optional.of(expectedContact));

        Optional<Contact> result = contactService.findById(id);

        assertTrue(result.isPresent());
        assertEquals(expectedContact, result.get());
        verify(contactRepository, times(1)).findById(id);
    }
}
