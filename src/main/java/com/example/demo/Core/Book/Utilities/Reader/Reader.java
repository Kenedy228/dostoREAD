package com.example.demo.Core.Book.Utilities.Reader;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class Reader {
    public List<List<String>> load(InputStream bookFile, ScreenFormat screenFormat) throws IOException {
        EpubReader reader = new EpubReader();

        Book book = reader.readEpub(bookFile);

        List<Resource> resources = book.getContents();

        List<String> allLines = new ArrayList<>();

        for (Resource resource : resources) {
            String html = new String(resource.getData(), StandardCharsets.UTF_8);
            String text = Jsoup.parse(html)
                    .body()
                    .html()
                    .replaceAll("<br\\s*/?>", "\n")
                    .replaceAll("</p>", "\n</p>")
                    .replaceAll("<[^>]+>", "")
                    .replaceAll("&nbsp;", " ")
                    .replaceAll("Спасибо, что скачали книгу в бесплатной электронной библиотеке Royallib.com", "")
                    .replaceAll("Оставить отзыв о книге", "")
                    .replaceAll("Все книги автора", "")
                    .replaceAll("Эта же книга в других форматах", "")
                    .replaceAll("Приятного чтения!", "")
                    .trim();

            String[] lines = text.split("\n");

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    allLines.add(line.trim());
                }
            }
        }

        List<List<String>> pages = new ArrayList<>();
        List<String> currentPage = new ArrayList<>();

        int currentColumnsCounter = 0;
        StringBuilder currentLine = new StringBuilder();
        StringBuilder currentIndent = new StringBuilder();

        for (String paragraph : allLines) {
            String[] words = paragraph.split(" ");

            for (String word : words) {
                if (currentLine.length() + word.length() <= screenFormat.getMaxCharsPerLine()) {
                    currentLine.append(word).append(" ");
                } else {
                    currentColumnsCounter++;
                    currentIndent.append(currentLine);
                    currentLine = new StringBuilder();
                    currentLine.append(word).append(" ");

                    if (currentColumnsCounter == screenFormat.getNumberOfColumns()) {
                        currentPage.add(currentIndent.toString());
                        pages.add(currentPage);
                        currentPage = new ArrayList<>();
                        currentIndent = new StringBuilder();
                        currentColumnsCounter= 0;
                    }
                }
            }

            if (currentLine.length() >= 0) {
                currentColumnsCounter++;
                currentIndent.append(currentLine);
                currentPage.add(currentIndent.toString());
                currentIndent = new StringBuilder();
                currentLine = new StringBuilder();

                if (currentColumnsCounter == screenFormat.getNumberOfColumns()) {
                    currentPage.add(currentIndent.toString());
                    pages.add(currentPage);
                    currentPage = new ArrayList<>();
                    currentColumnsCounter = 0;
                }
            }
        }

        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }
}