package ru.Fronzter.ShadeAc.check.impl.combat.ml;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.ml.DataCollectionManager;
import ru.Fronzter.ShadeAc.ml.ModelTrainer;
import ru.Fronzter.ShadeAc.ml.handmade.Matrix;
import ru.Fronzter.ShadeAc.ml.handmade.SimpleNeuralNetwork;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class KillauraHandmadeML extends Check {

    private static SimpleNeuralNetwork network;
    private final Queue<Float> movementHistory = new LinkedList<>();

    private static final int INPUT_SIZE = DataCollectionManager.SEQUENCE_LENGTH;
    private static final int HIDDEN_NODES = ModelTrainer.HIDDEN_NODES;
    private static final int OUTPUT_NODES = ModelTrainer.OUTPUT_NODES;

    private final double threshold;
    private final boolean debugMode;

    public KillauraHandmadeML(PlayerData data) {
        super(data, "Killaura", "HML");

        ShadeAc ac = ShadeAc.getInstance();
        this.threshold = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "threshold");
        this.debugMode = ac.getConfigManager().getCheckValueBoolean(getName(), getType(), "debug-mode");

        if (network == null) {
            loadNetworkFromFile();
        }
    }

    public void handleRotation(float deltaYaw, float deltaPitch) {
        movementHistory.add(deltaYaw);
        movementHistory.add(deltaPitch);

        while (movementHistory.size() > INPUT_SIZE) {
            movementHistory.poll();
        }
    }

    public void handleAttack() {
        if (network == null) return;

        if (movementHistory.size() == INPUT_SIZE) {
            float[] inputs = new float[INPUT_SIZE];
            int i = 0;
            for (Float val : movementHistory) {
                inputs[i++] = val;
            }

            double[] prediction = network.predict(inputs);
            double cheatProbability = prediction[1]; // вероятность того, что это чит

            if (cheatProbability > this.threshold) {
                flag(String.format("prob: §e%.2f%%", cheatProbability * 100));
            } else if (debugMode && cheatProbability > 0.1) {
                String rawMessage = String.format("&8[&3ShadeAc Debug&8] &b%s &7ML Prob (on attack): &e%.2f%%",
                        getData().getPlayer().getName(),
                        cheatProbability * 100);
                String debugMessage = ChatColor.translateAlternateColorCodes('&', rawMessage);

                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.hasPermission(ShadeAc.getInstance().getConfigManager().getAlertPermission()))
                        .forEach(p -> p.sendMessage(debugMessage));
            }
        }
    }

    public static void reloadNetwork() {
        Bukkit.getScheduler().runTaskAsynchronously(ShadeAc.getInstance(), () -> {
            network = null;
            loadNetworkFromFile();
        });
    }

    private static synchronized void loadNetworkFromFile() {
        if (network != null) return;

        ShadeAc plugin = ShadeAc.getInstance();
        File weightsFile = new File(plugin.getDataFolder(), "network_weights.yml");
        if (!weightsFile.exists()) {
            plugin.saveResource("network_weights.yml", false);
        }

        FileConfiguration weightsConfig = YamlConfiguration.loadConfiguration(weightsFile);

        try {
            Matrix w_ih = loadMatrixFromConfig(weightsConfig, "weights_input_hidden");
            Matrix w_ho = loadMatrixFromConfig(weightsConfig, "weights_hidden_output");
            Matrix b_h = loadVectorFromConfig(weightsConfig, "bias_hidden");
            Matrix b_o = loadVectorFromConfig(weightsConfig, "bias_output");

            validateMatrix("weights_input_hidden", w_ih, HIDDEN_NODES, INPUT_SIZE);
            validateMatrix("weights_hidden_output", w_ho, OUTPUT_NODES, HIDDEN_NODES);
            validateMatrix("bias_hidden", b_h, HIDDEN_NODES, 1);
            validateMatrix("bias_output", b_o, OUTPUT_NODES, 1);

            network = new SimpleNeuralNetwork(w_ih, w_ho, b_h, b_o);
            plugin.getLogger().info("ShadeAc Handmade ML: Нейросеть успешно загружена!");

        } catch (Exception e) {
            plugin.getLogger().severe("ShadeAc Handmade ML: КРИТИЧЕСКАЯ ОШИБКА загрузки весов! " + e.getMessage());
            network = null;
        }
    }

    private static void validateMatrix(String name, Matrix m, int expectedRows, int expectedCols) {
        if (m.rows != expectedRows || m.cols != expectedCols) {
            throw new IllegalArgumentException(String.format(
                    "Неверный размер матрицы '%s'. Ожидалось: %dx%d, Найдено: %dx%d.",
                    name, expectedRows, expectedCols, m.rows, m.cols
            ));
        }
    }

    @SuppressWarnings("unchecked")
    private static Matrix loadMatrixFromConfig(FileConfiguration config, String path) {
        List<List<Double>> listOfLists = (List<List<Double>>) config.getList(path);
        if (listOfLists == null || listOfLists.isEmpty()) throw new RuntimeException("Пустая матрица: " + path);
        int rows = listOfLists.size(); int cols = listOfLists.get(0).size();
        Matrix m = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) for (int j = 0; j < cols; j++) m.data[i][j] = listOfLists.get(i).get(j);
        return m;
    }

    private static Matrix loadVectorFromConfig(FileConfiguration config, String path) {
        List<Double> list = config.getDoubleList(path);
        if (list == null || list.isEmpty()) throw new RuntimeException("Пустой вектор: " + path);
        Matrix m = new Matrix(list.size(), 1);
        for (int i = 0; i < list.size(); i++) m.data[i][0] = list.get(i);
        return m;
    }
}