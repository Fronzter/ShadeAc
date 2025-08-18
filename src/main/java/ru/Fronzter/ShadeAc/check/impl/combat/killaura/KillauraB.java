package ru.Fronzter.ShadeAc.check.impl.combat.killaura;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.data.PlayerData;
import java.util.ArrayList;
import java.util.List;

public class KillauraB extends Check {

    private final double consistencyTolerance, consistencyMinDelta, jerkThreshold, flickLowThreshold, flickHighThreshold;
    private int buffer;

    public KillauraB(PlayerData data) {
        super(data, "Killaura", "B");

        ShadeAc ac = ShadeAc.getInstance();
        this.consistencyTolerance = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "consistency-tolerance");
        this.consistencyMinDelta = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "consistency-min-delta");
        this.jerkThreshold = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "jerk-threshold");
        this.flickLowThreshold = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "flick-low-threshold");
        this.flickHighThreshold = ac.getConfigManager().getCheckValueDouble(getName(), getType(), "flick-high-threshold");
    }

    public void handleUseEntity(WrapperPlayClientInteractEntity packet) {
        if (data.getDeltaYawHistory().size() < 4) {
            return;
        }

        List<Float> deltas = new ArrayList<>(data.getDeltaYawHistory());

        float d3 = Math.abs(deltas.get(deltas.size() - 1));
        float d2 = Math.abs(deltas.get(deltas.size() - 2));
        float d1 = Math.abs(deltas.get(deltas.size() - 3));
        float d0 = Math.abs(deltas.get(deltas.size() - 4));

        int points = 0;
        String debug = "";

        // Проверка на консистентность
        if (Math.abs(d3 - d2) < consistencyTolerance && d3 > consistencyMinDelta) {
            points++;
            debug += "C "; // Consistency
        }

        // Проверка на рывок (Jerk)
        double acceleration1 = d2 - d1;
        double acceleration2 = d3 - d2;
        double jerk = Math.abs(acceleration2 - acceleration1);

        if (jerk > jerkThreshold) {
            points++;
            debug += "J "; // Jerk
        }

        // Проверка на паттерн флика
        if (d1 < flickLowThreshold && d2 > flickHighThreshold) {
            points++;
            debug += "F "; // Flick
        }

        if (points >= 2) {
            buffer += points;
            if (buffer > 8) {
                flag("points: " + points, "buffer: " + buffer, "debug: " + debug.trim());
                buffer /= 2;
            }
        } else {
            buffer = Math.max(0, buffer - 1);
        }
    }
}