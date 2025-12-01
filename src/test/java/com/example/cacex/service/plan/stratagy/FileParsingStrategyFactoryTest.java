package com.example.cacex.service.plan.stratagy;

import com.example.cacex.exception.UnsupportedFileCategoryException;
import com.example.cacex.exception.UnsupportedFilePathException;
import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileParsingStrategyFactoryTest {

    @Test
    void resolveByPathMatchesStrategy() {
        FileParsingStrategy sideStrategy = new FixedStrategy(FileCategory.SIDE, Set.of(Path.of("side.json")));
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));
        Path supportedPath = Path.of("side.json");

        assertEquals(sideStrategy, factory.resolve(supportedPath));
    }

    @Test
    void resolveByPathThrowsWhenUnsupported() {
        FileParsingStrategy sideStrategy = new FixedStrategy(FileCategory.SIDE, Set.of(Path.of("side.json")));
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));
        Path unsupportedPath = Path.of("txn.json");

        assertThrows(UnsupportedFilePathException.class,
                () -> factory.resolve(unsupportedPath));
    }

    @Test
    void resolveByCategoryThrowsWhenUnsupported() {
        FileParsingStrategyFactory factory =
                new FileParsingStrategyFactory(List.of(new FixedStrategy(FileCategory.SIDE, Set.of())));

        assertThrows(UnsupportedFileCategoryException.class,
                () -> factory.resolve(FileCategory.TRANSACTION));
    }

    @Test
    void resolveByCategoryReturnsStrategy() {
        FileParsingStrategy sideStrategy = new FixedStrategy(FileCategory.SIDE, Set.of(Path.of("side.json")));
        FileParsingStrategyFactory factory = new FileParsingStrategyFactory(List.of(sideStrategy));
        FileCategory category = FileCategory.SIDE;

        assertEquals(sideStrategy, factory.resolve(category));
    }

    private static class FixedStrategy implements FileParsingStrategy {
        private final FileCategory category;
        private final Set<Path> supportedPaths;

        FixedStrategy(FileCategory category, Set<Path> supportedPaths) {
            this.category = category;
            this.supportedPaths = supportedPaths;
        }

        @Override
        public FileCategory getCategory() {
            return category;
        }

        @Override
        public boolean supports(Path path) {
            return supportedPaths.contains(path);
        }

        @Override
        public LoadedFile parse(Path path) {
            throw new UnsupportedOperationException("Not required for these tests");
        }
    }
}
