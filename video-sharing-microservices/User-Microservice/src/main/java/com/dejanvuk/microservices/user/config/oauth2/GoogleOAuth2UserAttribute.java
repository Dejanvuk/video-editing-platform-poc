package com.dejanvuk.microservices.user.config.oauth2;

import java.util.Map;

public class GoogleOAuth2UserAttribute extends OAuth2UserAttributes {

    public GoogleOAuth2UserAttribute(Map<String, Object> userAttributes) {
        super(userAttributes);
    }

    @Override
    public String getPictureUrl() {
        return (String) getUserAttributes().get("picture");
    }
}
