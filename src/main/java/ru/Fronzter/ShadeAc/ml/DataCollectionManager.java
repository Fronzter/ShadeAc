package ru.Fronzter.ShadeAc.ml;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class DataCollectionManager {
    private final ShadeAc plugin;
    private final Map<UUID, DataSession> activeSessions = new HashMap<>();
    public static final int SEQUENCE_LENGTH = 20;

    public DataCollectionManager(ShadeAc plugin) {
        this.plugin = plugin;
        File datasetDir = new File(plugin.getDataFolder(), "dataset");
        if (!datasetDir.exists()) {
            datasetDir.mkdirs();
            new File(datasetDir, "legit").mkdirs();
            new File(datasetDir, "cheat").mkdirs();
        }
    }

    public void startSession(Player player, String label) {
        DataSession session = new DataSession(label);
        if (session.isReady()) {
            activeSessions.put(player.getUniqueId(), session);
        }
    }

    public void stopSession(Player player) {
        DataSession session = activeSessions.remove(player.getUniqueId());
        if (session != null) {
            session.close();
        }
    }

    public void onRotation(Player player, float deltaYaw, float deltaPitch) {
        PlayerData data = ShadeAc.getInstance().getPlayerManager().getPlayerData(player);
        if (data != null && data.getCombatProcessor().isAttacking()) {
            DataSession session = activeSessions.get(player.getUniqueId());
            if (session != null) {
                session.addMovement(deltaYaw, deltaPitch);
            }
        }
    }

    private class DataSession {
        private final PrintWriter writer;
        private final Queue<Float> yawQueue = new LinkedList<>();
        private final Queue<Float> pitchQueue = new LinkedList<>();
        public final String fileName;

        DataSession(String label) {
            File dataFolder = new File(plugin.getDataFolder(), "dataset/" + label);
            File dataFile = new File(dataFolder, "session-" + System.currentTimeMillis() + ".csv");
            this.fileName = dataFile.getName();

            PrintWriter tempWriter = null;
            try {
                tempWriter = new PrintWriter(new FileWriter(dataFile, true));
            } catch (IOException ignored) {}
            this.writer = tempWriter;
        }

        boolean isReady() {
            return writer != null;
        }

        void addMovement(float dY, float dP) {
            if (Math.abs(dY) < 0.01 && Math.abs(dP) < 0.01) return;
            yawQueue.add(dY);
            pitchQueue.add(dP);
            if (yawQueue.size() >= SEQUENCE_LENGTH / 2) {
                saveSequence();
                yawQueue.poll();
                pitchQueue.poll();
            }
        }

        void saveSequence() {
            StringBuilder sb = new StringBuilder();
            yawQueue.forEach(y -> sb.append(String.format("%.4f", y).replace(",", ".")).append(","));
            pitchQueue.forEach(p -> sb.append(String.format("%.4f", p).replace(",", ".")).append(","));
            sb.deleteCharAt(sb.length() - 1);
            writer.println(sb.toString());
        }

        void close() {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }
}
