package com.example.cacex.repository;

import com.example.cacex.model.StateDocument;

import java.util.List;
import java.util.Optional;

/**
 * Storage abstraction for persisted plan state documents.
 */
public interface StateRepository {

    Optional<StateDocument> find(String id, String typeOfItem);

    List<StateDocument> list(String typeOfItem, String scope);

    void upsert(StateDocument document);

    void delete(String id, String typeOfItem);
}
