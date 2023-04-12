package com.benzol45.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return (new RestTemplateBuilder()).build();
    }

    //TODO каскадность при удалении пользователя. Наверно надо очищать - что бы сохранить факт выдачи книг и заказов
    //TODO отдельная страница для загрузки по ISBN
    //TODO отдельная страница для загрузки/поиска картинки на обложку
    //TODO страница книги где все поля + обложка + кнопка заказать + инофрмация доступна ли книга и если нет - когда будет
}
