package al.yn.simpleteleport.command;

import al.yn.simpleteleport.command.arguments.HomeArgumentType;
import al.yn.simpleteleport.command.arguments.SpaceBreakStringArgumentType;
import al.yn.simpleteleport.utility.CommandHelper;
import al.yn.simpleteleport.utility.ComponentHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import al.yn.simpleteleport.Constants;
import al.yn.simpleteleport.config.ConfigManager;
import al.yn.simpleteleport.config.data.DataManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class HomeCommand implements ICommand {

    private final ConfigManager configManager;
    private final DataManager dataManager;

    private final LiteralCommandNode<CommandSourceStack> setHomeCommand;
    private final LiteralCommandNode<CommandSourceStack> delHomeCommand;
    private final LiteralCommandNode<CommandSourceStack> listHomeCommand;

    private final LiteralCommandNode<CommandSourceStack> homeCommand;

    public HomeCommand(ConfigManager configManager, DataManager dataManager) {
        this.configManager = configManager;
        this.dataManager = dataManager;

        this.setHomeCommand = createSetHome("sethome").build();
        this.delHomeCommand = createDelHome("delhome", dataManager).build();
        this.listHomeCommand = createListHome("listhome").build();

        this.homeCommand = Commands.literal("home")
                .then(createSetHome("set"))
                .then(createDelHome("del", dataManager))
                .then(createListHome("list"))
                .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_HOME_TP))
                .then(Commands.argument("name", HomeArgumentType.create(dataManager))
                        .executes(this::onTpHomeNamed))
                .executes(this::onTpHome)
                .build();
    }

    private LiteralArgumentBuilder<CommandSourceStack> createSetHome(String command) {
        return Commands.literal(command)
                .requires(createRequires(Constants.PERMISSION_HOME_SET))
                .then(Commands.argument("name", HomeArgumentType.create(dataManager))
                        .executes(this::onSetHomeNamed))
                .executes(this::onSetHome);
    }

    private LiteralArgumentBuilder<CommandSourceStack> createDelHome(String command, DataManager dataManager) {
        return Commands.literal(command)
                .requires(createRequires(Constants.PERMISSION_HOME_DEL))
                .then(Commands.argument("name", HomeArgumentType.create(dataManager))
                        .executes(this::onDelHomeNamed))
                .executes(this::onDelHome);
    }

    private LiteralArgumentBuilder<CommandSourceStack> createListHome(String command) {
        return Commands.literal(command)
                .requires(createRequires(Constants.PERMISSION_HOME_LIST))
                .executes(this::onListHome);
    }

    private Predicate<CommandSourceStack> createRequires(String permission) {
        return r -> r.getSender().hasPermission(permission);
    }

    @Override
    public void register(Commands registrar) {
        registrar.register(setHomeCommand);
        registrar.register(delHomeCommand);
        registrar.register(listHomeCommand);
        registrar.register(homeCommand);
    }

    private int onSetHome(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        onSetHome(player, "Home");

        return 1;
    }

    private int onDelHome(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        onDelHome(player, "Home");

        return 1;
    }

    private int onTpHome(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        onTpHome(player, "Home");

        return 1;
    }

    private int onSetHomeNamed(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var name = StringArgumentType.getString(context, "name");
        onSetHome(player, name);

        return 1;
    }

    private int onDelHomeNamed(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var name = StringArgumentType.getString(context, "name");
        onDelHome(player, name);

        return 1;
    }

    private int onTpHomeNamed(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        var name = StringArgumentType.getString(context, "name");
        onTpHome(player, name);

        return 1;
    }

    private int onListHome(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var entity = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), entity)) {
            return 0;
        }

        var player = (Player) entity;
        assert player != null;

        onListHome(player);

        return 1;
    }

    private void onSetHome(Player player, String name) {
        var data = dataManager.getPlayerData(player);
        if (data.getHomeCount() >= configManager.getHomeMaxCount() && !player.hasPermission(Constants.PERMISSION_HOME_BYPASS_LIMIT)) {
            player.sendMessage(ComponentHelper.createHomeLimitReached());
            return;
        }

        if (data.hasHome(name)) {
            player.sendMessage(ComponentHelper.createHomeExisted(name));
            return;
        }

        var loc = player.getLocation();
        data.addHome(name, loc);
        player.sendMessage(ComponentHelper.createHomeSet(name, loc));
    }

    private void onDelHome(Player player, String name) {
        var data = dataManager.getPlayerData(player);
        if (!data.hasHome(name)) {
            player.sendMessage(ComponentHelper.createHomeNotFound(name));
            return;
        }

        var loc = data.getHome(name);
        data.delHome(name);
        player.sendMessage(ComponentHelper.createHomeDel(name, loc));
    }

    private void onTpHome(Player player, String name) {
        var data = dataManager.getPlayerData(player);
        if (!data.hasHome(name)) {
            player.sendMessage(ComponentHelper.createHomeNotFound(name));
            return;
        }

        var loc = data.getHome(name);
        player.teleport(loc);
        player.sendMessage(ComponentHelper.createHomeTp(name, loc));
    }

    private void onListHome(Player player) {
        var data = dataManager.getPlayerData(player);
        player.sendMessage(ComponentHelper.createHomeList(data.getHomes()));
    }
}
