package com.example.demo.infrastructure.storage.minio;

import lombok.Getter;

@Getter
public enum StorageBucket {
    BOOK("books"),
    COVERS("covers");

    private final String source;

    StorageBucket(String source) {
        this.source = source;
    }
}
