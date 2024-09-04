package io.github.elihuso.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.elihuso.simpleteleport.Constants;
import io.github.elihuso.simpleteleport.config.data.DataManager;
import io.github.elihuso.simpleteleport.utility.CommandHelper;
import io.github.elihuso.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class BackCommand implements ICommand {

    private final DataManager dataManager;

    public BackCommand(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    // Todo: server config to disable
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
            source.getSender().sendMessage(Component.text("No previous location recorded.").color(NamedTextColor.RED));
            return 0;
        }

        if (!TeleportHelper.teleportTo(executor, location)) {
            source.getSender().sendMessage(Component.text("Teleport failed due to bukkit api limit.").color(NamedTextColor.RED));
            return 0;
        }
        return 1;
    }

    @Override
    public void register(Commands registrar) {
        registrar.register(COMMAND);
    }
}
