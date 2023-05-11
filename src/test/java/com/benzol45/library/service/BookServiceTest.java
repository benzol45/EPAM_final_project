package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.exception.ObjectNotFoundException;
import com.benzol45.library.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Test
    void getBase64Cover() throws IOException {
        BookService bookService = new BookService(null);

        String tempFolder = org.assertj.core.util.Files.temporaryFolderPath();
        String filename = tempFolder + UUID.randomUUID() + ".jpg";
        byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9};
        Files.write(Path.of(filename),bytes);
        Book book = Book.builder().imagePath(filename).build();
        assertEquals(Base64.getEncoder().encodeToString(bytes), bookService.getBase64Cover(book));

        Book bookWithIncorrectPath = Book.builder().imagePath("incorrect").build();
        assertThrows(ObjectNotFoundException.class, () -> bookService.getBase64Cover(bookWithIncorrectPath));
    }

    @Test
    void setExternalFileAsCoverImage() {
        BookService bookService = new BookService(null);
        assertThrows(IllegalStateException.class,()->bookService.setExternalFileAsCoverImage(new Book(),"incorrectURL"));

    }
}