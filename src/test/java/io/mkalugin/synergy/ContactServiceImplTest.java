package io.mkalugin.synergy;

import io.mkalugin.synergy.config.AppConfig;
import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.service.ContactServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = AppConfig.class)
public class ContactServiceImplTest {

    @Autowired
    private ContactServiceImpl contactService;

    @Test
    void findAllReturnContacts() throws IOException {
        List<Contact> contacts = contactService.findAll();

        assertNotNull(contacts);
        assertEquals(5, contacts.size());
        assertEquals("Leonov", contacts.get(0).getLastName());
        assertEquals("Ivanov", contacts.get(1).getLastName());
    }

}
