package com.benzol45.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    //Выписать в презентацию "интересные фишки"
    //Пройтись по полезностям и выписать ссылки на примеры

    //ISSUE LIST:
    //Какие книги больше всго раз брали, какие дольше всего ждали, какие наибольший рейтинг, какие наименьший
    //писать отдельную историю вида "книга - дата заказа - дата выдачи - дата возврата" для анализа каких книг не хватает
    //таблица "прочитанные книги" по читателям. кто - какая книга - когда, может совместить со статистикой


    //??? Свойства - рабочее время и рабочие дни, что то в стиле  library.open-time=9:00AM library.close-time=7:00PM library.weekend=[6,7]
    //??? штрафы записывать в базу и отдельно оформить получение
}
