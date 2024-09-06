package io.github.elihuso.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.elihuso.simpleteleport.Constants;
import io.github.elihuso.simpleteleport.config.ConfigManager;
import io.github.elihuso.simpleteleport.config.memory.MemoryDataManager;
import io.github.elihuso.simpleteleport.utility.CommandHelper;
import io.github.elihuso.simpleteleport.utility.ComponentHelper;
import io.github.elihuso.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TeleportCommand implements ICommand {

    private final ConfigManager configManager;
    private final MemoryDataManager memoryDataManager;

    public TeleportCommand(ConfigManager configManager, MemoryDataManager memoryDataManager) {
        this.configManager = configManager;
        this.memoryDataManager = memoryDataManager;
    }

    private final LiteralCommandNode<CommandSourceStack> tpa = Commands.literal("tpa")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_USE))
                    .executes(this::onTpa))
            .build();

    private final LiteralCommandNode<CommandSourceStack> tpHere = Commands.literal("tphere")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_HERE))
                    .executes(this::onTpHere))
            .build();

    private final LiteralCommandNode<CommandSourceStack> tpCancel = Commands.literal("tpcancel")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
                    .executes(this::onTpCancel))
            .executes(this::onTpCancelAll)
            .build();

    private final LiteralCommandNode<CommandSourceStack> tpAccept = Commands.literal("tpaccept")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_ACCEPT))
                    .executes(this::onTpAccept))
            .executes(this::onTpAcceptAll)
            .build();

    private final LiteralCommandNode<CommandSourceStack> tpDeny = Commands.literal("tpdeny")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_DENY))
                    .executes(this::onTpDeny))
            .executes(this::onTpDenyAll)
            .build();

    @Override
    public void register(Commands registrar) {
        registrar.register(tpa);
        registrar.register(tpHere);
        registrar.register(tpCancel);
        registrar.register(tpAccept, List.of("tpacc"));
        registrar.register(tpDeny, List.of("tpdn"));
    }

    private int onTpa(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var target = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();
        if (!CommandHelper.ensurePlayerOnline(sender, player)
                || !CommandHelper.ensurePlayerOnline(sender, target)) {
            sender.sendMessage(ComponentHelper.createPlayerOffline());
            return 0;
        }

        if (player == target) {
            sender.sendMessage(ComponentHelper.createTpaToSelf());
            return 0;
        }

        var globalData = memoryDataManager.getGlobalData();

        if (globalData.hasTpRequested(player.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ComponentHelper.createTpaRequestExists());
            return 0;
        }

        var timeout = configManager.getTpaTimeout();
        globalData.addTpaRequest(player.getUniqueId(), target.getUniqueId(), timeout);
        player.sendMessage(ComponentHelper.createTpaRequesterResult(target, timeout));
        target.sendMessage(ComponentHelper.createTpaRequestedMessage(player, timeout));

        return 1;
    }

    private int onTpHere(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var target = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();
        if (!CommandHelper.ensurePlayerOnline(sender, player)
                || !CommandHelper.ensurePlayerOnline(sender, target)) {
            sender.sendMessage(ComponentHelper.createPlayerOffline());
            return 0;
        }

        if (player == target) {
            sender.sendMessage(ComponentHelper.createTpaToSelf());
            return 0;
        }

        var globalData = memoryDataManager.getGlobalData();

        if (globalData.hasTpRequested(player.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(ComponentHelper.createTpaRequestExists());
            return 0;
        }

        var timeout = configManager.getTpaTimeout();
        globalData.addTpHereRequest(player.getUniqueId(), target.getUniqueId(), timeout);
        player.sendMessage(ComponentHelper.createTpHereRequesterResult(target, timeout));
        target.sendMessage(ComponentHelper.createTpHereRequestedMessage(player, timeout));

        return 1;
    }

    private int onTpCancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var target = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();

        var globalData = memoryDataManager.getGlobalData();
        if (globalData.removeTpRequest(player.getUniqueId(), target.getUniqueId())) {
            target.sendMessage(ComponentHelper.createTpCanceled(player));
            player.sendMessage(ComponentHelper.createTpCanceledTo(target));
        } else {
            player.sendMessage(ComponentHelper.createTpNoSuchRequest(target));
        }
        return 1;
    }

    private int onTpCancelAll(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var globalData = memoryDataManager.getGlobalData();
        for (var result : globalData.removeTpRequest(player.getUniqueId())) {
            var p = Bukkit.getPlayer(result);
            if (p != null) {
                p.sendMessage(ComponentHelper.createTpCanceled(player));
            }
        }

        player.sendMessage(ComponentHelper.createTpCanceledAll());
        return 1;
    }

    private int onTpAccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var target = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();

        var globalData = memoryDataManager.getGlobalData();
        Player teleported = null;
        Player teleportDest = null;
        if (globalData.hasTpaRequested(target.getUniqueId(), player.getUniqueId())) {
            teleported = target;
            teleportDest = player;
        } else if (globalData.hasTpHereRequested(target.getUniqueId(), player.getUniqueId())) {
            teleported = player;
            teleportDest = target;
        } else {
            player.sendMessage(ComponentHelper.createTpNoSuchRequest(target));
            return 0;
        }

        globalData.removeTpRequest(target.getUniqueId(), player.getUniqueId());
        if (TeleportHelper.teleportTo(teleported, teleportDest)) {
            teleported.sendMessage(ComponentHelper.createTpAcceptedFrom(teleportDest));
            teleportDest.sendMessage(ComponentHelper.createTpAcceptedBy(teleported));
            return 1;
        } else {
            teleported.sendMessage(ComponentHelper.createTeleportFailed());
            teleportDest.sendMessage(ComponentHelper.createTeleportFailed());
            return 0;
        }
    }

    private int onTpAcceptAll(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var globalData = memoryDataManager.getGlobalData();
        for (var u : globalData.getTpaRequested(player.getUniqueId())) {
            var p = Bukkit.getPlayer(u);
            if (p != null && p.isConnected()) {
                globalData.removeTpRequest(u, player.getUniqueId());
                p.sendMessage(ComponentHelper.createTpAcceptedBy(player));
                doTeleport(p, player);
            }
        }

        for (var u : globalData.getTpHereRequested(player.getUniqueId())) {
            var p = Bukkit.getPlayer(u);
            if (p != null && p.isConnected()) {
                globalData.removeTpRequest(u, player.getUniqueId());
                p.sendMessage(ComponentHelper.createTpAcceptedBy(player));
                doTeleport(player, p);
            }
        }

        player.sendMessage(ComponentHelper.createTpAcceptedAll());

        return 1;
    }

    private void doTeleport(Player player, Player target) {
        if (!TeleportHelper.teleportTo(player, target)) {
            player.sendMessage(ComponentHelper.createTeleportFailed());
            target.sendMessage(ComponentHelper.createTeleportFailed());
        }
    }

    private int onTpDeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var target = context.getArgument("target", PlayerSelectorArgumentResolver.class).resolve(source).getFirst();

        var globalData = memoryDataManager.getGlobalData();
        if (globalData.hasTpRequested(target.getUniqueId(), player.getUniqueId())) {
            globalData.removeTpRequest(target.getUniqueId(), player.getUniqueId());
            player.sendMessage(ComponentHelper.createTpDeniedFrom(target));
            target.sendMessage(ComponentHelper.createTpDeniedBy(player));
            return 1;
        } else {
            player.sendMessage(ComponentHelper.createTpNoSuchRequest(target));
            return 0;
        }
    }

    private int onTpDenyAll(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var sender = source.getSender();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var globalData = memoryDataManager.getGlobalData();
        for (var u : globalData.getTpaRequested(player.getUniqueId())) {
            var p = Bukkit.getPlayer(u);
            if (p != null) {
                globalData.removeTpRequest(u, player.getUniqueId());
                p.sendMessage(ComponentHelper.createTpDeniedBy(player));
            }
        }

        for (var u : globalData.getTpHereRequested(player.getUniqueId())) {
            var p = Bukkit.getPlayer(u);
            if (p != null) {
                globalData.removeTpRequest(u, player.getUniqueId());
                p.sendMessage(ComponentHelper.createTpDeniedBy(player));
            }
        }

        player.sendMessage(ComponentHelper.createTpDeniedAll());
        return 1;
    }
}
