package com.github.arsenmonets.view;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRenderer {

    public static String render(String template, Map<String, Object> model) {
        String html = TemplateLoader.load(template);
        html = processForEach(html, model);
        html = replaceVariables(html, model);
        return html;
    }

    public static String processForEach(String html, Map<String, Object> model) {
        Pattern pattern = Pattern.compile("#foreach\\s*\\(\\s*([\\w]+)\\s*in\\s*([\\w.]+)\\s*\\)(.+?)#end",
                Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String itemVar = matcher.group(1);
            String listVar = matcher.group(2);
            String bodyTemplate = matcher.group(3);

            Object listObj = model.get(listVar);
            StringBuilder loopResult = new StringBuilder();

            if (listObj instanceof Collection) {
                Collection<?> items = (Collection<?>) listObj;
                for (Object item : items) {
                    Map<String, Object> itemModel = new java.util.HashMap<>(model);
                    itemModel.put(itemVar, item);
                    loopResult.append(replaceVariables(bodyTemplate, itemModel));
                }
            }

            matcher.appendReplacement(result, Matcher.quoteReplacement(loopResult.toString()));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public static String replaceVariables(String html, Map<String, Object> model) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(html);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String variable = matcher.group(1);
            Object value = getNestedValue(variable, model);
            String replacement = value != null ? Matcher.quoteReplacement(value.toString()) : "";
            matcher.appendReplacement(result, replacement);
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static Object getNestedValue(String path, Map<String, Object> model) {
        String[] parts = path.split("\\.");
        Object current = model.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            if (current == null)
                return null;

            try {
                Class<?> clazz = current.getClass();
                String getterName = "get" + parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1);
                current = clazz.getMethod(getterName).invoke(current);
            } catch (Exception e) {
                return null;
            }
        }

        return current;
    }
}
