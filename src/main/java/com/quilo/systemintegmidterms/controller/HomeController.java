package com.quilo.systemintegmidterms.controller;

import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.quilo.systemintegmidterms.service.GoogleContactsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final GoogleContactsService contactsService;

    public HomeController(GoogleContactsService contactsService) {
        this.contactsService = contactsService;
    }

    @GetMapping("/")
    public String showIndex(Model model, OAuth2AuthenticationToken authentication) throws Exception {
        // Retrieve contacts from Google
        List<Person> contacts = contactsService.getAllContacts(authentication);
        model.addAttribute("contacts", contacts);

        // Initialize an empty Person
        Person emptyPerson = new Person();
        emptyPerson.setNames(new ArrayList<>());
        emptyPerson.setEmailAddresses(new ArrayList<>());
        emptyPerson.setPhoneNumbers(new ArrayList<>());
        emptyPerson.getNames().add(new Name());
        emptyPerson.getEmailAddresses().add(new EmailAddress());
        emptyPerson.getPhoneNumbers().add(new PhoneNumber());

        model.addAttribute("person", emptyPerson);
        return "index";
    }
}
