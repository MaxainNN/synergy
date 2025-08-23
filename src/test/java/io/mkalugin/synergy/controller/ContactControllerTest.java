package io.mkalugin.synergy.controller;

import io.mkalugin.synergy.dto.ContactDto;
import io.mkalugin.synergy.service.ContactServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(ContactControllerTest.TestConfig.class)
class ContactControllerTest {

    private static final Logger log = LoggerFactory.getLogger(ContactControllerTest.class);

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private ContactServiceImpl contactServiceImpl;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public ContactServiceImpl contactServiceImpl() {
            ContactServiceImpl mock = Mockito.mock(ContactServiceImpl.class);

            Mockito.when(mock.findAll())
                    .thenReturn(List.of(expectedContact));
            Mockito.when(mock.findById(1L))
                    .thenReturn(Optional.of(expectedContact));

            return mock;
        }
    }

    private static final ContactDto expectedContact = new ContactDto(1L,
            "Alexey",
            "Leonov",
            "79990190299");

    @BeforeEach
    @DisplayName("Checking that service is mocked")
    void setUp() {
        assertTrue(Mockito.mockingDetails(contactServiceImpl).isMock());

        System.out.println("Mock settings: " +
                Mockito.mockingDetails(contactServiceImpl).getMockCreationSettings());
    }

    @Test
    @DisplayName("Get all contacts")
    void checkGetAllContacts() throws Exception {
        log.info("Starting test: checkGetAllContacts");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(expectedContact.getId()))
                .andExpect(jsonPath("$[0].firstName").value(expectedContact.getFirstName()))
                .andReturn();

        log.info("Test completed successfully. Response: {}",
                result.getResponse().getContentAsString());
    }

    @Test
    @DisplayName("Get contact by ID")
    void checkGetContactById() throws Exception {
        log.info("Starting test: checkGetContactById");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/1")
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
    @DisplayName("Create new contact")
    void checkCreateContact() throws Exception {
        log.info("Starting test: checkCreateContact");

        ContactDto savedContact = new ContactDto(2L, "Maxim", "Kalugin", "79191778546");

        Mockito.when(contactServiceImpl.save(Mockito.any(ContactDto.class)))
                .thenReturn(savedContact);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
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
}
