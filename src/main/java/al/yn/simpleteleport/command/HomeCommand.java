package al.yn.simpleteleport.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import al.yn.simpleteleport.Constants;
import al.yn.simpleteleport.config.ConfigManager;
import al.yn.simpleteleport.config.data.DataManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public class HomeCommand implements ICommand {

    private final ConfigManager configManager;
    private final DataManager dataManager;

    private final LiteralCommandNode<CommandSourceStack> setHomeCommand = Commands.literal("sethome")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_HOME_SET))
            .then(Commands.argument("name", StringArgumentType.word())
                    .executes(this::onSetHomeNamed))
            .executes(this::onSetHome)
            .build();

    private final LiteralCommandNode<CommandSourceStack> delHomeCommand = Commands.literal("delhome")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_HOME_DEL))
            .then(Commands.argument("name", StringArgumentType.word())
                    .executes(this::onDelHomeNamed))
            .executes(this::onDelHome)
            .build();

    private final LiteralCommandNode<CommandSourceStack> listHomeCommand = Commands.literal("listhome")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_HOME_LIST))
            .executes(this::onListHome)
            .build();

    private final LiteralCommandNode<CommandSourceStack> homeCommand = Commands.literal("home")
            .then(Commands.literal("set").redirect(setHomeCommand))
            .then(Commands.literal("del").redirect(delHomeCommand))
            .then(Commands.literal("list").redirect(listHomeCommand))
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_HOME_TP))
            .then(Commands.argument("name", StringArgumentType.word())
                    .executes(this::onTpHomeNamed))
            .executes(this::onTpHome)
            .build();


    public HomeCommand(ConfigManager configManager, DataManager dataManager) {
        this.configManager = configManager;
        this.dataManager = dataManager;
    }

    @Override
    public void register(Commands registrar) {
        registrar.register(setHomeCommand);
        registrar.register(delHomeCommand);
        registrar.register(listHomeCommand);
        registrar.register(homeCommand);
    }


    private int onSetHome(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onDelHome(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onTpHome(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onSetHomeNamed(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onDelHomeNamed(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onTpHomeNamed(CommandContext<CommandSourceStack> context) {
        return 0;
    }

    private int onListHome(CommandContext<CommandSourceStack> context) {
        return 0;
    }
}
