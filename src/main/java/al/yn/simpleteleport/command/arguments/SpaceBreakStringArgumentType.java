package al.yn.simpleteleport.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

@SuppressWarnings("UnstableApiUsage")
public class SpaceBreakStringArgumentType implements CustomArgumentType<String, String> {

    private static final Pattern NON_SPACE_PATTERN = Pattern.compile("^\\S+");

    protected SpaceBreakStringArgumentType() {
    }

    public static SpaceBreakStringArgumentType string() {
        return new SpaceBreakStringArgumentType();
    }

    @Override
    public @NotNull String parse(@NotNull StringReader reader) throws CommandSyntaxException {
        final var next = reader.peek();
        if (StringReader.isQuotedStringStart(next)) {
            return reader.readString();
        }
        final var text = reader.getRemaining();
        var matcher = NON_SPACE_PATTERN.matcher(text);
        if (matcher.find()) {
            reader.setCursor(reader.getCursor() + matcher.end());
            return matcher.group();
        }
        return "";
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }
}
