package io.github.elihuso.simpleteleport.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.elihuso.simpleteleport.Constants;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TeleportCommand implements ICommand {

    private final LiteralCommandNode<CommandSourceStack> TPA = Commands.literal("tpa")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_USE))
                    .executes(this::onTpa)
            )
            .build();

    private final LiteralCommandNode<CommandSourceStack> TP_HERE = Commands.literal("tphere")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_HERE))
            )
            .build();
    private final LiteralCommandNode<CommandSourceStack> TP_CANCEL = Commands.literal("tpcancel")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
            )
            .build();
    private final LiteralCommandNode<CommandSourceStack> TP_ACCEPT = Commands.literal("tpaccept")
            .then(Commands.argument("target", ArgumentTypes.player())
                    .requires(r -> r.getSender().hasPermission(Constants.PERMISSION_TPA_CANCEL))
            )
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

    private int onTpa(CommandContext<CommandSourceStack> context) {


        return 0;
    }

}
