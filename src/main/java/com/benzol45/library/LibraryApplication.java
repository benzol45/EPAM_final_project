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

    //Личный кабинет библиотекаря -
    //      заказы и выдача по ним (показывать какие можно);
    //      !ready выдача без заказа домой и в читальный зал (контроль возможности);
    //      список выданного с возможностью вернуть (отмечать просрочки) если просрочено - наложить штраф

    //TODO при заказе читателя показывать доступна книга или нет и если нет - когда будет доступна
    //TODO отдельная страница для загрузки по ISBN
    //TODO отдельная страница для загрузки/поиска картинки на обложку
    //TODO страница книги где все поля + обложка + кнопка заказать + инофрмация доступна ли книга и если нет - когда будет, у кого на руках и например последние 5 кто брали

    //TODO писать отдельную историю вида "книга - дата заказа - дата выдачи" для анализа каких книг не хватает
}
