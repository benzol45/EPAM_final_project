package com.benzol45.library.service;

import com.benzol45.library.entity.Book;
import com.benzol45.library.repository.BookRepository;
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

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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
        //TODO Не нашли - залогируй и верни пустое
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
            throw new IllegalStateException(e);
        }
        String base64String = Base64.getEncoder().encodeToString(bytes);
        return base64String;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void setMultipartFileAsCoverImage(Book book, MultipartFile multipartFile) {
        UUID uuid = UUID.randomUUID();
        String fileName = getFolderForImages() + uuid.toString() + ".jpg";
        try {
            multipartFile.transferTo(Path.of(fileName));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        book.setImagePath(fileName);
        bookRepository.save(book);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public void setExternalFileAsCoverImage(Book book, String externalCoverUrl) {
        UUID uuid = UUID.randomUUID();
        String fileName = getFolderForImages() + uuid.toString() + ".jpg";

        try {
            InputStream inputStream = new URL(externalCoverUrl).openStream();
            Files.copy(inputStream, Path.of(fileName), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        book.setImagePath(fileName);
        bookRepository.save(book);
    }

    private String getFolderForImages() {
        String homeDir = System.getProperty("user.home");
        String folderForImages = homeDir + "/library_images/";
        if (Files.notExists(Path.of(folderForImages))) {
            try {
                Files.createDirectory(Path.of(folderForImages));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return folderForImages;
    }
}
