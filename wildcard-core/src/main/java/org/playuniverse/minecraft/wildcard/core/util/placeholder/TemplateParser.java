package org.playuniverse.minecraft.wildcard.core.util.placeholder;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateParser {

    public static final Pattern TEMPLATE_DEFINE = Pattern
        .compile("(?<template>\\#DEFINE\\(\\\"(?<key>[a-zA-Z0-9\\_\\-\\. ]+)\\\"\\)\\s?\\{\\s{2,3}?(?<content>(.|\\s)+?)\\s?\\s?\\})");
    public static final Pattern TEMPLATE_REDEFINE = Pattern.compile(
        "(?<template>#REDEFINE\\(\"(?<key>[a-zA-Z0-9-_.*]+)\"((, ?(?<start>\\d+))?(, ?(?<amount>\\d+)))?\\)\\[\"(?<content>[a-zA-Z0-9-_.]+)\"\\])");
    public static final Pattern TEMPLATE_USE = Pattern.compile(
        "(?<placeholder>\\#\\(\"(?<key>[a-zA-Z0-9-_.*]+)\"((, ?(?<offset>\\d+))?(, ?(?<amount>\\d+)))?\\)(\\[\\\"(?<sequence>.+)\\\"\\])?)");

    private TemplateParser() {}

    public static Template[] parse(final String data) {
        final DefaultTemplateStore store = new DefaultTemplateStore();
        parse(store, data);
        return store.templateArray();
    }

    public static void parse(final TemplateStore store, final String data) {
        Matcher matcher = TEMPLATE_DEFINE.matcher(data);
        while (matcher.find()) {
            store.setTemplate(new Template(matcher.group("template"), matcher.group("key"), matcher.group("content")));
        }
        matcher = TEMPLATE_REDEFINE.matcher(data);
        while (matcher.find()) {
            String content = matcher.group("content");
            final Template template = store.getTemplate(content);
            if (template == null) {
                content = "";
            } else {
                content = template.getContent();
            }
            final String key = matcher.group("key");
            if (!key.contains("*")) {
                store.setTemplate(new Template(matcher.group("template"), key, content));
                continue;
            }
            final String start = matcher.group("start");
            final String amount = matcher.group("amount");
            if (amount == null) {
                store.setTemplate(new Template(matcher.group("template"), key, content));
                continue;
            }
            final int count = parseOrDefault(amount, 1);
            final int offset = parseOrDefault(start, 0);
            final Template parent = new Template(matcher.group("template"), key.replace("*", offset + ""), content);
            if (count <= 1) {
                store.setTemplate(parent);
                continue;
            }
            store.setTemplate(parent);
            for (int current = 1; current < count; current++) {
                store.setTemplate(new ForkTemplate(parent, key.replace("*", offset + current + "")));
            }
        }
    }

    public static String strip(final TemplateStore store, final String data) {
        return strip(data, store.templateArray());
    }

    public static String strip(final String data, Template... templates) {
        if (templates.length == 0) {
            return data;
        }
        String output = data;
        templates = Arrays.stream(templates).filter(template -> !(template instanceof ForkTemplate)).toArray(Template[]::new);
        for (final Template template : templates) {
            output = output.replace(template.getOriginal(), "");
        }
        return output;
    }

    public static String apply(final TemplateStore store, final String data) {
        String output = data;
        final Matcher matcher = TEMPLATE_USE.matcher(data);
        while (matcher.find()) {
            final String key = matcher.group("key");
            if (!key.contains("*")) {
                final Template template = store.getTemplate(key);
                if (template == null) {
                    output = output.replace(matcher.group("placeholder"), "");
                    continue;
                }
                output = output.replace(matcher.group("placeholder"), template.getReplaceContent());
                continue;
            }
            final int count = parseOrDefault(matcher.group("amount"), 1);
            final int offset = parseOrDefault(matcher.group("start"), 0);
            if (count <= 1) {
                final Template template = store.getTemplate(key.replace("*", offset + ""));
                if (template == null) {
                    output = output.replace(matcher.group("placeholder"), "");
                    continue;
                }
                output = output.replace(matcher.group("placeholder"), template.getReplaceContent());
                continue;
            }
            final String sequence = valueOrDefault(matcher.group("sequence"), "");
            final int length = sequence.length();
            final StringBuilder builder = new StringBuilder();
            for (int current = 0; current < count; current++) {
                final Template template = store.getTemplate(key.replace("*", current + offset + ""));
                if (template == null) {
                    continue;
                }
                builder.append(template.getReplaceContent()).append(sequence);
            }
            final String replace = builder.toString();
            output = output.replace(matcher.group("placeholder"), replace.subSequence(0, replace.length() - length));
        }
        return output;
    }

    private static int parseOrDefault(final String value, final int fallback) {
        if (value == null) {
            return fallback;
        }
        try {
            return Math.abs(Integer.parseInt(value));
        } catch (final NumberFormatException e) {
            return fallback;
        }
    }

    private static String valueOrDefault(final String value, final String fallback) {
        return value == null ? fallback : value;
    }

}
