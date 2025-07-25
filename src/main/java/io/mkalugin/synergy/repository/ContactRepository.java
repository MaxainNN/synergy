package io.mkalugin.synergy.repository;

import io.mkalugin.synergy.dto.Contact;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class ContactRepository {
    private final List<Contact> contacts = new ArrayList<>();
    private Long nextId = 1L;

    @PostConstruct
    public void init() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("contacts.csv")) {
            if (is == null) {
                log.warn("Warning: contacts.csv not found in resources. Using empty contact list.");
                return;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length >= 3) {
                        Contact contact = new Contact();
                        contact.setId(nextId++);
                        contact.setLastName(data[0].trim());
                        contact.setFirstName(data[1].trim());
                        contact.setPhone(data[3].trim());
                        contacts.add(contact);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load contacts.csv", e);
        }
    }

    public List<Contact> findAll() {
        return contacts;
    }

    public Optional<Contact> findById(Long id) {
        return contacts.stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Contact save(Contact contact) {
        if (contact.getId() == null) {
            contact.setId(nextId++);
        }
        contacts.add(contact);
        return contact;
    }

    public void delete(Long id) {
        contacts.removeIf(c -> c.getId().equals(id));
    }
}
