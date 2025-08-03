package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.dto.Contact;
import io.mkalugin.synergy.service.ContactServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contacts")
public class ContactController {
    private final ContactServiceImpl contactService;

    @Autowired
    public ContactController(ContactServiceImpl contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        return contactService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contact createContact(@RequestBody Contact contact) {
        return contactService.save(contact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        if (contactService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contact.setId(id);
        return ResponseEntity.ok(contactService.save(contact));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        if (contactService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        contactService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        contactService.clearCache();
        return ResponseEntity.ok("Cache cleared");
    }
}
