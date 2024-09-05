package io.github.elihuso.simpleteleport.config.serializer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class WorldSerializer implements TypeSerializer<World> {

    public static final WorldSerializer INSTANCE = new WorldSerializer();

    @Override
    public World deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var str = node.getString();
        if (str != null) {
            return Bukkit.getWorld(str);
        }

        return null;
    }

    @Override
    public void serialize(Type type, @Nullable World obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        node.set(obj.getName());
    }
}
