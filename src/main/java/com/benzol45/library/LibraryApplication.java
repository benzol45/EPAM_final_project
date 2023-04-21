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

    //Перехват эксепшенов https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc
    //                      https://www.baeldung.com/spring-security-custom-access-denied-page
    //Мультиязык
    //Покрытие тестами

    //TODO отдельная страница для загрузки по ISBN
    //TODO отдельная страница для загрузки/поиска картинки на обложку
    //TODO страница книги где все поля + обложка + кнопка заказать + инофрмация доступна ли книга и если нет - когда будет, И ДЛЯ АДМИНА И БИБЛИОТЕКАРЯ у кого на руках и например последние 5 кто брали
    //TODO писать отдельную историю вида "книга - дата заказа - дата выдачи - дата возврата" для анализа каких книг не хватает

    //TODO ??? может на страницах делать книги и/или пользователей гиперссылками
    //TODO ??? Свойства - рабочее время и дни
//    library.open-time=9:00AM
//    library.close-time=7:00PM
//    library.weekend=[6,7]
    //TODO ??? штрафы записывать в базу и отдельно оформить получение ? (пока не храним, только рассчитываем, считам что получили сразу)

}
