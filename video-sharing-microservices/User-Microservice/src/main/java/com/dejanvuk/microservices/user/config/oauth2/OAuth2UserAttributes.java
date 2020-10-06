package com.dejanvuk.microservices.user.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

@Getter
@Setter
public abstract class OAuth2UserAttributes {
    private Map<String, Object> userAttributes;

    public OAuth2UserAttributes(Map<String, Object> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public String getId() {
        return Integer.toString((Integer) userAttributes.get("id"));
    }

    public String getName() {
        return (String) userAttributes.get("name");
    }

    public String getEmail() {
        return (String) userAttributes.get("email");
    }

    public abstract String getPictureUrl();

    public static void validateOAuth2UserAttributes(OAuth2UserAttributes oAuth2UserAttributes) {
        if (oAuth2UserAttributes.getEmail() == null)
            throw new BadCredentialsException("Github Email is private,please add a public email or Sign up using a different provider!");
    }
}
