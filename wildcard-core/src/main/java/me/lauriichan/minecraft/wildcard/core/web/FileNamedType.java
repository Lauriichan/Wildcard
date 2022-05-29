package me.lauriichan.minecraft.wildcard.core.web;

import java.util.Arrays;
import java.util.List;

import com.syntaxphoenix.syntaxapi.net.http.NamedType;

public enum FileNamedType implements NamedType {

    PLAIN("text/plain", "txt", "yml", "yaml"),
    HTML("text/html", "html", "htm"),
    CSS("text/css", "css"),
    CSV("text/csv", "csv"),
    ICS("text/calendar", "ics"),
    JS("text/javascript", "js", "mjs"),

    AVIF("image/avif", "avif"),
    BMP("image/bmp", "bmp"),
    GIF("image/gif", "gif"),
    ICO("image/vnd.microsoft.icon", "ico"),
    JPEG("image/jpeg", "jpg", "jpeg"),
    PNG("image/png", "png"),
    SVG("image/svg+xml", "svg"),
    TIFF("image/tiff", "tif", "tiff"),
    WEBP("image/webp", "webp"),

    OTF("font/otf", "otf"),
    TTF("font/ttf", "ttf"),
    WOFF("font/woff", "woff"),
    WOFF2("font/woff2", "woff2"),

    AAC("audio/aac", "aac"),
    MIDI("audio/midi", "mid", "midi"),
    MP3("audio/mpeg", "mp3"),
    OGG("audio/ogg", "ogg"),
    OPUS("audio/opus", "opus"),
    WAV("audio/wav", "wav"),
    WEBA("audio/webm", "weba"),

    AVI("video/avi", "avi"),
    MP4("video/mp4", "mp4"),
    MPEG("video/mpeg", "mpeg"),
    OGV("video/ogg", "ogv"),
    MPEG_TS("video/mp2t", "ts"),
    WEBM("video/webm", "webm"),

    ABW("application/x-abiword", "abw"),
    ARC("application/x-freearc", "arc"),
    AZW("application/vnd.amazon.ebook", "azw"),
    BIN("application/octet-stream", "bin"),
    BZIP("application/x-bzip", "bz"),
    BZIP2("application/x-bzip2", "bz2"),
    CDA("application/x-cdf", "cda"),
    CSH("application/x-csh", "csh"),
    DOC("application/msword", "doc"),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    EOT("application/vnd.ms-fontobject", "eot"),
    EPUB("application/epub+zip", "epub"),
    GZIP("application/gzip", "gz"),
    JAR("application/java-archive", "jar"),
    JSON("application/json", "json"),
    JSONLD("application/ld+json", "jsonld"),
    MPKG("application/vnd.apple.installer+xml", "mpkg"),
    ODP("application/vnd.oasis.opendocument.presentation", "odp"),
    ODS("application/vnd.oasis.opendocument.spreadsheet", "ods"),
    OGX("application/ogg", "ogx"),
    PDF("application/pdf", "pdf"),
    PHP("application/x-httpd-php", "php"),
    PPT("application/vnd.ms-powerpoint", "ppt"),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),
    RAR("application/vnd.rar", "rar"),
    RTF("application/rtf", "rtf"),
    SH("application/x-sh", "sh"),
    SWF("application/x-shockwave-flash", "swf"),
    TAR("application/x-tar", "tar"),
    VSD("application/vnd.visio", "vsd"),
    XHTML("application/xhtml+xml", "xhtml"),
    XLS("application/vnd.ms-excel", "xls"),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),
    XML("application/xml", "xml"),
    XUL("application/vnd.mozilla.xul+xml", "xul"),
    ZIP("application/zip", "zip"),
    $7Z("application/x-7z-compressed", "7z");

    private final String type;
    private final List<String> extensions;

    FileNamedType(final String type, final String... extensions) {
        this.type = type;
        this.extensions = Arrays.asList(extensions);
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public boolean has(final String extension) {
        return extensions.contains(extension.contains(".") ? extension.substring(1) : extension);
    }

    public static FileNamedType parse(final String extension) {
        final FileNamedType[] types = values();
        for (int index = 0; index < types.length; index++) {
            if (types[index].has(extension)) {
                return types[index];
            }
        }
        return null;
    }

}
