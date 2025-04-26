package com.example.demo.Core.Book.Utilities.ReadingProgress.Model;

import com.example.demo.Core.Book.Model.Book;
import com.example.demo.Security.User.Model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reading_progress")
@Data
@NoArgsConstructor
public class ReadingProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private int page;

    @Column(nullable = false)
    private int screen;
}