package com.example.demo.presentation.web.viewmodel;

import com.example.demo.presentation.web.viewmodel.BookCardViewModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookPageViewModel {
    private List<BookCardViewModel> cards;
    private int currentPage;
    private int totalPages;
    private String url;

    public boolean hasElements() {
        return totalPages > 0;
    }
}
