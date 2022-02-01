package me.lauriichan.minecraft.wildcard.core.web;

import java.io.File;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.web.IWebPathHandler;

public interface ISpecialPathHandler extends IWebPathHandler {
    @Override
    default void handlePath(final File directory, final HttpSender sender, final HttpWriter writer, final ReceivedRequest data)
        throws Exception {
        if (sender instanceof WebSender) {
            handleWeb(directory, (WebSender) sender, writer, data);
            return;
        }
        handleWeb(directory, new WebSender(sender, null), writer, data);
    }

    void handleWeb(File directory, WebSender sender, HttpWriter writer, ReceivedRequest data) throws Exception;

}
