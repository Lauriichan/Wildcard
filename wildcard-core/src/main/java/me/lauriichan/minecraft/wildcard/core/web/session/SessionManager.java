package me.lauriichan.minecraft.wildcard.core.web.session;

import java.util.ArrayList;
import java.util.Optional;

import com.syntaxphoenix.syntaxapi.net.http.ReceivedRequest;
import com.syntaxphoenix.syntaxapi.random.Keys;

import me.lauriichan.minecraft.wildcard.core.data.container.nbt.NbtAdapterRegistry;

public final class SessionManager {

    private static final Keys KEYS = new Keys(System.currentTimeMillis());

    private final NbtAdapterRegistry registry = new NbtAdapterRegistry();
    private final ArrayList<ClientSession> sessions = new ArrayList<>();

    public ClientSession getSession(final ReceivedRequest request) {
        final String session = Optional.ofNullable(request.getCookie("WCSession")).map(object -> (String) object)
            .orElseGet(this::generateSession);
        final Optional<ClientSession> option = getOptionalSession(session);
        if (option.isPresent() && !option.filter(ClientSession::hasExpired).isPresent()) {
            sessions.remove(option.get());
            return createSession(generateSession());
        }
        return option.orElseGet(() -> createSession(session));
    }

    public Optional<ClientSession> getOptionalSession(final String sessionId) {
        synchronized (sessions) {
            return sessions.stream().filter(current -> current.getId().equals(sessionId)).findFirst();
        }
    }

    private ClientSession createSession(final String sessionId) {
        final ClientSession session = new ClientSession(this, registry, sessionId);
        synchronized (sessions) {
            sessions.add(session);
        }
        return session;
    }

    private String generateSession() {
        String sessionId = KEYS.makeKey(18);
        while (getOptionalSession(sessionId).isPresent()) {
            sessionId = KEYS.makeKey(18);
        }
        return sessionId;
    }

}
