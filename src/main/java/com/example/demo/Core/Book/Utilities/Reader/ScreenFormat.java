package com.example.demo.Core.Book.Utilities.Reader;

import lombok.Getter;

@Getter
public enum ScreenFormat {
    STANDARD(700, 25.6, 70), INCREASED(700, 38.4, 60);

    private final double maxHeightOfPage;
    private final double heightOfColumn;
    private final int maxCharsPerLine;
    private final int numberOfColumns;

    ScreenFormat(double maxHeightOfPage, double heightOfColumn, int maxCharsPerLine) {
        this.maxHeightOfPage = maxHeightOfPage;
        this.heightOfColumn = heightOfColumn;
        this.maxCharsPerLine = maxCharsPerLine;
        this.numberOfColumns = (int) ((int)maxHeightOfPage / heightOfColumn);
    }
}
