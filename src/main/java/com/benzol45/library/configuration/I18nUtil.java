package com.benzol45.library.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nUtil {
    private final MessageSource messageSource;

    @Autowired
    public I18nUtil(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String prefix, String key, Locale locale) {
        String fullKey = prefix + "." + key;
        return messageSource.getMessage(fullKey, null, locale);
    }
}
