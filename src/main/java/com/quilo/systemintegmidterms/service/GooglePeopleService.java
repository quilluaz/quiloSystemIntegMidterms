package com.quilo.systemintegmidterms.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.google.api.services.people.v1.model.Biography;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GooglePeopleService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GooglePeopleService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    private PeopleService getPeopleService(OAuth2AuthenticationToken authentication)
            throws IOException, GeneralSecurityException {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        return new PeopleService.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                request -> request.getHeaders().setAuthorization("Bearer " + client.getAccessToken().getTokenValue())
        )
                .setApplicationName("Google Contacts Integration")
                .build();
    }

    // Fetch all contacts with additional fields
    public List<Person> getContacts(OAuth2AuthenticationToken authentication) {
        try {
            var response = getPeopleService(authentication)
                    .people().connections()
                    .list("people/me")
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata,birthdays,biographies,photos")
                    .execute();
            return response.getConnections() != null
                    ? response.getConnections()
                    : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Fetch a single contact with additional fields
    public Person getContactByResourceName(OAuth2AuthenticationToken authentication, String resourceName) {
        try {
            return getPeopleService(authentication).people()
                    .get(resourceName)
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata,birthdays,biographies,photos")
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch contact", e);
        }
    }

    // Create a new contact with multiple emails, phones, birthday, and notes
    public void createContact(OAuth2AuthenticationToken authentication,
                              String firstName,
                              String lastName,
                              List<String> emails,
                              List<String> phones,
                              String birthday,
                              String notes) {
        try {
            Person person = new Person();
            person.setNames(Collections.singletonList(
                    new Name().setGivenName(firstName).setFamilyName(lastName)
            ));
            if (emails != null && !emails.isEmpty()) {
                person.setEmailAddresses(
                        emails.stream().map(email -> new EmailAddress().setValue(email))
                                .collect(Collectors.toList())
                );
            }
            if (phones != null && !phones.isEmpty()) {
                person.setPhoneNumbers(
                        phones.stream().map(phone -> new PhoneNumber().setValue(phone))
                                .collect(Collectors.toList())
                );
            }
            if (birthday != null && !birthday.isEmpty()) {
                String[] parts = birthday.split("-");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    // Use the People API Date model
                    com.google.api.services.people.v1.model.Date birthdayDate =
                            new com.google.api.services.people.v1.model.Date();
                    birthdayDate.setYear(year);
                    birthdayDate.setMonth(month);
                    birthdayDate.setDay(day);
                    person.setBirthdays(Collections.singletonList(
                            new Birthday().setDate(birthdayDate)
                    ));
                }
            }
            if (notes != null && !notes.isEmpty()) {
                person.setBiographies(Collections.singletonList(new Biography().setValue(notes)));
            }
            getPeopleService(authentication)
                    .people()
                    .createContact(person)
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create contact", e);
        }
    }

    // Update existing contact with multiple emails, phones, birthday, and notes
    public void updateContact(OAuth2AuthenticationToken authentication,
                              String resourceName,
                              String firstName,
                              String lastName,
                              List<String> newEmails,
                              List<String> newPhones,
                              String birthday,
                              String notes) {
        try {
            Person existing = getPeopleService(authentication).people()
                    .get(resourceName)
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata,birthdays,biographies")
                    .execute();

            existing.setNames(Collections.singletonList(
                    new Name().setGivenName(firstName).setFamilyName(lastName)
            ));
            if (newEmails != null && !newEmails.isEmpty()) {
                existing.setEmailAddresses(
                        newEmails.stream().map(email -> new EmailAddress().setValue(email))
                                .collect(Collectors.toList())
                );
            }
            if (newPhones != null && !newPhones.isEmpty()) {
                existing.setPhoneNumbers(
                        newPhones.stream().map(phone -> new PhoneNumber().setValue(phone))
                                .collect(Collectors.toList())
                );
            }
            if (birthday != null && !birthday.isEmpty()) {
                String[] parts = birthday.split("-");
                if (parts.length == 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    com.google.api.services.people.v1.model.Date birthdayDate =
                            new com.google.api.services.people.v1.model.Date();
                    birthdayDate.setYear(year);
                    birthdayDate.setMonth(month);
                    birthdayDate.setDay(day);
                    existing.setBirthdays(Collections.singletonList(
                            new Birthday().setDate(birthdayDate)
                    ));
                }
            }
            if (notes != null && !notes.isEmpty()) {
                existing.setBiographies(Collections.singletonList(new Biography().setValue(notes)));
            }

            getPeopleService(authentication).people()
                    .updateContact(resourceName, existing)
                    .setUpdatePersonFields("names,emailAddresses,phoneNumbers,birthdays,biographies")
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update contact", e);
        }
    }

    // Delete contact
    public void deleteContact(OAuth2AuthenticationToken authentication, String resourceName) {
        try {
            getPeopleService(authentication).people()
                    .deleteContact(resourceName)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete contact", e);
        }
    }
}
