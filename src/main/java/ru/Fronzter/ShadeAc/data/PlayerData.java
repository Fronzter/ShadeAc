package ru.Fronzter.ShadeAc.data;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.mitigation.MitigationType;
import ru.Fronzter.ShadeAc.utils.math.DataAnalyzer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {

    private final Player player;
    private final UUID uuid;
    private final List<Check> checks = new ArrayList<>();

    // Rotation дата
    private final Deque<Float> yawHistory = new LinkedList<>();
    private final Deque<Float> pitchHistory = new LinkedList<>();
    private final Deque<Float> deltaYawHistory = new LinkedList<>();
    private final Deque<Float> deltaPitchHistory = new LinkedList<>();
    private float lastYaw, lastPitch;

    // Combat дата
    private final CombatProcessor combatProcessor = new CombatProcessor();
    private long lastAttackTime;
    private final DataAnalyzer clickTimings = new DataAnalyzer(20);

    // Mitigation дата
    private MitigationType nextMitigation = MitigationType.NONE;

    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        instantiateChecks();
    }

    private void instantiateChecks() {
        ShadeAc.getInstance().getCheckManager().getCheckClasses().forEach(checkClass -> {
            try {
                Constructor<? extends Check> constructor = checkClass.getConstructor(PlayerData.class);
                checks.add(constructor.newInstance(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Getter
    public static class CombatProcessor {
        private int ticksSinceLastAttack = Integer.MAX_VALUE;

        public void onAttack() {
            this.ticksSinceLastAttack = 0;
        }

        public void onTick() {
            if (ticksSinceLastAttack < Integer.MAX_VALUE) {
                ticksSinceLastAttack++;
            }
        }


        public boolean isAttacking() {
            return ticksSinceLastAttack <= 40;
        }
    }
}