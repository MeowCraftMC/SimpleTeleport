package al.yn.simpleteleport.command;

import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public interface ICommand {
    void register(Commands registrar);
}
