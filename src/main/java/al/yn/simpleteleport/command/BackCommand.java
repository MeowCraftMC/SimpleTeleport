package al.yn.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import al.yn.simpleteleport.Constants;
import al.yn.simpleteleport.command.arguments.BoolOrUnsetArgumentType;
import al.yn.simpleteleport.command.arguments.EnumArgumentType;
import al.yn.simpleteleport.config.ConfigManager;
import al.yn.simpleteleport.config.data.DataManager;
import al.yn.simpleteleport.config.data.enums.TeleportType;
import al.yn.simpleteleport.utility.CommandHelper;
import al.yn.simpleteleport.utility.ComponentHelper;
import al.yn.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class BackCommand implements ICommand {

    private final ConfigManager configManager;
    private final DataManager dataManager;

    private final LiteralCommandNode<CommandSourceStack> command;

    public BackCommand(ConfigManager configManager, DataManager dataManager) {
        this.configManager = configManager;
        this.dataManager = dataManager;

        this.command = Commands.literal("back")
                .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_BACK))
                .executes(this::onBack)
                .then(Commands.literal("preference")
                        .then(Commands.argument("type", EnumArgumentType.create(TeleportType.class))
                                .then(Commands.argument("value", BoolOrUnsetArgumentType.create())
                                        .requires(r -> this.configManager.getBackEnablePlayerCustomPreference())
                                        .executes(this::onSetPreference)
                                )
                        )
                )
                .build();
    }

    private int onSetPreference(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var sender = source.getSender();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, executor)) {
            return 0;
        }

        var preference = context.getArgument("type", TeleportType.class);
        var value = context.getArgument("value", BoolOrUnsetArgumentType.Value.class);

        var player = (Player) executor;
        assert player != null;
        var data = dataManager.getPlayerData(player);
        if (value.getValue() == null) {
            data.removeLocationRecordingPreference(preference);
        } else {
            data.addLocationRecordingPreference(preference, value.getValue());
        }

        data.save();
        player.sendMessage(ComponentHelper.createBackPreferenceSet());

        return 1;
    }

    private int onBack(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var sender = source.getSender();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, executor)) {
            return 0;
        }

        assert executor != null;
        var data = dataManager.getPlayerData((Player) executor);
        var location = data.getPreviousLocation();
        if (location == null) {
            source.getSender().sendMessage(ComponentHelper.createNoBackPoint());
            return 0;
        }

        if (!TeleportHelper.teleportTo(executor, location)) {
            source.getSender().sendMessage(ComponentHelper.createTeleportFailed());
            return 0;
        }

        source.getSender().sendMessage(ComponentHelper.createBackSuccessful());
        return 1;
    }

    @Override
    public void register(Commands registrar) {
        registrar.register(command);
    }
}
