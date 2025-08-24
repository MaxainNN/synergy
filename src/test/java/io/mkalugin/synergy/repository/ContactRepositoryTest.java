package io.mkalugin.synergy.repository;

import io.mkalugin.synergy.model.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    void findAll_ShouldReturnAllContacts() {
        Contact contact1 = new Contact(null, "Ivanov", "Ivan", "+7-912-345-67-89");
        Contact contact2 = new Contact(null, "Petrov", "Petr", "+7-923-456-78-90");

        entityManager.persist(contact1);
        entityManager.persist(contact2);
        entityManager.flush();

        List<Contact> contacts = contactRepository.findAll();

        assertEquals(2, contacts.size());
        assertTrue(contacts.stream().anyMatch(c -> c.getLastName().equals("Ivanov")));
        assertTrue(contacts.stream().anyMatch(c -> c.getLastName().equals("Petrov")));
    }

    @Test
    void findById_ShouldReturnContactWhenExists() {
        Contact contact = new Contact(null, "Sidorov", "Sergey", "+7-934-567-89-01");
        Contact savedContact = entityManager.persist(contact);
        entityManager.flush();

        Optional<Contact> foundContact = contactRepository.findById(savedContact.getId());

        assertTrue(foundContact.isPresent());
        assertEquals("Sidorov", foundContact.get().getLastName());
        assertEquals("Sergey", foundContact.get().getFirstName());
        assertEquals("+7-934-567-89-01", foundContact.get().getPhone());
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        Optional<Contact> foundContact = contactRepository.findById(999L);
        assertFalse(foundContact.isPresent());
    }

    @Test
    void save_ShouldPersistContact() {
        Contact contact = new Contact(null, "Kuznetsov", "Alex", "+7-945-678-90-12");

        Contact savedContact = contactRepository.save(contact);

        assertNotNull(savedContact.getId());
        assertEquals("Kuznetsov", savedContact.getLastName());
        assertEquals("Alex", savedContact.getFirstName());
        assertEquals("+7-945-678-90-12", savedContact.getPhone());

        Contact foundContact = entityManager.find(Contact.class, savedContact.getId());
        assertNotNull(foundContact);
        assertEquals("Kuznetsov", foundContact.getLastName());
    }

    @Test
    void deleteById_ShouldRemoveContact() {
        Contact contact = new Contact(null, "Smirnov", "Dmitry", "+7-956-789-01-23");
        Contact savedContact = entityManager.persist(contact);
        entityManager.flush();

        contactRepository.deleteById(savedContact.getId());
        entityManager.flush();
        entityManager.clear();

        Contact deletedContact = entityManager.find(Contact.class, savedContact.getId());
        assertNull(deletedContact);
    }

    @Test
    void save_ShouldUpdateExistingContact() {
        Contact contact = new Contact(null, "Orlov", "Oleg", "+7-967-890-12-34");
        Contact savedContact = entityManager.persist(contact);
        entityManager.flush();

        savedContact.setPhone("+7-999-999-99-99");
        Contact updatedContact = contactRepository.save(savedContact);
        entityManager.flush();

        assertEquals(savedContact.getId(), updatedContact.getId());
        assertEquals("Orlov", updatedContact.getLastName());
        assertEquals("Oleg", updatedContact.getFirstName());
        assertEquals("+7-999-999-99-99", updatedContact.getPhone());

        Contact foundContact = entityManager.find(Contact.class, savedContact.getId());
        assertEquals("+7-999-999-99-99", foundContact.getPhone());
    }
}