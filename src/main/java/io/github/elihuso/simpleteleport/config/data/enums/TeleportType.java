package io.github.elihuso.simpleteleport.config.data.enums;

import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

import static org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.*;

public enum TeleportType {
    GAME_PLAY(List.of(DISMOUNT, EXIT_BED, SPECTATE)),
    ENDER_TELEPORT(List.of(ENDER_PEARL, CHORUS_FRUIT)),
    PORTAL(List.of(NETHER_PORTAL, END_PORTAL, END_GATEWAY)),
    DEATH(List.of()),
    SYSTEM(List.of(COMMAND, PLUGIN)),
    OTHER(List.of(UNKNOWN));

    private final List<PlayerTeleportEvent.TeleportCause> causes;

    TeleportType(List<PlayerTeleportEvent.TeleportCause> causes) {
        this.causes = causes;
    }

    public boolean matches(PlayerTeleportEvent.TeleportCause cause) {
        return causes.contains(cause);
    }

    public static TeleportType fromBukkit(PlayerTeleportEvent.TeleportCause cause) {
        return switch (cause) {
            case DISMOUNT, EXIT_BED, SPECTATE -> GAME_PLAY;
            case ENDER_PEARL, CHORUS_FRUIT -> ENDER_TELEPORT;
            case NETHER_PORTAL, END_PORTAL, END_GATEWAY -> PORTAL;
            case COMMAND, PLUGIN -> SYSTEM;
            case UNKNOWN -> OTHER;
        };
    }
}
