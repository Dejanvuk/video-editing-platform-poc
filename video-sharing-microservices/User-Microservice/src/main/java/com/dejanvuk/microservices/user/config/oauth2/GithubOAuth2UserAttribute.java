package com.dejanvuk.microservices.user.config.oauth2;

import java.util.Map;

public class GithubOAuth2UserAttribute extends OAuth2UserAttributes {

    public GithubOAuth2UserAttribute(Map<String, Object> userAttributes) {
        super(userAttributes);
    }

    @Override
    public String getPictureUrl() {
        return (String) getUserAttributes().get("avatar_url");
    }
}
