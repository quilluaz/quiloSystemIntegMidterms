package com.quilo.systemintegmidterms.controller;

import com.google.api.services.people.v1.model.Person;
import com.quilo.systemintegmidterms.service.GoogleContactsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class GoogleContactsController {

    private final GoogleContactsService contactsService;

    public GoogleContactsController(GoogleContactsService contactsService) {
        this.contactsService = contactsService;
    }

    // Create
    @PostMapping
    public Person createContact(OAuth2AuthenticationToken authentication, @RequestBody Person person) throws Exception {
        return contactsService.createContact(authentication, person);
    }

    // Read
    @GetMapping
    public List<Person> getAllContacts(OAuth2AuthenticationToken authentication) throws Exception {
        return contactsService.getAllContacts(authentication);
    }

    // Update
    @PutMapping("/{resourceName}")
    public Person updateContact(OAuth2AuthenticationToken authentication,
                                @PathVariable String resourceName,
                                @RequestBody Person person) throws Exception {
        return contactsService.updateContact(authentication, resourceName, person);
    }

    // Delete
    @DeleteMapping("/{resourceName}")
    public ResponseEntity<?> deleteContact(OAuth2AuthenticationToken authentication,
                                           @PathVariable String resourceName) throws Exception {
        contactsService.deleteContact(authentication, resourceName);
        return ResponseEntity.ok().build();
    }
}