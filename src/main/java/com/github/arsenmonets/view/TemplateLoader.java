package com.github.arsenmonets.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateLoader {

    public static String load(String templateName) {
        try {
            String path = "src/main/resources/templates/" + templateName + ".html";
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "<h1>Template not found: " + templateName + "</h1>";
        }
    }
}
