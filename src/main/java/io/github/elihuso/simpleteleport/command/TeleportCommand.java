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

    private final LiteralCommandNode<CommandSourceStack> TPA = Commands.literal("tpa")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_USE))
                    .executes(this::onTpa))
            .build();

    private final LiteralCommandNode<CommandSourceStack> TP_HERE = Commands.literal("tphere")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_HERE))
                    .executes(this::onTpHere))
            .build();

    private final LiteralCommandNode<CommandSourceStack> TP_CANCEL = Commands.literal("tpcancel")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
                    .executes(this::onTpCancel))
            .executes(this::onTpCancelAll)
            .build();

    private final LiteralCommandNode<CommandSourceStack> TP_ACCEPT = Commands.literal("tpaccept")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
                    .executes(this::onTpAccept))
            .executes(this::onTpAcceptAll)
            .build();

    private final LiteralCommandNode<CommandSourceStack> TP_DENY = Commands.literal("tpdeny")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
            )
            .build();

    @Override
    public void register(Commands registrar) {
        registrar.register(TPA);
        registrar.register(TP_HERE);
        registrar.register(TP_CANCEL);
        registrar.register(TP_ACCEPT, List.of("tpacc"));
        registrar.register(TP_DENY, List.of("tpdn"));
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
            target.sendMessage(ComponentHelper.createTpCanceledMessage(player));
        }

        player.sendMessage(ComponentHelper.createTpCancelResult());
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
                p.sendMessage(ComponentHelper.createTpCanceledMessage(player));
            }
        }

        player.sendMessage(ComponentHelper.createTpCancelResult());
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
        if (globalData.hasTpaRequested(player.getUniqueId(), target.getUniqueId())) {
            if (!TeleportHelper.teleportTo(player, target)) {
                player.sendMessage(ComponentHelper.createTeleportFailed());
                return 0;
            }

            return 1;
        }

        if (globalData.hasTpHereRequested(player.getUniqueId(), target.getUniqueId())) {
            if (!TeleportHelper.teleportTo(target, player)) {
                player.sendMessage(ComponentHelper.createTeleportFailed());
                return 0;
            }
            return 1;
        }


        return 0;
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


        return 0;
    }
}
