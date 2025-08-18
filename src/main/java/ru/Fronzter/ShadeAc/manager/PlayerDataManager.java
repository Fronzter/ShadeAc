package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    @Getter
    private final Map<UUID, PlayerData> playerDataMap = new ConcurrentHashMap<>();

    public void createData(Player player) {
        playerDataMap.put(player.getUniqueId(), new PlayerData(player));
    }

    public void removeData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }

    public PlayerData getData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }
}