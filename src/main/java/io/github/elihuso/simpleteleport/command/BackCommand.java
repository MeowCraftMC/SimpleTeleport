package io.github.elihuso.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.elihuso.simpleteleport.Constants;
import io.github.elihuso.simpleteleport.SimpleTeleport;
import io.github.elihuso.simpleteleport.utility.CommandHelper;
import io.github.elihuso.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class BackCommand implements ICommand {

    private final SimpleTeleport plugin;

    public BackCommand(SimpleTeleport plugin) {
        this.plugin = plugin;
    }

    private final LiteralCommandNode<CommandSourceStack> COMMAND = Commands.literal("back")
            .requires(r -> r.getExecutor() != null && r.getExecutor().hasPermission(Constants.PERMISSION_BACK))
            .executes(this::onBack)
            .build();

    private int onBack(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var sender = source.getSender();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(sender, executor)) {
            return 0;
        }

        var location = plugin.getDataManager().getLocation(executor.getUniqueId());
        if (location == null) {
            source.getSender().sendMessage(Component.text("No previous location found.").color(NamedTextColor.RED));
            return 0;
        }

        TeleportHelper.teleportTo(executor, location);
        return 1;
    }

    @Override
    public void register(Commands registrar) {
        registrar.register(COMMAND);
    }
}
