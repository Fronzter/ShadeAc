package ru.Fronzter.ShadeAc.analysis;

import lombok.Getter;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;
import ru.Fronzter.ShadeAc.util.math.MinecraftMath;
import ru.Fronzter.ShadeAc.util.type.RunningMode;

import java.util.Map;

@Getter
public class RotationProcessor {

    private final PlayerData playerData;

    private static final int SIGNIFICANT_SAMPLES_THRESHOLD = 15;
    private static final int TOTAL_SAMPLES_THRESHOLD = 80;

    private final RunningMode yawGcdMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);
    private final RunningMode pitchGcdMode = new RunningMode(TOTAL_SAMPLES_THRESHOLD);

    private float lastYawDelta, lastPitchDelta;

    private double sensitivityYaw, sensitivityPitch;
    private double lastGcdYaw, lastGcdPitch;
    private double modeYaw, modePitch;

    public RotationProcessor(PlayerData playerData) {
        this.playerData = playerData;
    }

    public void process(final RotationUpdate update) {
        float deltaYaw = update.getAbsDeltaYaw();

        this.lastGcdYaw = MinecraftMath.getSensitivityGCD(deltaYaw, lastYawDelta);

        if (deltaYaw > 0 && deltaYaw < 5 && lastGcdYaw > MinecraftMath.MINIMUM_SENSITIVITY_DIVISOR) {
            this.yawGcdMode.add(lastGcdYaw);
            this.lastYawDelta = deltaYaw;
        }

        float deltaPitch = update.getAbsDeltaPitch();
        this.lastGcdPitch = MinecraftMath.getSensitivityGCD(deltaPitch, lastPitchDelta);
        if (deltaPitch > 0 && deltaPitch < 5 && lastGcdPitch > MinecraftMath.MINIMUM_SENSITIVITY_DIVISOR) {
            this.pitchGcdMode.add(lastGcdPitch);
            this.lastPitchDelta = deltaPitch;
        }

        if (this.yawGcdMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
            Map.Entry<Double, Integer> mode = this.yawGcdMode.getMode();
            if (mode != null && mode.getValue() > SIGNIFICANT_SAMPLES_THRESHOLD) {
                this.modeYaw = mode.getKey();
                this.sensitivityYaw = convertGcdToSensitivity(this.modeYaw);
            }
        }
        if (this.pitchGcdMode.size() > SIGNIFICANT_SAMPLES_THRESHOLD) {
            Map.Entry<Double, Integer> mode = this.pitchGcdMode.getMode();
            if (mode != null && mode.getValue() > SIGNIFICANT_SAMPLES_THRESHOLD) {
                this.modePitch = mode.getKey();
                this.sensitivityPitch = convertGcdToSensitivity(this.modePitch);
            }
        }
    }

    private double convertGcdToSensitivity(double gcd) {
        // (gcd / 0.15 / 8.0) ^ (1/3)
        double cubed = gcd / 1.2;
        double cbrt = Math.cbrt(cubed);
        return (cbrt - 0.2) / 0.6;
    }
}