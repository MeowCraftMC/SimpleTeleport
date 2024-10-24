package al.yn.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import al.yn.simpleteleport.Constants;
import al.yn.simpleteleport.utility.CommandHelper;
import al.yn.simpleteleport.utility.ComponentHelper;
import al.yn.simpleteleport.utility.TeleportHelper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class WorldTeleportCommand implements ICommand {

    private final LiteralCommandNode<CommandSourceStack> spawn = Commands.literal("spawn")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TP_SPAWN))
            .executes(this::onSpawn)
            .build();

    private final LiteralCommandNode<CommandSourceStack> bed = Commands.literal("bed")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TP_BED))
            .executes(this::onBed)
            .build();

    private final LiteralCommandNode<CommandSourceStack> top = Commands.literal("top")
            .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TP_TOP))
            .executes(this::onTop)
            .build();

    @Override
    public void register(Commands registrar) {
        registrar.register(spawn);
        registrar.register(bed);
        registrar.register(top);
    }

    private int onSpawn(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), executor)) {
            return 0;
        }

        assert executor != null;
        if (!TeleportHelper.teleportTo(executor, executor.getWorld().getSpawnLocation())) {
            source.getSender().sendMessage(ComponentHelper.createTeleportFailed());
            return 0;
        }

        source.getSender().sendMessage(ComponentHelper.createTpSpawn());
        return 1;
    }

    private int onBed(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), executor)) {
            return 0;
        }

        var player = (Player) executor;
        assert player != null;
        var location = player.getRespawnLocation();

        if (location == null) {
            player.sendMessage(ComponentHelper.createTpNoBed());
            return 0;
        }

        if (!TeleportHelper.teleportTo(player, location)) {
            player.sendMessage(ComponentHelper.createTeleportFailed());
            return 0;
        }

        source.getSender().sendMessage(ComponentHelper.createTpBed());
        return 1;
    }

    private int onTop(CommandContext<CommandSourceStack> context) {
        var source = context.getSource();
        var executor = source.getExecutor();
        if (!CommandHelper.ensureAsPlayer(source.getSender(), executor)) {
            return 0;
        }

        assert executor != null;
        var y = executor.getWorld().getHighestBlockYAt(executor.getLocation());
        var location = executor.getLocation().clone();
        location.setY(y + 1);
        if (!TeleportHelper.teleportTo(executor, location)) {
            source.getSender().sendMessage(ComponentHelper.createTeleportFailed());
            return 0;
        }

        source.getSender().sendMessage(ComponentHelper.createTpTop());
        return 1;
    }
}
