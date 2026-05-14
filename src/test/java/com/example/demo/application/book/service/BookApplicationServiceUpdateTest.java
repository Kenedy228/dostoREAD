package com.example.demo.application.book.service;

import com.example.demo.application.book.command.BookCommand;
import com.example.demo.application.library.service.ReadingProgressApplicationService;
import com.example.demo.application.user.service.UserApplicationService;
import com.example.demo.infrastructure.persistence.jpa.entity.BookEntity;
import com.example.demo.infrastructure.persistence.jpa.repository.BookJpaRepository;
import com.example.demo.infrastructure.persistence.jpa.mapper.BookCardViewMapper;
import com.example.demo.infrastructure.storage.minio.MinioStorageService;
import com.example.demo.presentation.web.viewmodel.BookPageViewModel;
import com.example.demo.presentation.web.viewmodel.ReadingPositionViewModel;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BookApplicationServiceUpdateTest {

    @Test
    void keepsExistingFilesWhenUpdateDoesNotIncludeNewUploads() throws Exception {
        InMemoryBookRepository repository = new InMemoryBookRepository();
        BookEntity existing = new BookEntity();
        existing.setId(1);
        existing.setTitle("Old title");
        existing.setAuthor("Old author");
        existing.setPathToBook("old-book.epub");
        existing.setPathToCover("old-cover.jpg");
        existing.setDescription("Description");
        existing.setLicense("License");
        existing.setPublishDate(LocalDate.of(2025, 1, 1));
        existing.setPublisher("Publisher");
        existing.setPageCount((short) 120);
        repository.put(existing);

        BookApplicationService service = new BookApplicationService(
                repository.proxy(),
                new NoopMinioStorageService(),
                stubUserService(),
                null,
                null,
                new GenreApplicationService(null)
        );

        BookCommand command = new BookCommand(
                "New title",
                "New author",
                List.of("Фантастика"),
                null,
                null,
                "New description",
                null,
                "New license",
                LocalDate.of(2025, 2, 1),
                "New publisher",
                (short) 220
        );

        BookEntity saved = service.update(1, command, List.of());

        assertThat(saved.getPathToBook()).isEqualTo("old-book.epub");
        assertThat(saved.getPathToCover()).isEqualTo("old-cover.jpg");
        assertThat(saved.getTitle()).isEqualTo("New title");
        assertThat(saved.getAuthor()).isEqualTo("New author");
    }

    private UserApplicationService stubUserService() {
        return new UserApplicationService(null, new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(rawPassword.toString());
            }
        });
    }

    private static final class NoopMinioStorageService extends MinioStorageService {
        private NoopMinioStorageService() {
            super("http://localhost:9000", "access", "secret");
        }

        @Override
        public String uploadFile(String bucketName, org.springframework.web.multipart.MultipartFile file) {
            throw new AssertionError("uploadFile should not be called when no files are provided");
        }

        @Override
        public String getFileUrl(String path, String bucketName) {
            return path;
        }

        @Override
        public InputStream getFile(String path, String bucketName) {
            return InputStream.nullInputStream();
        }
    }

    private static final class InMemoryBookRepository implements InvocationHandler {
        private final Map<Integer, BookEntity> store = new LinkedHashMap<>();
        private final BookJpaRepository proxy = (BookJpaRepository) Proxy.newProxyInstance(
                BookJpaRepository.class.getClassLoader(),
                new Class<?>[]{BookJpaRepository.class},
                this
        );

        BookJpaRepository proxy() {
            return proxy;
        }

        void put(BookEntity book) {
            store.put(book.getId(), book);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findBookById" -> store.get(args[0]);
                case "save" -> {
                    BookEntity book = (BookEntity) args[0];
                    store.put(book.getId(), book);
                    yield book;
                }
                case "findBookByAuthorAndTitle" -> store.values().stream()
                        .filter(book -> book.getAuthor().equals(args[0]) && book.getTitle().equals(args[1]))
                        .findFirst()
                        .orElse(null);
                case "findAll" -> new ArrayList<>(store.values());
                case "deleteById", "flush" -> null;
                default -> throw new UnsupportedOperationException("Method not implemented in test stub: " + method.getName());
            };
        }
    }
}
