package com.example.cacex.service.plan.stratagy;

import com.example.cacex.model.FileCategory;
import com.example.cacex.model.LoadedFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileParsingStrategy {

    FileCategory getCategory();

    boolean supports(Path path);

    LoadedFile parse(Path path) throws IOException;
}
