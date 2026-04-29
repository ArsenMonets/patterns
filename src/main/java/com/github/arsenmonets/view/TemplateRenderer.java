package com.github.arsenmonets.view;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {

    public static String render(String template, Map<String, Object> model) {
        String html = TemplateLoader.load(template);
        return replaceVariables(html, model);
    }

    private static String replaceVariables(String html, Map<String, Object> model) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group(1);
            Object value = model.get(variable);
            String replacement = value != null ? Matcher.quoteReplacement(value.toString()) : "";
            matcher.appendReplacement(result, replacement);
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
