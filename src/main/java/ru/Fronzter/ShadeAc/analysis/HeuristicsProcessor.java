package ru.Fronzter.ShadeAc.analysis;

import lombok.Getter;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
public class HeuristicsProcessor {

    private final PlayerData playerData;
    private static final int MAX_SAMPLES = 20;

    private final Deque<Double> yawDeltas = new ArrayDeque<>();
    private final Deque<Double> pitchDeltas = new ArrayDeque<>();
    private final Deque<Long> clickIntervals = new ArrayDeque<>();
    private final Deque<Double> combatYawDeltas = new ArrayDeque<>();
    private final Deque<Double> combatPitchDeltas = new ArrayDeque<>();

    public HeuristicsProcessor(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void addSample(Deque<Double> deque, double sample) {
        deque.add(sample);
        if (deque.size() > MAX_SAMPLES) {
            deque.removeFirst();
        }
    }

    public void addSample(Deque<Long> deque, long sample) {
        deque.add(sample);
        if (deque.size() > MAX_SAMPLES) {
            deque.removeFirst();
        }
    }
}