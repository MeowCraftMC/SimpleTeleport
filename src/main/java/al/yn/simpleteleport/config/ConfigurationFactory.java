package al.yn.simpleteleport.config;

import al.yn.simpleteleport.config.serializer.LocationSerializer;
import al.yn.simpleteleport.config.serializer.WorldSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class ConfigurationFactory {
    private static final ObjectMapper.Factory OBJECT_MAPPER_FACTORY = ObjectMapper.factoryBuilder()
            .addNodeResolver(NodeResolver.onlyWithSetting())
            .build();

    public static void registerSerializers(TypeSerializerCollection.Builder builder) {
        builder.registerAnnotatedObjects(OBJECT_MAPPER_FACTORY)
                .register(Location.class, LocationSerializer.INSTANCE)
                .register(World.class, WorldSerializer.INSTANCE);
    }
}
