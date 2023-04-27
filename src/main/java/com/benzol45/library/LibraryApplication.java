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

    //TODO перенести в отдельный конфиг
    @Bean
    public RestTemplate restTemplate() {
        return (new RestTemplateBuilder()).build();
    }

    //Мультиязык
    //Покрытие тестами

    //TODO на страницу инофрмация доступна ли книга и если нет - когда будет, И ДЛЯ АДМИНА И БИБЛИОТЕКАРЯ у кого на руках и ??? кнопку заказать/выдать
    //TODO писать отдельную историю вида "книга - дата заказа - дата выдачи - дата возврата" для анализа каких книг не хватает
    //TODO добавить на страницах выход на root page
    //TODO таблица "прочитанные книги" по читателям. кто - какая книга - когда, может совместить со статистикой
    //TODO Если прочитал книгу - можешь оставить рейтинг для книги и сортировать и фильтровать по рейтингу список

    //TODO ??? Свойства - рабочее время и дни
//    library.open-time=9:00AM
//    library.close-time=7:00PM
//    library.weekend=[6,7]
    //TODO ??? штрафы записывать в базу и отдельно оформить получение ? (пока не храним, только рассчитываем, считам что получили сразу)

}
