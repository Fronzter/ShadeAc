package ru.Fronzter.ShadeAc.ml;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.impl.combat.ml.KillauraHandmadeML;
import ru.Fronzter.ShadeAc.ml.handmade.Matrix;
import ru.Fronzter.ShadeAc.ml.handmade.SimpleNeuralNetwork;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModelTrainer extends BukkitRunnable {

    private final ShadeAc plugin;
    private final CommandSender sender;
    private final int epochs;

    public static final int INPUT_NODES = DataCollectionManager.SEQUENCE_LENGTH;
    public static final int HIDDEN_NODES = 8;
    public static final int OUTPUT_NODES = 2;

    public ModelTrainer(ShadeAc plugin, CommandSender sender, int epochs) {
        this.plugin = plugin;
        this.sender = sender;
        this.epochs = epochs;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        sendMessage(ChatColor.YELLOW + "Начинаю загрузку датасета...");

        try {
            List<TrainingData> trainingDataset = loadDataset();
            if (trainingDataset.isEmpty()) {
                sendMessage(ChatColor.RED + "Датасет пуст! Сначала соберите данные командой /sacml start.");
                return;
            }
            sendMessage(ChatColor.GREEN + "Загружено " + trainingDataset.size() + " последовательностей для обучения.");

            SimpleNeuralNetwork network = new SimpleNeuralNetwork(INPUT_NODES, HIDDEN_NODES, OUTPUT_NODES);

            sendMessage(ChatColor.YELLOW + "Начинаю обучение... Отслеживайте прогресс в консоли.");

            for (int i = 0; i < epochs; i++) {
                Collections.shuffle(trainingDataset);
                for (TrainingData data : trainingDataset) {
                    network.train(data.getInputs(), data.getTargets());
                }

                if ((i + 1) % (epochs / 10 == 0 ? 1 : epochs / 10) == 0) {
                    plugin.getLogger().info("[ShadeAc ML Trainer] Прогресс обучения: " + (i + 1) + "/" + epochs + " эпох.");
                }
            }

            sendMessage(ChatColor.GREEN + "Обучение успешно завершено!");
            sendMessage(ChatColor.YELLOW + "Сохраняю новые веса в network_weights.yml...");

            saveWeights(network);
            KillauraHandmadeML.reloadNetwork();

            long duration = (System.currentTimeMillis() - startTime) / 1000;
            sendMessage(ChatColor.GOLD + "Готово! Веса сохранены и загружены. Время обучения: " + duration + " секунд.");

        } catch (Exception e) {
            sendMessage(ChatColor.RED + "Во время обучения произошла критическая ошибка. Подробности в консоли.");
            e.printStackTrace();
        }
    }

    private List<TrainingData> loadDataset() throws IOException {
        List<TrainingData> dataset = new ArrayList<>();
        int skippedLines = 0;

        File legitDir = new File(plugin.getDataFolder(), "dataset/legit");
        File cheatDir = new File(plugin.getDataFolder(), "dataset/cheat");

        if (legitDir.listFiles() != null) {
            for (File file : legitDir.listFiles(f -> f.getName().endsWith(".csv"))) {
                for (String line : Files.readAllLines(file.toPath())) {
                    try {
                        dataset.add(new TrainingData(line, new float[]{1.0f, 0.0f})); // [legit, cheat]
                    } catch (IllegalArgumentException e) {
                        skippedLines++;
                    }
                }
            }
        }

        if (cheatDir.listFiles() != null) {
            for (File file : cheatDir.listFiles(f -> f.getName().endsWith(".csv"))) {
                for (String line : Files.readAllLines(file.toPath())) {
                    try {
                        dataset.add(new TrainingData(line, new float[]{0.0f, 1.0f})); // [legit, cheat]
                    } catch (IllegalArgumentException e) {
                        skippedLines++;
                    }
                }
            }
        }

        if (skippedLines > 0) {
            plugin.getLogger().warning("[ShadeAc ML Trainer] Пропущено " + skippedLines + " строк в датасете из-за неверной длины.");
        }

        return dataset;
    }

    private void saveWeights(SimpleNeuralNetwork network) throws IOException {
        File weightsFile = new File(plugin.getDataFolder(), "network_weights.yml");
        FileConfiguration config = new YamlConfiguration();

        config.set("weights_input_hidden", matrixTo2DList(network.weights_ih));
        config.set("weights_hidden_output", matrixTo2DList(network.weights_ho));
        config.set("bias_hidden", matrixTo1DList(network.bias_h));
        config.set("bias_output", matrixTo1DList(network.bias_o));

        config.save(weightsFile);
    }

    private List<List<Double>> matrixTo2DList(Matrix m) {
        return java.util.Arrays.stream(m.data)
                .map(row -> java.util.Arrays.stream(row).boxed().collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private List<Double> matrixTo1DList(Matrix m) {
        return java.util.Arrays.stream(m.toArray()).boxed().collect(Collectors.toList());
    }

    private void sendMessage(String message) {
        Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(message));
    }

    private static class TrainingData {
        private final float[] inputs;
        private final float[] targets;

        TrainingData(String csvLine, float[] targets) {
            String[] values = csvLine.split(",");
            if (values.length != INPUT_NODES) {
                throw new IllegalArgumentException("Неверная длина строки в датасете!");
            }
            this.inputs = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                this.inputs[i] = Float.parseFloat(values[i]);
            }
            this.targets = targets;
        }
        public float[] getInputs() { return inputs; }
        public float[] getTargets() { return targets; }
    }
}