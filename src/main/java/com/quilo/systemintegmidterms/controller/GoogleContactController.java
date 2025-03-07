package com.quilo.systemintegmidterms.controller;

import com.google.api.services.people.v1.model.Person;
import com.quilo.systemintegmidterms.service.GooglePeopleService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/contacts")
public class GoogleContactController {

    private final GooglePeopleService googlePeopleService;

    public GoogleContactController(GooglePeopleService googlePeopleService) {
        this.googlePeopleService = googlePeopleService;
    }

    @GetMapping
    public String getContacts(OAuth2AuthenticationToken authentication, Model model) {
        List<Person> persons = googlePeopleService.getContacts(authentication);
        List<ContactDto> contacts = persons.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        model.addAttribute("contacts", contacts);
        return "index";
    }

    private ContactDto convertToDto(Person person) {
        ContactDto dto = new ContactDto();
        // Extract resourceName (remove the "people/" prefix)
        if (person.getResourceName() != null) {
            dto.setResourceName(person.getResourceName().replace("people/", ""));
        }
        // Names
        if (person.getNames() != null && !person.getNames().isEmpty()) {
            dto.setFirstName(person.getNames().get(0).getGivenName());
            dto.setLastName(person.getNames().get(0).getFamilyName());
        } else {
            dto.setFirstName("");
            dto.setLastName("");
        }
        // Emails
        if (person.getEmailAddresses() != null && !person.getEmailAddresses().isEmpty()) {
            List<String> emails = person.getEmailAddresses().stream()
                    .map(email -> email.getValue())
                    .collect(Collectors.toList());
            dto.setEmails(emails);
        } else {
            dto.setEmails(Collections.emptyList());
        }
        // Phone Numbers
        if (person.getPhoneNumbers() != null && !person.getPhoneNumbers().isEmpty()) {
            List<String> phones = person.getPhoneNumbers().stream()
                    .map(phone -> phone.getValue())
                    .collect(Collectors.toList());
            dto.setPhones(phones);
        } else {
            dto.setPhones(Collections.emptyList());
        }
        // Birthday (formatted)
        String formattedBirthday = "";
        if (person.getBirthdays() != null && !person.getBirthdays().isEmpty() &&
                person.getBirthdays().get(0).getDate() != null) {
            var bd = person.getBirthdays().get(0).getDate();
            formattedBirthday = String.format("%04d-%02d-%02d", bd.getYear(), bd.getMonth(), bd.getDay());
        }
        dto.setFormattedBirthday(formattedBirthday);
        // Notes
        if (person.getBiographies() != null && !person.getBiographies().isEmpty()) {
            dto.setNotes(person.getBiographies().get(0).getValue());
        } else {
            dto.setNotes("");
        }
        // Photo URL
        if (person.getPhotos() != null && !person.getPhotos().isEmpty() && person.getPhotos().get(0).getUrl() != null) {
            dto.setPhotoUrl(person.getPhotos().get(0).getUrl());
        } else {
            dto.setPhotoUrl(null);
        }
        return dto;
    }

    @GetMapping("/new")
    public String showAddContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact-form";
    }

    @PostMapping
    public String addContact(OAuth2AuthenticationToken authentication,
                             @ModelAttribute ContactForm contactForm) {
        List<String> emails = contactForm.getEmail();
        List<String> phones = contactForm.getPhone();
        googlePeopleService.createContact(
                authentication,
                contactForm.getFirstName(),
                contactForm.getLastName(),
                emails,
                phones,
                contactForm.getBirthday(),
                contactForm.getNotes()
        );
        return "redirect:/contacts";
    }

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
            StringBuilder emails = new StringBuilder();
            person.getEmailAddresses().forEach(email -> {
                if (emails.length() > 0) emails.append(", ");
                emails.append(email.getValue());
            });
            form.setEmail(Arrays.asList(emails.toString().split(",\\s*")));
        }
        if (person.getPhoneNumbers() != null && !person.getPhoneNumbers().isEmpty()) {
            StringBuilder phones = new StringBuilder();
            person.getPhoneNumbers().forEach(phone -> {
                if (phones.length() > 0) phones.append(", ");
                phones.append(phone.getValue());
            });
            form.setPhone(Arrays.asList(phones.toString().split(",\\s*")));
        }
        if (person.getBirthdays() != null && !person.getBirthdays().isEmpty() &&
                person.getBirthdays().get(0).getDate() != null) {
            var bd = person.getBirthdays().get(0).getDate();
            String formatted = String.format("%04d-%02d-%02d", bd.getYear(), bd.getMonth(), bd.getDay());
            form.setBirthday(formatted);
        }
        if (person.getBiographies() != null && !person.getBiographies().isEmpty()) {
            form.setNotes(person.getBiographies().get(0).getValue());
        }

        model.addAttribute("contactForm", form);
        model.addAttribute("contactId", contactId);
        return "contact-form";
    }

    @PostMapping("/{contactId}/update")
    public String updateContact(OAuth2AuthenticationToken authentication,
                                @PathVariable String contactId,
                                @ModelAttribute ContactForm contactForm) {
        String resourceName = "people/" + contactId;
        List<String> emails = contactForm.getEmail();
        List<String> phones = contactForm.getPhone();
        googlePeopleService.updateContact(
                authentication,
                resourceName,
                contactForm.getFirstName(),
                contactForm.getLastName(),
                emails,
                phones,
                contactForm.getBirthday(),
                contactForm.getNotes()
        );
        return "redirect:/contacts";
    }

    @PostMapping("/{contactId}/delete")
    public String deleteContact(OAuth2AuthenticationToken authentication,
                                @PathVariable String contactId) {
        String resourceName = "people/" + contactId;
        googlePeopleService.deleteContact(authentication, resourceName);
        return "redirect:/contacts";
    }

    public static class ContactForm {
        private String firstName;
        private String lastName;
        private List<String> email;
        private List<String> phone;
        private String birthday;
        private String notes;

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public List<String> getEmail() { return email; }
        public void setEmail(List<String> email) { this.email = email; }

        public List<String> getPhone() { return phone; }
        public void setPhone(List<String> phone) { this.phone = phone; }

        public String getBirthday() { return birthday; }
        public void setBirthday(String birthday) { this.birthday = birthday; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ContactDto {
        private String resourceName;
        private String firstName;
        private String lastName;
        private List<String> emails;
        private List<String> phones;
        private String formattedBirthday;
        private String notes;
        private String photoUrl;

        public String getResourceName() { return resourceName; }
        public void setResourceName(String resourceName) { this.resourceName = resourceName; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public List<String> getEmails() { return emails; }
        public void setEmails(List<String> emails) { this.emails = emails; }

        public List<String> getPhones() { return phones; }
        public void setPhones(List<String> phones) { this.phones = phones; }

        public String getFormattedBirthday() { return formattedBirthday; }
        public void setFormattedBirthday(String formattedBirthday) { this.formattedBirthday = formattedBirthday; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }

        public String getPhotoUrl() { return photoUrl; }
        public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    }
}
