package io.github.elihuso.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.elihuso.simpleteleport.Constants;
import io.github.elihuso.simpleteleport.config.data.DataManager;
import io.github.elihuso.simpleteleport.utility.CommandHelper;
import io.github.elihuso.simpleteleport.utility.ComponentHelper;
import io.github.elihuso.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class BackCommand implements ICommand {

    private final DataManager dataManager;

    public BackCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    private final LiteralCommandNode<CommandSourceStack> COMMAND = Commands.literal("back")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_BACK))
            .executes(this::onBack)
            .build();

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
        registrar.register(COMMAND);
    }
}
