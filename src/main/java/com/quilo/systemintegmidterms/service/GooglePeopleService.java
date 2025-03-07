package com.quilo.systemintegmidterms.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

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
                request -> request.getHeaders()
                        .setAuthorization("Bearer " + client.getAccessToken().getTokenValue())
        )
                .setApplicationName("Google Contacts Integration")
                .build();
    }

    // Fetch all contacts
    public List<Person> getContacts(OAuth2AuthenticationToken authentication) {
        try {
            ListConnectionsResponse response = getPeopleService(authentication)
                    .people().connections()
                    .list("people/me")
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata")
                    .execute();

            return response.getConnections() != null
                    ? response.getConnections()
                    : Collections.emptyList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Fetch a single contact
    public Person getContactByResourceName(OAuth2AuthenticationToken authentication, String resourceName) {
        try {
            return getPeopleService(authentication).people()
                    .get(resourceName)
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata")
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch contact", e);
        }
    }

    // Create a new contact with first/last name
    public void createContact(OAuth2AuthenticationToken authentication,
                              String firstName,
                              String lastName,
                              String email,
                              String phone) {
        try {
            Person person = new Person()
                    .setNames(Collections.singletonList(
                            new Name()
                                    .setGivenName(firstName)
                                    .setFamilyName(lastName)
                    ))
                    .setEmailAddresses(Collections.singletonList(
                            new EmailAddress().setValue(email)
                    ))
                    .setPhoneNumbers(Collections.singletonList(
                            new PhoneNumber().setValue(phone)
                    ));

            getPeopleService(authentication)
                    .people()
                    .createContact(person)
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create contact", e);
        }
    }

    // Update existing contact
    public void updateContact(OAuth2AuthenticationToken authentication,
                              String resourceName,
                              String firstName,
                              String lastName,
                              String newEmail,
                              String newPhone) {
        try {
            Person existing = getPeopleService(authentication)
                    .people()
                    .get(resourceName)
                    .setPersonFields("names,emailAddresses,phoneNumbers,metadata")
                    .execute();

            existing.setNames(Collections.singletonList(
                    new Name()
                            .setGivenName(firstName)
                            .setFamilyName(lastName)
            ));
            existing.setEmailAddresses(Collections.singletonList(
                    new EmailAddress().setValue(newEmail)
            ));
            existing.setPhoneNumbers(Collections.singletonList(
                    new PhoneNumber().setValue(newPhone)
            ));

            getPeopleService(authentication)
                    .people()
                    .updateContact(resourceName, existing)
                    .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                    .execute();

        } catch (Exception e) {
            throw new RuntimeException("Failed to update contact", e);
        }
    }

    // Delete contact
    public void deleteContact(OAuth2AuthenticationToken authentication, String resourceName) {
        try {
            getPeopleService(authentication)
                    .people()
                    .deleteContact(resourceName)
                    .execute();
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete contact", e);
        }
    }
}
