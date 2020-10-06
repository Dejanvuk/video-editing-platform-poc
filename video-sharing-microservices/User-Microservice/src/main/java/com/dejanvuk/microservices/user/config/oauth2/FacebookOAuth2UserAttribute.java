package com.dejanvuk.microservices.user.config.oauth2;

import java.util.Map;

public class FacebookOAuth2UserAttribute extends OAuth2UserAttributes {

    public FacebookOAuth2UserAttribute(Map<String, Object> userAttributes) {
        super(userAttributes);
    }

    @Override
    public String getPictureUrl() {
        Map<String, Object> userAttributes = getUserAttributes();

        if (userAttributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) userAttributes.get("picture");
            if (pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }

        return null;
    }
}
