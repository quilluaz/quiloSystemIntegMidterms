package com.quilo.systemintegmidterms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() throws Exception {
        InputStream in = getClass().getResourceAsStream("/credentials/client_secret.json");
        if (in == null) {
            throw new IllegalStateException("client_secret.json file not found in /credentials/");
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(in, new TypeReference<Map<String, Object>>() {});
        Map<String, Object> webMap = (Map<String, Object>) jsonMap.get("web");

        String clientId = (String) webMap.get("client_id");
        String clientSecret = (String) webMap.get("client_secret");
        List<String> redirectUris = (List<String>) webMap.get("redirect_uris");
        String redirectUri = redirectUris.get(0); // Use the first redirect URI from the list

        ClientRegistration registration = ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(redirectUri)
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .clientName("Google")
                .scope("openid", "profile", "email", "https://www.googleapis.com/auth/contacts")
                .build();

        return new InMemoryClientRegistrationRepository(registration);
    }
}
