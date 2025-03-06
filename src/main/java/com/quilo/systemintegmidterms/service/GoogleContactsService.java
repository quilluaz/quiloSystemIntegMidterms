package com.quilo.systemintegmidterms.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.http.HttpCredentialsAdapter;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GoogleContactsService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GoogleContactsService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    private PeopleService getPeopleService(OAuth2AuthenticationToken authentication) throws Exception {
        // Use the authorized client service to load the OAuth2 client
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        // Get the access token provided from the login flow
        String accessToken = client.getAccessToken().getTokenValue();

        // Create GoogleCredentials from the access token
        AccessToken token = new AccessToken(accessToken, null);
        GoogleCredentials credentials = GoogleCredentials.create(token);

        return new PeopleService.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Your App Name")
                .build();
    }

    public List<Person> getAllContacts(OAuth2AuthenticationToken authentication) throws Exception {
        PeopleService peopleService = getPeopleService(authentication);
        ListConnectionsResponse response = peopleService.people().connections()
                .list("people/me")
                .setPageSize(100)
                .setPersonFields("names,emailAddresses,phoneNumbers")
                .execute();
        return response.getConnections();
    }

    public Person createContact(OAuth2AuthenticationToken authentication, Person contact) throws Exception {
        PeopleService peopleService = getPeopleService(authentication);
        return peopleService.people().createContact(contact).execute();
    }

    public Person updateContact(OAuth2AuthenticationToken authentication, String resourceName, Person contact) throws Exception {
        PeopleService peopleService = getPeopleService(authentication);
        return peopleService.people().updateContact(resourceName, contact)
                .setUpdatePersonFields("names,emailAddresses,phoneNumbers")
                .execute();
    }

    public void deleteContact(OAuth2AuthenticationToken authentication, String resourceName) throws Exception {
        PeopleService peopleService = getPeopleService(authentication);
        peopleService.people().deleteContact(resourceName).execute();
    }
}
