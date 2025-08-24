package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.service.ContactServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactServiceImpl contactService;

    @GetMapping
    public List<ContactDto> getAllContacts() {
        return contactService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return contactService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactDto createContact(@RequestBody ContactDto contact) {
        return contactService.save(contact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactDto> updateContact(@PathVariable Long id,
                                                    @RequestBody ContactDto contact) {
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
