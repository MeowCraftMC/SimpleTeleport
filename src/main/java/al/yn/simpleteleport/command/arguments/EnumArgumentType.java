package al.yn.simpleteleport.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class EnumArgumentType<E extends Enum<E>> implements CustomArgumentType<E, String> {

    protected final Class<E> type;

    protected EnumArgumentType(Class<E> type) {
        this.type = type;
    }

    public static <E extends Enum<E>> EnumArgumentType<E> create(Class<E> type) {
        return new EnumArgumentType<>(type);
    }

    @Override
    public @NotNull E parse(StringReader reader) throws CommandSyntaxException {
        var str = reader.readUnquotedString();
        try {
            return Enum.valueOf(type, str);
        } catch (IllegalArgumentException ex) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, str);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                                       @NotNull SuggestionsBuilder builder) {
        for (var e : EnumSet.allOf(type)) {
            builder.suggest(e.toString());
        }
        return builder.buildFuture();
    }
}
