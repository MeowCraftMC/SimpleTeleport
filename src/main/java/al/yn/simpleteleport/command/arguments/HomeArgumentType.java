package al.yn.simpleteleport.command.arguments;

import al.yn.simpleteleport.config.data.DataManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class HomeArgumentType extends SpaceBreakStringArgumentType {

    private final DataManager dataManager;

    protected HomeArgumentType(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public static HomeArgumentType create(DataManager dataManager) {
        return new HomeArgumentType(dataManager);
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                                       @NotNull SuggestionsBuilder builder) {
        if (context.getSource() instanceof CommandSourceStack source) {
            if (source.getSender() instanceof Player player) {
                var data = dataManager.getPlayerData(player);
                for (var e : data.getHomes().entrySet()) {
                    builder.suggest(e.getKey());
                }
            }
        }
        return builder.buildFuture();
    }
}
