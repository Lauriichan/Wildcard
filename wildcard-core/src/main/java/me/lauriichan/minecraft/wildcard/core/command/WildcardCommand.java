package me.lauriichan.minecraft.wildcard.core.command;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.syntaxphoenix.syntaxapi.utils.java.Arrays;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.MessageAdapter;
import me.lauriichan.minecraft.wildcard.core.command.api.CommandContext;
import me.lauriichan.minecraft.wildcard.core.command.api.StringReader;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.ExecutionNode;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.LiteralNode;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;
import me.lauriichan.minecraft.wildcard.core.data.storage.Database;
import me.lauriichan.minecraft.wildcard.core.data.storage.HistoryEntry;
import me.lauriichan.minecraft.wildcard.core.data.storage.RequestResult;
import me.lauriichan.minecraft.wildcard.core.data.storage.Token;
import me.lauriichan.minecraft.wildcard.core.data.storage.util.UUIDHelper;
import me.lauriichan.minecraft.wildcard.core.message.ClickAction;
import me.lauriichan.minecraft.wildcard.core.message.HoverAction;
import me.lauriichan.minecraft.wildcard.core.message.PlatformClickEvent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformComponent;
import me.lauriichan.minecraft.wildcard.core.message.PlatformHoverEvent;
import me.lauriichan.minecraft.wildcard.core.message.hover.HoverText;

// TODO: Figure out how to solve this issue

public final class WildcardCommand implements IBasicCommand {

    private final Map<String, Permission> helpMessages;

    public WildcardCommand() {
        HashMap<String, Permission> helpMessages = new HashMap<>();
        helpMessages.put("command.help.get", null);
        helpMessages.put("command.help.help", null);
        helpMessages.put("command.help.deny", Permission.COMMAND_DENY);
        helpMessages.put("command.help.allow", Permission.COMMAND_ALLOW);
        helpMessages.put("command.help.create", Permission.COMMAND_CREATE);
        helpMessages.put("command.help.reload", Permission.COMMAND_RELOAD);
        helpMessages.put("command.help.history", Permission.COMMAND_HISTORY);
        this.helpMessages = Collections.unmodifiableMap(helpMessages);
    }

    @Override
    @Command(name = "wildcard", aliases = "card")
    public RootNode<BaseInfo> build(final String name) {
        final LiteralNode<BaseInfo> root = new LiteralNode<>(name);
        root.putChild(new ExecutionNode<>("history", this::onHistory));
        root.putChild(new ExecutionNode<>("reload", this::onReload));
        root.putChild(new ExecutionNode<>("create", this::onCreate));
        root.putChild(new ExecutionNode<>("allow", this::onAllow));
        root.putChild(new ExecutionNode<>("deny", this::onDeny));
        root.putChild(new ExecutionNode<>("help", this::onHelp));
        root.putChild(new ExecutionNode<>("get", this::onGet));
        root.setExecution("help");
        return root;
    }

    private void onHelp(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        for (String key : helpMessages.keySet()) {
            Permission permission = helpMessages.get(key);
            if (permission != null && !info.isPermitted(permission)) {
                continue;
            }
            info.send(key);
        }
    }

    private void onHistory(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        if (!info.isPermitted(Permission.COMMAND_HISTORY)) {
            info.send("unpermitted.command", "permission", Permission.COMMAND_HISTORY.id());
            return;
        }
        final Container<Database> container = info.getCore().getDatabase();
        if (container.isEmpty()) {
            info.send("command.database.unavailable");
            return;
        }
        final Database database = container.get();
        final StringReader reader = context.getReader();
        final Container<UUID> target = Container.of();
        final HistoryEntry[] history = getHistory(target, info, database, reader);
        int page = 0;
        final int pages = (int) Math.ceil(history.length / 6d);
        if (reader.skipWhitespace().hasNext() && reader.testInt()) {
            page = Math.min(pages, Math.max(0, reader.parseInt()));
        }
        final int startIndex = page * 6;
        final int endIndex = Math.min(history.length - 1, startIndex + 6);
        final String name = target.map(info::getName).orElseGet(() -> info.getTranslation().translate("command.history.self"));
        info.send("command.history.list.start", "name", name);
        for (int index = startIndex; index < endIndex; index++) {
            final HistoryEntry entry = history[index];
            final String idxSize = space((index + 1 + "").length());
            if (entry.getTokenId() == null) {
                info.send("commnad.history.list.item.deny", "idx", index + 1, "idxSize", idxSize, "time", entry.getTimeAsString());
                continue;
            }
            info.send("commnad.history.list.item.allow", "idx", index + 1, "idxSize", idxSize, "ownerName",
                info.getName(entry.getTokenId()), "ownerId", entry.getTokenId(), "time", entry.getTimeAsString());
        }
        final String targetCmd = target.isEmpty() ? " " : name + ' ';
        PlatformComponent[] output = {};
        if (page - 1 >= 0) {
            final PlatformComponent[] current = info.translate("command.history.list.page.previous", "page", page);
            info.apply(current, new PlatformClickEvent(ClickAction.RUN_COMMAND, "/wildcard history " + targetCmd + (page - 1)),
                new PlatformHoverEvent(HoverAction.SHOW_TEXT, new HoverText(info.translate("command.action.page.previous"))));
            output = Arrays.merge(PlatformComponent[]::new, output, current);
        }
        output = Arrays.merge(PlatformComponent[]::new, output, info.translate("command.history.list.page.info", "page", page + 1));
        if (page + 1 < pages) {
            final PlatformComponent[] current = info.translate("command.history.list.page.next", "page", page + 2);
            info.apply(current, new PlatformClickEvent(ClickAction.RUN_COMMAND, "/wildcard history " + targetCmd + (page + 1)),
                new PlatformHoverEvent(HoverAction.SHOW_TEXT, new HoverText(info.translate("command.action.page.next"))));
            output = Arrays.merge(PlatformComponent[]::new, output, current);
        }
        info.send(output);
        info.send("command.history.list.end", "name", name);
    }

    private void onReload(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        if (!info.isPermitted(Permission.COMMAND_RELOAD)) {
            info.send("unpermitted.command", "permission", Permission.COMMAND_RELOAD.id());
            return;
        }
        info.send("command.reload.start");
        if (info.getCore().reload()) {
            info.send("command.reload.success");
            return;
        }
        info.send("command.reload.failed");
    }

    private void onCreate(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        if (!info.isPermitted(Permission.COMMAND_CREATE)) {
            info.send("unpermitted.command", "permission", Permission.COMMAND_CREATE.id());
            return;
        }
        final Container<Database> container = info.getCore().getDatabase();
        if (container.isEmpty()) {
            info.send("command.database.unavailable");
            return;
        }
        final Database database = container.get();
        final StringReader reader = context.getReader();
        if (!info.isPlayer() || reader.hasNext()) {
            if (!reader.skipWhitespace().hasNext()) {
                info.send("command.target.specify");
                return;
            }
            final String targetRaw = reader.read();
            if (targetRaw.trim().isEmpty() || targetRaw.length() < 3) {
                info.send("command.target.invalid", "input", targetRaw);
                return;
            }
            UUID uniqueId = UUIDHelper.fromString(targetRaw);
            if (uniqueId == null) {
                uniqueId = info.getUniqueId(targetRaw);
                if (uniqueId == null) {
                    info.send("command.target.invalid", "input", targetRaw);
                    return;
                }
            }
            OffsetDateTime time = OffsetDateTime.now();

            int expires = 7;
            if (reader.skipWhitespace().hasNext() && reader.testInt()) {
                expires = Math.min(365, reader.parseInt());
            }
            time = expires <= 0 ? null : time.plusDays(expires);
            int uses = 10;
            if (reader.skipWhitespace().hasNext() && reader.testInt()) {
                uses = reader.parseInt();
                if (uses <= 0) {
                    uses = Integer.MAX_VALUE;
                }
            }
            final String name = info.getName(uniqueId);
            info.send("command.create.other.start", "user", name);
            final Token token = database.getTokenOrGenerate(uniqueId, uses, time).join();
            info.send(getTokenMessage(info, "command.create.other.end", name, token));
            if (!info.isPlayer()) {
                info.send("&7" + token.getToken());
            }
            final MessageAdapter target = info.getMessageAdapter(uniqueId);
            if (!target.isOnline()) {
                return;
            }
            final PlatformComponent[] components = info.translate("command.create.other.notify");
            info.apply(components, new PlatformClickEvent(ClickAction.RUN_COMMAND, "/wildcard get"),
                new PlatformHoverEvent(HoverAction.SHOW_TEXT, new HoverText(info.translate("command.action.token.get"))));
            target.send(components);
            return;
        }
        OffsetDateTime time = OffsetDateTime.now();
        int expires = 7;
        if (reader.skipWhitespace().testInt()) {
            expires = Math.min(365, reader.parseInt());
        }
        time = expires <= 0 ? null : time.plusDays(expires);
        int uses = 10;
        if (reader.skipWhitespace().testInt()) {
            uses = reader.parseInt();
            if (uses <= 0) {
                uses = Integer.MAX_VALUE;
            }
        }
        info.send("command.create.self.start");
        final Token token = database.getTokenOrGenerate(info.getSenderId(), uses, time).join();
        info.send(getTokenMessage(info, "command.create.self.end", "", token));
    }

    private void onAllow(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        if (!info.isPermitted(Permission.COMMAND_ALLOW)) {
            info.send("unpermitted.command", "permission", Permission.COMMAND_ALLOW.id());
            return;
        }
        final Container<Database> container = info.getCore().getDatabase();
        if (container.isEmpty()) {
            info.send("command.database.unavailable");
            return;
        }
        final Database database = container.get();
        final StringReader reader = context.getReader();
        if (!reader.skipWhitespace().hasNext()) {
            info.send("command.target.specify");
            return;
        }
        final String targetRaw = reader.read();
        if (targetRaw.trim().isEmpty() || targetRaw.length() < 3) {
            info.send("command.target.invalid", "input", targetRaw);
            return;
        }
        UUID uniqueId = UUIDHelper.fromString(targetRaw);
        if (uniqueId == null) {
            uniqueId = info.getUniqueId(targetRaw);
            if (uniqueId == null) {
                info.send("command.target.invalid", "input", targetRaw);
                return;
            }
        }
        final String name = info.getName(uniqueId);
        info.send("command.allow.start", "name", name);
        final RequestResult result = database.allow(uniqueId, info.getSenderId()).join();
        info.send("command.allow." + result.name().toLowerCase(), "name", name);
    }

    private void onDeny(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        if (!info.isPermitted(Permission.COMMAND_DENY)) {
            info.send("unpermitted.command", "permission", Permission.COMMAND_DENY.id());
            return;
        }
        final Container<Database> container = info.getCore().getDatabase();
        if (container.isEmpty()) {
            info.send("command.database.unavailable");
            return;
        }
        final Database database = container.get();
        final StringReader reader = context.getReader();
        if (!reader.skipWhitespace().hasNext()) {
            info.send("command.target.specify");
            return;
        }
        final String targetRaw = reader.read();
        if (targetRaw.trim().isEmpty() || targetRaw.length() < 3) {
            info.send("command.target.invalid", "input", targetRaw);
            return;
        }
        UUID uniqueId = UUIDHelper.fromString(targetRaw);
        if (uniqueId == null) {
            uniqueId = info.getUniqueId(targetRaw);
            if (uniqueId == null) {
                info.send("command.target.invalid", "input", targetRaw);
                return;
            }
        }
        final String name = info.getName(uniqueId);
        info.send("command.deny.start", "user", name);
        final RequestResult result = database.deny(uniqueId).join();
        info.send("command.deny." + result.name().toLowerCase(), "user", name);
        if (result == RequestResult.SUCCESS) {
            info.getMessageAdapter(uniqueId).kick(info.translate("unpermitted.ban", "server", info.getAdapter().getServerName()));
        }
    }

    private void onGet(final CommandContext<BaseInfo> context) {
        final BaseInfo info = context.getSource();
        final Container<Database> container = info.getCore().getDatabase();
        if (container.isEmpty()) {
            info.send("command.database.unavailable");
            return;
        }
        final Database database = container.get();
        final StringReader reader = context.getReader();
        if (!info.isPlayer() || reader.hasNext() && info.isPermitted(Permission.COMMAND_GET_OTHER)) {
            if (!reader.skipWhitespace().hasNext()) {
                info.send("command.target.specify");
                return;
            }
            final String targetRaw = reader.read();
            if (targetRaw.trim().isEmpty() || targetRaw.length() < 3) {
                info.send("command.target.invalid", "input", targetRaw);
                return;
            }
            UUID uniqueId = UUIDHelper.fromString(targetRaw);
            if (uniqueId == null) {
                uniqueId = info.getUniqueId(targetRaw);
                if (uniqueId == null) {
                    info.send("command.target.invalid", "input", targetRaw);
                    return;
                }
            }
            final String name = info.getName(uniqueId);
            info.send("command.get.other.start", "user", name);
            final Token token = database.getToken(uniqueId).join();
            if (token == null) {
                info.send("command.get.other.notfound", "user", name);
                return;
            }
            info.send(getTokenMessage(info, "command.get.other.found", name, token));
            if (!info.isPlayer()) {
                info.send("&7" + token.getToken());
            }
            return;
        }
        info.send("command.get.self.start");
        final Token token = database.getToken(info.getSenderId()).join();
        if (token == null) {
            info.send("command.get.self.notfound");
            return;
        }
        info.send(getTokenMessage(info, "command.get.self.found", "", token));
    }

    /*
     * Helper
     */

    private String space(final int amount) {
        final StringBuilder builder = new StringBuilder();
        for (int index = 0; index < amount; index++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    private HistoryEntry[] getHistory(final Container<UUID> target, final BaseInfo info, final Database database,
        final StringReader reader) {
        if (!info.isPlayer() || reader.hasNext()) {
            if (!reader.skipWhitespace().hasNext()) {
                info.send("command.target.specify");
                return null;
            }
            final String targetRaw = reader.read();
            if (targetRaw.trim().isEmpty() || targetRaw.length() < 3) {
                info.send("command.target.invalid", "input", targetRaw);
                return null;
            }
            UUID uniqueId = UUIDHelper.fromString(targetRaw);
            if (uniqueId == null) {
                uniqueId = info.getUniqueId(targetRaw);
                if (uniqueId == null) {
                    info.send("command.target.invalid", "input", targetRaw);
                    return null;
                }
            }
            info.send("command.history.get.other", "id", uniqueId.toString());
            target.replace(uniqueId);
            return database.getHistory(uniqueId).join();
        }
        info.send("command.history.get.self");
        target.replace(null);
        return database.getHistory(info.getSenderId()).join();
    }

    private PlatformComponent[] getTokenMessage(final BaseInfo info, final String message, final String user, final Token token) {
        final PlatformComponent[] components = info.translate(message, "user", user, "token", token.getToken(), "uses", token.getUses(),
            "expires", token.getExpiresAsString());
        info.apply(components, new PlatformClickEvent(ClickAction.COPY_TO_CLIPBOARD, token.getToken()),
            new PlatformHoverEvent(HoverAction.SHOW_TEXT, new HoverText(info.translate("command.action.token.copy"))));
        return components;
    }

}
