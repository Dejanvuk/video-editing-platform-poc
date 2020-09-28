package com.dejanvuk.microservices.user.config.oauth2;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.Map;

public class OAuth2ProviderFactory {
    public static OAuth2UserAttributes getOAuth2UserAttributesByProvider(OAuth2AuthenticationToken oAuth2AuthenticationToken, Map<String, Object> userAttributes) {
        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        OAuth2UserAttributes oAuth2UserAttributes = null;
        switch(registrationId) {
            case "google":
                oAuth2UserAttributes = new GoogleOAuth2UserAttribute(userAttributes);
                break;
            case "facebook":
                oAuth2UserAttributes = new FacebookOAuth2UserAttribute(userAttributes);
                break;
            case "github":
                oAuth2UserAttributes = new GithubOAuth2UserAttribute(userAttributes);
                break;
            default:
                throw new RuntimeException("Provider: " + registrationId + " is not supported yet!");
        }

        OAuth2UserAttributes.validateOAuth2UserAttributes(oAuth2UserAttributes);

        return oAuth2UserAttributes;
    }
}
