package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ContactControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContactService contactService;

    private static final ContactDto expectedContact = new ContactDto(1L,
            "Alexey",
            "Leonov",
            "79990190299");

    @BeforeEach
    @DisplayName("Checking that service is mocked")
    void setUp() {
        assertTrue(org.mockito.Mockito.mockingDetails(contactService).isMock());

        // Настройка моков
        when(contactService.findAll()).thenReturn(List.of(expectedContact));
        when(contactService.findById(1L)).thenReturn(Optional.of(expectedContact));

        System.out.println("Mock settings: " +
                org.mockito.Mockito.mockingDetails(contactService).getMockCreationSettings());
    }

    @Test
    @DisplayName("Get all contacts")
    void checkGetAllContacts() throws Exception {
        log.info("Starting test: checkGetAllContacts");

        MvcResult result = mockMvc.perform(get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedContact.getId()))
                .andExpect(jsonPath("$[0].firstName").value(expectedContact.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(expectedContact.getLastName()))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Get contact by ID")
    void checkGetContactById() throws Exception {
        log.info("Starting test: checkGetContactById");

        MvcResult result = mockMvc.perform(get("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedContact.getId()))
                .andExpect(jsonPath("$.firstName").value(expectedContact.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedContact.getLastName()))
                .andExpect(jsonPath("$.phone").value(expectedContact.getPhone()))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Get contact by ID - not found")
    void checkGetContactById_NotFound() throws Exception {
        log.info("Starting test: checkGetContactById_NotFound");

        when(contactService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/contacts/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        log.info("Test completed successfully");
    }

    @Test
    @DisplayName("Create new contact")
    void checkCreateContact() throws Exception {
        log.info("Starting test: checkCreateContact");

        ContactDto savedContact = new ContactDto(2L, "Maxim", "Kalugin", "79191778546");

        when(contactService.save(any(ContactDto.class))).thenReturn(savedContact);

        MvcResult result = mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstName": "Maxim",
                            "lastName": "Kalugin",
                            "phone": "79191778546"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.firstName").value("Maxim"))
                .andExpect(jsonPath("$.lastName").value("Kalugin"))
                .andExpect(jsonPath("$.phone").value("79191778546"))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Update contact")
    void checkUpdateContact() throws Exception {
        log.info("Starting test: checkUpdateContact");

        ContactDto updatedContact = new ContactDto(1L, "Alexey", "Leonov Updated", "79990190299");

        when(contactService.save(any(ContactDto.class))).thenReturn(updatedContact);

        MvcResult result = mockMvc.perform(put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "id": 1,
                            "firstName": "Alexey",
                            "lastName": "Leonov Updated",
                            "phone": "79990190299"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Leonov Updated"))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Delete contact")
    void checkDeleteContact() throws Exception {
        log.info("Starting test: checkDeleteContact");

        mockMvc.perform(delete("/api/contacts/1"))
                .andExpect(status().isNoContent());

        log.info("Test completed successfully");
    }

    @Test
    @DisplayName("Create contact with validation error")
    void checkCreateContact_ValidationError() throws Exception {
        log.info("Starting test: checkCreateContact_ValidationError");

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "firstName": "",
                            "lastName": "Test",
                            "phone": "123"
                        }
                        """))
                .andExpect(status().isBadRequest());

        log.info("Test completed successfully");
    }
}
