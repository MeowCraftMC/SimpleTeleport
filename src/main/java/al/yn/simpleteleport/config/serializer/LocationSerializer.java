package al.yn.simpleteleport.config.serializer;

import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;

public class LocationSerializer implements TypeSerializer<Location> {

    public static final LocationSerializer INSTANCE = new LocationSerializer();

    private ConfigurationNode getOrThrow(ConfigurationNode node, Object... path) throws SerializationException {
        if (!node.hasChild(path)) {
            throw new SerializationException("Field " + Arrays.toString(path) + " is required!");
        }
        return node.node(path);
    }

    @Override
    public Location deserialize(Type type, ConfigurationNode node) throws SerializationException {
        World world = null;
        if (node.hasChild("world")) {
            world = node.node("world").get(World.class);
        }

        var x = getOrThrow(node, "x").getDouble();
        var y = getOrThrow(node, "y").getDouble();
        var z = getOrThrow(node, "z").getDouble();
        var pitch = getOrThrow(node, "pitch").getFloat();
        var yaw = getOrThrow(node, "yaw").getFloat();

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            node.raw(null);
            return;
        }

        if (obj.isWorldLoaded()) {
            node.node("world").set(obj.getWorld());
        } else {
            node.node("world").set(null);
        }
        node.node("x").set(obj.getX());
        node.node("y").set(obj.getY());
        node.node("z").set(obj.getZ());
        node.node("pitch").set(obj.getPitch());
        node.node("yaw").set(obj.getYaw());
    }
}
