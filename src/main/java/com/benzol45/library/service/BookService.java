package com.benzol45.library.service;

import com.benzol45.library.configuration.actuator.Metrics;
import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contains methods for processing books (create/edit/delete/get/find)
 */

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;
    private final Metrics metrics;

    @Autowired
    public BookService(BookRepository bookRepository, Metrics metrics) {
        this.bookRepository = bookRepository;
        this.metrics = metrics;
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public List<Book> getListWithPaging(String filter, Pageable pageable) {
        if (filter==null || filter.isEmpty()) {
            return bookRepository.findAll(pageable).getContent();
        } else {
            return bookRepository.findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(filter,filter,pageable).getContent();
        }
    }

    public int getTotalPages(String filter, Pageable pageable) {
        if (filter==null || filter.isEmpty()) {
            return bookRepository.findAll(pageable).getTotalPages();
        } else {
            return bookRepository.findAllByTitleContainsIgnoreCaseOrAuthorContainsIgnoreCase(filter,filter,pageable).getTotalPages();
        }
    }

    public Book getById(Long id) {
        log.debug("Can't find book with given id" + id);
        return bookRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Can't find book with id " + id));
    }

    public Optional<Book> getByISBN(String isbn) {
        return bookRepository.findByISBN(isbn).stream().findFirst();
    }

    public String getBase64Cover(Book book) {
        if (book.getImagePath()==null || book.getImagePath().isBlank()) {
            return "";
        }

        Path coverImage = Path.of(book.getImagePath());
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(coverImage);
        } catch (IOException e) {
            log.warn("Can't read cover image file " + coverImage.toAbsolutePath() + " for book with id " + book.getId());
            throw new IllegalStateException(e);
        }
        String base64String = Base64.getEncoder().encodeToString(bytes);
        return base64String;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Book save(Book book) {
        bookRepository.save(book);
        metrics.refreshBookCopyCounter();
        return book;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
        metrics.refreshBookCopyCounter();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void setMultipartFileAsCoverImage(Book book, MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        String fileName = getFolderForStoringImages() + uuid.toString() + ".jpg";
        try {
            multipartFile.transferTo(Path.of(fileName));
        } catch (IOException e) {
            log.warn("Can't write cover image file " + fileName + " for book with id " + book.getId());
            throw new IllegalStateException(e);
        }

        book.setImagePath(fileName);
        bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void setExternalFileAsCoverImage(Book book, String externalCoverUrl) {
        UUID uuid = UUID.randomUUID();
        String fileName = getFolderForStoringImages() + uuid.toString() + ".jpg";

        try {
            InputStream inputStream = new URL(externalCoverUrl).openStream();
            Files.copy(inputStream, Path.of(fileName), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            log.warn("Can't transfer external cover image file from " + externalCoverUrl + " to " + fileName + " for book with id " + book.getId());
            throw new IllegalStateException(e);
        }

        book.setImagePath(fileName);
        bookRepository.save(book);
    }

    private String getFolderForStoringImages() {
        String homeDir = System.getProperty("user.home");
        String folderForImages = homeDir + "/library_images/";
        if (Files.notExists(Path.of(folderForImages))) {
            try {
                Files.createDirectory(Path.of(folderForImages));
            } catch (IOException e) {
                log.warn("Can't create folder for storing cover images " + folderForImages);
                throw new IllegalStateException(e);
            }
        }
        return folderForImages;
    }

    @PreAuthorize("hasRole('READER')")
    public void setRatingForBook(Book book, double rating) {
        book.setRating(rating);
        bookRepository.save(book);
    }
}
