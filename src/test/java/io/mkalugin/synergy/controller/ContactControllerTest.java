package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.model.Contact;
import io.mkalugin.synergy.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Controller tests")
class ContactControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ContactControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactRepository contactRepository;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
    }

    @Test
    @DisplayName("Get all contacts")
    void checkGetAllContacts() throws Exception {
        log.info("Starting test: checkGetAllContacts");

        Contact contact = new Contact();
        contact.setFirstName("FirstName");
        contact.setLastName("LastName");
        contact.setPhone("79990190299");
        contactRepository.save(contact);

        MvcResult result = mockMvc.perform(get("/api/contacts")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").value("FirstName"))
                .andExpect(jsonPath("$[0].lastName").value("LastName"))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @Disabled("Need fix By id tests")
    @DisplayName("Get contact by ID")
    void checkGetContactById() throws Exception {
        log.info("Starting test: checkGetContactById");

        Contact contact = new Contact();
        contact.setFirstName("FirstName1");
        contact.setLastName("LastName1");
        contact.setPhone("79990190298");
        contactRepository.save(contact);

        MvcResult result = mockMvc.perform(get("/api/contacts/1")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("FirstName1"))
                .andExpect(jsonPath("$.lastName").value("LastName1"))
                .andExpect(jsonPath("$.phone").value("79990190298"))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Get contact by ID - not found")
    void checkGetContactById_NotFound() throws Exception {
        log.info("Starting test: checkGetContactById_NotFound");

        mockMvc.perform(get("/api/contacts/999")
                        .with(httpBasic("user", "password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        log.info("Test completed successfully");
    }

    @Test
    @DisplayName("Create new contact")
    void checkCreateContact() throws Exception {
        log.info("Starting test: checkCreateContact");

        MvcResult result = mockMvc.perform(post("/api/contacts")
                        .with(httpBasic("user", "password"))
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
                .andExpect(jsonPath("$.firstName").value("Maxim"))
                .andExpect(jsonPath("$.lastName").value("Kalugin"))
                .andExpect(jsonPath("$.phone").value("79191778546"))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @Disabled("Need fix By id tests")
    @DisplayName("Update contact")
    void checkUpdateContact() throws Exception {
        log.info("Starting test: checkUpdateContact");

        MvcResult result = mockMvc.perform(put("/api/contacts/1")
                        .with(httpBasic("user", "password"))
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
    @Disabled("Need fix By id tests")
    @DisplayName("Delete contact")
    void checkDeleteContact() throws Exception {
        log.info("Starting test: checkDeleteContact");

        mockMvc.perform(delete("/api/contacts/1")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isNoContent());

        log.info("Test completed successfully");
    }

}
