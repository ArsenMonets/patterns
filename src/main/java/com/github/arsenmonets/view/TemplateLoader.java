package com.github.arsenmonets.view;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TemplateLoader {

    public static String load(String templateName) {
        try {
            String resourcePath = "/templates/" + templateName + ".html";
            InputStream inputStream = TemplateLoader.class.getResourceAsStream(resourcePath);

            if (inputStream == null) {
                return "<h1>Template not found: " + templateName + "</h1>";
            }

            byte[] bytes = inputStream.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "<h1>Template not found: " + templateName + "</h1>";
        }
    }
}
