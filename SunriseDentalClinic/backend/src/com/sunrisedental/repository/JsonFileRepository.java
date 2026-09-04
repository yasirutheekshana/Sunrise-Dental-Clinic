package com.sunrisedental.repository;

import com.sunrisedental.util.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generic repository managing persistent entities stored in JSON files.
 * Provides thread-safe read and write operations.
 *
 * @param <T> Entity type
 */
public abstract class JsonFileRepository<T> {
    private final File file;
    private final Class<T> entityClass;
    private final Object lock = new Object();

    public JsonFileRepository(String filePath, Class<T> entityClass) {
        this.file = new File(filePath);
        this.entityClass = entityClass;
        ensureFileExists();
    }

    private void ensureFileExists() {
        synchronized (lock) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (!file.exists()) {
                    file.createNewFile();
                    JsonUtil.writeToFile(file, new ArrayList<T>());
                }
            } catch (Exception e) {
                System.err.println("Failed to initialize file: " + file.getAbsolutePath() + ": " + e.getMessage());
            }
        }
    }

    public List<T> findAll() {
        synchronized (lock) {
            return JsonUtil.fromJsonList(file, entityClass);
        }
    }

    public void saveAll(List<T> entities) {
        synchronized (lock) {
            JsonUtil.writeToFile(file, entities != null ? entities : Collections.emptyList());
        }
    }

    public void add(T entity) {
        if (entity == null) return;
        synchronized (lock) {
            List<T> list = findAll();
            list.add(entity);
            saveAll(list);
        }
    }

    public File getFile() {
        return file;
    }
}
