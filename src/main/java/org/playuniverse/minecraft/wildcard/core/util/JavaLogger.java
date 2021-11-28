package org.playuniverse.minecraft.wildcard.core.util;

import java.util.function.BiConsumer;

import org.playuniverse.minecraft.wildcard.core.IWildcardAdapter;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.logging.LoggerState;
import com.syntaxphoenix.syntaxapi.logging.color.LogType;
import com.syntaxphoenix.syntaxapi.logging.color.LogTypeMap;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;

public final class JavaLogger implements ILogger {

    private final String format = "%s[Wildcard/%s] %s";

    private final LogTypeMap map = new LogTypeMap();
    private final IWildcardAdapter adapter;
    private final ILogAssist logger;

    private LoggerState state = LoggerState.CUSTOM;

    public JavaLogger(final IWildcardAdapter adapter) {
        this.adapter = adapter;
        this.logger = adapter.getLogAssist();
        setType("debug", 'b');
        setType("info", '7');
        setType("warning", 'e');
        setType("error", 'c');
    }

    public ILogAssist getLogger() {
        return logger;
    }

    public IWildcardAdapter getAdapter() {
        return adapter;
    }

    @Override
    public ILogger close() {
        return this;
    }

    @Override
    public LogTypeMap getTypeMap() {
        return map;
    }

    @Override
    public ILogger setCustom(final BiConsumer<Boolean, String> custom) {
        return this;
    }

    @Override
    public BiConsumer<Boolean, String> getCustom() {
        return null;
    }

    public ILogger setType(final String name, final char color) {
        return setType(new SafeColorType(name, color));
    }

    @Override
    public ILogger setType(final LogType type) {
        map.override(type);
        return this;
    }

    public LogType getTypeOrDefault(final String typeId) {
        return map.tryGetById(typeId).orElse(SafeColorType.DEFAULT);
    }

    @Override
    public LogType getType(final String typeId) {
        return map.getById(typeId);
    }

    @Override
    public ILogger setState(final LoggerState state) {
        this.state = state.extendedInfo() ? LoggerState.EXTENDED_CUSTOM : LoggerState.CUSTOM;
        return this;
    }

    @Override
    public LoggerState getState() {
        return state;
    }

    @Override
    public ILogger setThreadName(final String name) {
        return this;
    }

    @Override
    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    @Override
    public ILogger setColored(final boolean color) {
        return this;
    }

    @Override
    public boolean isColored() {
        return true;
    }

    @Override
    public ILogger log(final String message) {
        return log(LogTypeId.INFO, message);
    }

    @Override
    public ILogger log(final LogTypeId type, final String message) {
        return log(type.id(), message);
    }

    @Override
    public ILogger log(final String typeId, final String message) {
        return print(getTypeOrDefault(typeId), message);
    }

    @Override
    public ILogger log(final String... messages) {
        return log(LogTypeId.INFO, messages);
    }

    @Override
    public ILogger log(final LogTypeId type, final String... messages) {
        return log(type.id(), messages);
    }

    @Override
    public ILogger log(final String typeId, final String... messages) {
        return print(getTypeOrDefault(typeId), messages);
    }

    @Override
    public ILogger log(final Throwable throwable) {
        return log(LogTypeId.ERROR, throwable);
    }

    @Override
    public ILogger log(final LogTypeId type, final Throwable throwable) {
        return log(type.id(), throwable);
    }

    @Override
    public ILogger log(final String typeId, final Throwable throwable) {
        return print(getTypeOrDefault(typeId), Exceptions.stackTraceToString(throwable));
    }

    private ILogger print(final LogType type, final String message) {
        print(type.getName(), type.asColorString(), message);
        return this;
    }

    private ILogger print(final LogType type, final String[] messages) {
        final String typeName = type.getName();
        final String color = type.asColorString();
        for (final String message : messages) {
            print(typeName, color, message);
        }
        return this;
    }

    private void print(final String type, final String color, final String message) {
        logger.info(String.format(format, color, type, message));
    }

}
