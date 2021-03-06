package me.lauriichan.minecraft.wildcard.core.web;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.net.http.Cookie;
import com.syntaxphoenix.syntaxapi.net.http.CustomRequestData;
import com.syntaxphoenix.syntaxapi.net.http.HttpSender;
import com.syntaxphoenix.syntaxapi.net.http.HttpWriter;
import com.syntaxphoenix.syntaxapi.net.http.NamedAnswer;
import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.net.http.RequestContent;
import com.syntaxphoenix.syntaxapi.net.http.RequestType;
import com.syntaxphoenix.syntaxapi.net.http.ResponseCode;
import com.syntaxphoenix.syntaxapi.net.http.StandardContentType;
import com.syntaxphoenix.syntaxapi.net.http.web.WebRedirectHandler;
import com.syntaxphoenix.syntaxapi.net.http.web.WebServer;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataType;
import me.lauriichan.minecraft.wildcard.core.data.container.nbt.NbtContainer;
import me.lauriichan.minecraft.wildcard.core.settings.RatelimitSettings;
import me.lauriichan.minecraft.wildcard.core.settings.WebSettings;
import me.lauriichan.minecraft.wildcard.core.util.NamedThreadFactory;
import me.lauriichan.minecraft.wildcard.core.util.Resources;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.core.util.WindowsShortcut;
import me.lauriichan.minecraft.wildcard.core.web.command.impl.RequestCommandHandler;
import me.lauriichan.minecraft.wildcard.core.web.session.ClientSession;
import me.lauriichan.minecraft.wildcard.core.web.session.SessionManager;

public final class WebControl extends WebRedirectHandler {

    private final Container<WebServer> server = Container.of();

    private final SessionManager sessionManager = new SessionManager();
    private final RequestCommandHandler commandHandler;

    private final WebSettings settings = Singleton.get(WebSettings.class);
    private final RatelimitSettings ratelimit = Singleton.get(RatelimitSettings.class);

    private final WildcardCore core;

    private final Container<String> hostPath = Container.of();

    private final NamedThreadFactory threadFactory = new NamedThreadFactory("Wildcard");
    private final ExecutorService threadService = Executors.newCachedThreadPool(threadFactory);

    public WebControl(final WildcardCore core) {
        super(WindowsShortcut.lookUp(new File(Singleton.get(WebSettings.class).getString("directory", "webpage"))));
        this.core = core;
        this.commandHandler = new RequestCommandHandler(core);
        setDefault(new ComplexPathHandler(core.getEventManager()));
        setDirectory(Resources.getExternalPathFor("webpage").toFile());
    }

    /*
     * Getters
     */

    public ExecutorService getThreadService() {
        return threadService;
    }

    public RequestCommandHandler getCommandHandler() {
        return commandHandler;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public WebSettings getSettings() {
        return settings;
    }

    public Container<WebServer> getServer() {
        return server;
    }

    public String getHostPath() {
        return hostPath.get();
    }

    /*
     * Load server if enabled
     */

    public void load() {
        exit();
        loadRatelimit();
        final int port = settings.getInteger("port", 80);
        final String hostRaw = settings.getString("host", "*");
        final InetAddress host = resolveHost(hostRaw);
        if (host == null) {
            core.getLogger().log(LogTypeId.WARNING, "Couldn't resolve host '" + hostRaw + "'!");
            core.getLogger().log(LogTypeId.WARNING, "WebServer will not startup as long as the host can't be resolved!");
            return;
        }
        final WebServer instance = new SpecializedWebServer(port, host, threadService);
        instance.setHandler(this);
        instance.addTypes(RequestType.GET, RequestType.POST);
        instance.setValidator((writer,
            request) -> (request.getType() == RequestType.POST ? RequestContent.NEEDED.message(request.getHeader("Content-Length") == null)
                : RequestContent.UNNEEDED));
        instance.applyName(core.getServerName());
        final boolean useHostPath = settings.getBoolean("host.use.path", false);
        final boolean useHostPort = settings.getBoolean("host.use.port", true);
        final String hostPath = settings.getString("host.path", "0.0.0.0");
        this.hostPath.replace("http://" + (useHostPath ? hostPath : (hostRaw == null ? host.getHostName() : hostRaw))
            + (useHostPort ? (port == 80 ? "/" : ":" + port + "/") : "/"));
        try {
            instance.start();
            server.replace(instance);
        } catch (final IOException exp) {
            core.getLogger().log(LogTypeId.WARNING, "Failed to start WebServer!");
            core.getLogger().log(LogTypeId.WARNING, exp);
        }
    }

    private void loadRatelimit() {
        ratelimit.getBoolean("enabled", true);
        ratelimit.getInteger("attempts", 5);
    }

    private InetAddress resolveHost(final String host) {
        try {
            if (host == null || host.trim().isEmpty() || host.equals("*")) {
                return InetAddress.getByName("0.0.0.0");
            }
            return InetAddress.getByName(host);
        } catch (final UnknownHostException e) {
            // Ignore and just return null
            return null;
        }
    }

    public void exit() {
        if (!server.isPresent()) {
            return;
        }
        final WebServer instance = server.get();
        try {
            instance.stop();
            instance.getServerThread().shutdownNow();
        } catch (final IOException e) {
            core.getLogger().log(LogTypeId.WARNING, "Failed to stop WebServer");
            core.getLogger().log(LogTypeId.WARNING, e);
        }
        server.replace(null);
    }

    public void shutdown() {
        threadService.shutdownNow(); // Instant shutdown please
    }

    /*
     * Handle server requests
     */

    @Override
    public boolean handleRequest(final HttpSender httpSender, final HttpWriter writer, final ReceivedRequest data) throws Exception {

        final ClientSession session = sessionManager.getSession(data);
        final NbtContainer container = session.getData();

        if (!container.has("cookie-set", IDataType.BOOLEAN) || !container.get("cookie-set", IDataType.BOOLEAN)) {
            container.set("cookie-set", true, IDataType.BOOLEAN);
            new NamedAnswer(StandardContentType.JSON)
                .addCookie(Cookie.of("WCSession", session.getId()).add("Expires", session.getExpireTimeString()))
                .header("Location", hostPath.get() + data.getPathAsString()).code(ResponseCode.TEMPORARY_REDIRECT).write(writer);
            return true;
        }

        final WebSender sender = new WebSender(httpSender, session);
        if (data.getType() == RequestType.GET) {
            return super.handleRequest(sender, writer, data);
        }
        if (!String.class.isAssignableFrom(((CustomRequestData<?>) data.getData()).getType())) {
            throw new IllegalArgumentException("Unknown type");
        }
        if (session.isUsed()) {
            return true;
        }
        session.setUsed(true);
        try {
            commandHandler.call(getDirectory().get(), sender, writer, data, data.getData().getValue().toString());
            return true;
        } finally {
            session.setUsed(false);
        }
    }

}
