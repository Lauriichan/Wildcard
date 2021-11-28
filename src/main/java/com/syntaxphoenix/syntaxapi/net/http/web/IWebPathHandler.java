package com.syntaxphoenix.syntaxapi.net.http.web;

import java.io.File;

import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;

@FunctionalInterface
public interface IWebPathHandler {

    public void handlePath(File directory, HttpSender sender, HttpWriter writer, ReceivedRequest data) throws Exception;

}
