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
            String expression = matcher.group(1);
            String replacement = evaluateExpression(expression, model);
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static String evaluateExpression(String expression, Map<String, Object> model) {
        int questionMarkIdx = expression.indexOf('?');
        if (questionMarkIdx > 0) {
            int colonIdx = expression.indexOf(':', questionMarkIdx);
            if (colonIdx > questionMarkIdx) {
                String condition = expression.substring(0, questionMarkIdx).trim();
                String trueValue = expression.substring(questionMarkIdx + 1, colonIdx).trim();
                String falseValue = expression.substring(colonIdx + 1).trim();

                if (evaluateCondition(condition, model)) {
                    return processValue(trueValue, model);
                } else {
                    return processValue(falseValue, model);
                }
            }
        }

        Object value = getNestedValue(expression, model);
        return value != null ? value.toString() : "";
    }

    private static String processValue(String value, Map<String, Object> model) {
        value = value.trim();

        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }

        Object objValue = getNestedValue(value, model);
        return objValue != null ? objValue.toString() : value;
    }

    private static boolean evaluateCondition(String condition, Map<String, Object> model) {
        condition = condition.trim();

        if (condition.contains("==")) {
            String[] parts = condition.split("==", 2);
            String left = parts[0].trim();
            String right = parts[1].trim();

            Object leftValue = getNestedValue(left, model);
            Object rightValue = parseValue(right);

            if (leftValue == null)
                return rightValue == null;
            return leftValue.equals(rightValue);
        }

        if (condition.contains("!=")) {
            String[] parts = condition.split("!=", 2);
            String left = parts[0].trim();
            String right = parts[1].trim();

            Object leftValue = getNestedValue(left, model);
            Object rightValue = parseValue(right);

            if (leftValue == null)
                return rightValue != null;
            return !leftValue.equals(rightValue);
        }

        Object value = getNestedValue(condition, model);
        return isTruthy(value);
    }

    private static Object parseValue(String value) {
        value = value.trim();

        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }

        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

    private static boolean isTruthy(Object value) {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return ((Number) value).doubleValue() != 0;
        if (value instanceof String)
            return !((String) value).isEmpty();
        if (value instanceof Collection)
            return !((Collection<?>) value).isEmpty();
        return true;
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
