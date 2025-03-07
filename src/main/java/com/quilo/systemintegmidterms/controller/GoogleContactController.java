package com.quilo.systemintegmidterms.controller;

import com.quilo.systemintegmidterms.service.GooglePeopleService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contacts")
public class GoogleContactController {

    private final GooglePeopleService googlePeopleService;

    public GoogleContactController(GooglePeopleService googlePeopleService) {
        this.googlePeopleService = googlePeopleService;
    }

    /**
     * Display the list of contacts in the index view.
     */
    @GetMapping
    public String getContacts(OAuth2AuthenticationToken authentication, Model model) {
        model.addAttribute("contacts", googlePeopleService.getContacts(authentication));
        return "index";
    }

    /**
     * Show form for adding a new contact
     * (If you do everything via a modal, this might be unused, but it's here for completeness.)
     */
    @GetMapping("/new")
    public String showAddContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact-form";
    }

    /**
     * Handle POST for creating a new contact
     * (Uses firstName, lastName, email, phone in ContactForm)
     */
    @PostMapping
    public String addContact(OAuth2AuthenticationToken authentication,
                             @ModelAttribute ContactForm contactForm) {
        googlePeopleService.createContact(
                authentication,
                contactForm.getFirstName(),
                contactForm.getLastName(),
                contactForm.getEmail(),
                contactForm.getPhone()
        );
        return "redirect:/contacts";
    }

    /**
     * Show an edit form (optional) for a single contact.
     * Here, contactId is something like "c2424619112402910077"
     * We prepend "people/" inside this method to fetch from Google.
     */
    @GetMapping("/{contactId}/edit")
    public String showEditContactForm(@PathVariable String contactId,
                                      OAuth2AuthenticationToken authentication,
                                      Model model) {
        // Build the full People API resource name
        String resourceName = "people/" + contactId;

        var person = googlePeopleService.getContactByResourceName(authentication, resourceName);

        ContactForm form = new ContactForm();
        if (person.getNames() != null && !person.getNames().isEmpty()) {
            form.setFirstName(person.getNames().get(0).getGivenName());
            form.setLastName(person.getNames().get(0).getFamilyName());
        }
        if (person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty()) {
            form.setEmail(person.getEmailAddresses().get(0).getValue());
        }
        if (person.getPhoneNumbers() != null && !person.getPhoneNumbers().isEmpty()) {
            form.setPhone(person.getPhoneNumbers().get(0).getValue());
        }

        model.addAttribute("contactForm", form);
        model.addAttribute("contactId", contactId);
        return "contact-form";
    }

    /**
     * Handle POST to update an existing contact.
     * Again, 'contactId' is just the portion after "people/", so we reconstruct "people/xxxx".
     */
    @PostMapping("/{contactId}/update")
    public String updateContact(OAuth2AuthenticationToken authentication,
                                @PathVariable String contactId,
                                @ModelAttribute ContactForm contactForm) {

        String resourceName = "people/" + contactId; // Rebuild the Google People API resource name

        googlePeopleService.updateContact(
                authentication,
                resourceName,
                contactForm.getFirstName(),
                contactForm.getLastName(),
                contactForm.getEmail(),
                contactForm.getPhone()
        );
        return "redirect:/contacts";
    }

    /**
     * Delete contact by ID portion only, no slash in the route.
     */
    @PostMapping("/{contactId}/delete")
    public String deleteContact(OAuth2AuthenticationToken authentication,
                                @PathVariable String contactId) {
        String resourceName = "people/" + contactId;
        googlePeopleService.deleteContact(authentication, resourceName);
        return "redirect:/contacts";
    }

    /**
     * Simple form-backing bean with separate first/last name fields
     */
    public static class ContactForm {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}
