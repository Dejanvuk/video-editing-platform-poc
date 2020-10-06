package com.dejanvuk.microservices.user.utility;

import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class CookieUtility {
    public String getCookieValue(MultiValueMap<String, HttpCookie> cookieMap, String cookieName) {
        List<HttpCookie> cookies = cookieMap.get(cookieName);
        if (cookies == null) {
            return null;
        }

        return cookies.stream().findFirst().map(HttpCookie::getValue).orElse(null);
    }

    public void deleteCookie(ServerHttpResponse response, MultiValueMap<String, HttpCookie> cookieMap, String cookieName) {
        List<HttpCookie> cookies = cookieMap.get(cookieName);
        for (HttpCookie cookie : cookies) {
            var currentCookieName = cookie.getName();
            if (currentCookieName.equals(cookieName)) {
                ResponseCookie.ResponseCookieBuilder rcb = ResponseCookie.from(currentCookieName, "");
                rcb.path("/");
                rcb.maxAge(0);
                response.addCookie(rcb.build());
            }
        }
    }

    public void addCookie(ServerHttpResponse response, String cookieName, String cookieValue, String path, int age) {
        ResponseCookie.ResponseCookieBuilder rcb = ResponseCookie.from(cookieName, cookieValue);
        rcb.path("/");
        rcb.httpOnly(true);
        rcb.maxAge(120);
        response.addCookie(rcb.build());
    }
}
