package com.benzol45.library.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;

//Курс Марселя от Maxima занятие 30
//Три вариант работы с сообщениями
// - в шаблоне th:text="#{ИмяСвойстваИзНабораСообщений}", например в root.html
// - в коде. Locale locale = LocaleContextHolder.getLocale(); @Autowired MessageSource messageSource; messageSource.getMessage("ИмяСвойстваИзНабораСообщений",null,locale); пример в FineService
// - сообщения валидатора. В наборе ресурсов указывается код ошибки любого нужного уровня (видно в BindingResult у каждой ошибки в кодах), например в validatorMessage_en.properties

@Configuration
public class I18nConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setCookieName("lang");
        cookieLocaleResolver.setCookieMaxAge(Duration.ofDays(365));
        cookieLocaleResolver.setCookieHttpOnly(true);
        cookieLocaleResolver.setCookiePath("/");

        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(
                "messages/accountLibrarian",
                "messages/accountReader",
                "messages/book",
                "messages/bookCatalog",
                "messages/bookEdit",
                "messages/bookGive",
                "messages/error",
                "messages/fine",
                "messages/fineService",
                "messages/isbn",
                "messages/login",
                "messages/newUser",
                "messages/root",
                "messages/userAdministration",
                "messages/validatorMessage"
                );
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
