package io.mkalugin.synergy.service;

import io.mkalugin.synergy.model.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {
    @Value("${contacts.file.path}")
    private String filepath;

    @Override
    public List<Contact> findAll() {
        try {
            return Files.lines(Paths.get(filepath))
                    .skip(1)
                    .map(line -> {
                        String[] data = line.split(",");
                        Contact contact = new Contact();
                        contact.setLastName(data[0]);
                        contact.setFirstName(data[1]);
                        contact.setMiddleName(data[2]);
                        contact.setPhone(data[3]);
                        return contact;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read contacts file", e);
        }
    }
}
