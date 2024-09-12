package io.github.elihuso.simpleteleport.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class BoolOrUnsetArgumentType extends EnumArgumentType<BoolOrUnsetArgumentType.Value> {

    protected BoolOrUnsetArgumentType() {
        super(BoolOrUnsetArgumentType.Value.class);
    }

    public static BoolOrUnsetArgumentType create() {
        return new BoolOrUnsetArgumentType();
    }

    @Override
    public @NotNull BoolOrUnsetArgumentType.Value parse(StringReader reader) throws CommandSyntaxException {
        var str = reader.readUnquotedString();
        try {
            return Enum.valueOf(type, str.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect().createWithContext(reader, str);
        }
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context,
                                                                       @NotNull SuggestionsBuilder builder) {
        for (var e : EnumSet.allOf(type)) {
            builder.suggest(e.toString().toLowerCase(Locale.ROOT));
        }
        return builder.buildFuture();
    }

    public enum Value {
        TRUE(true),
        FALSE(false),
        UNSET(null),
        ;

        private final Boolean value;

        Value(Boolean value) {
            this.value = value;
        }

        public Boolean getValue() {
            return value;
        }
    }
}
