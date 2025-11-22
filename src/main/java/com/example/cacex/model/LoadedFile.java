package com.example.cacex.model;

import java.nio.file.Path;
import java.util.Objects;

public class LoadedFile {

    private final FileCategory category;
    private final String key;
    private final Path path;
    private final Object payload;

    public LoadedFile(FileCategory category, String key, Path path, Object payload) {
        this.category = category;
        this.key = key;
        this.path = path;
        this.payload = payload;
    }

    public FileCategory getCategory() {
        return category;
    }

    public String getKey() {
        return key;
    }

    public Path getPath() {
        return path;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoadedFile that = (LoadedFile) o;
        return category == that.category && Objects.equals(key, that.key)
                && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, key, payload);
    }

    @Override
    public String toString() {
        return "LoadedFile{" +
                "category=" + category +
                ", key='" + key + '\'' +
                ", path=" + path +
                ", payload=" + payload +
                '}';
    }
}
