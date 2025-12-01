package com.example.cacex.service.plan.stratagy;

import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.exception.UnsupportedFilePathException;
import com.example.cacex.model.FileCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileParsingStrategyFactoryTest {

    @Mock
    private FileParsingStrategy sideStrategy;

    @Test
    void resolveByPathMatchesStrategy() {
        when(sideStrategy.getCategory()).thenReturn(FileCategory.SIDE);
        when(sideStrategy.supports(Path.of("side.json"))).thenReturn(true);
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));

        factory.resolve(Path.of("side.json"));
    }

    @Test
    void resolveByPathThrowsWhenUnsupported() {
        when(sideStrategy.getCategory()).thenReturn(FileCategory.SIDE);
        when(sideStrategy.supports(Path.of("txn.json"))).thenReturn(false);
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));

        assertThrows(UnsupportedFilePathException.class,
                () -> factory.resolve(Path.of("txn.json")));
    }

    @Test
    void resolveByCategoryThrowsWhenUnsupported() {
        when(sideStrategy.getCategory()).thenReturn(FileCategory.SIDE);
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));

        assertThrows(UnsupportedFileCategoryException.class,
                () -> factory.resolve(FileCategory.TRANSACTION));
    }

    @Test
    void resolveByCategoryReturnsStrategy() {
        when(sideStrategy.getCategory()).thenReturn(FileCategory.SIDE);
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));

        assertEquals(sideStrategy, factory.resolve(FileCategory.SIDE));
    }
}
