package ru.Fronzter.ShadeAc.check.impl.combat.aim;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.configuration.ConfigurationSection;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.CheckCategory;
import ru.Fronzter.ShadeAc.check.CheckInfo;
import ru.Fronzter.ShadeAc.data.PlayerData;
import ru.Fronzter.ShadeAc.data.update.RotationUpdate;
import ru.Fronzter.ShadeAc.util.math.StatisticUtil;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(
        name = "Aim",
        subType = "A",
        category = CheckCategory.COMBAT,
        description = "Detects robotic aiming smoothness (Aim Assist)."
)
public class AimA extends Check {

    private final double maxDeviation;
    private final double maxJerk;

    public AimA(PlayerData playerData) {
        super(playerData);
        ConfigurationSection section = ShadeAc.getInstance().getConfigManager().getCheckSection(this);
        if (section != null) {
            this.maxDeviation = section.getDouble("max-deviation", 0.09);
            this.maxJerk = section.getDouble("max-jerk", 0.12);
        } else {
            this.maxDeviation = 0.09;
            this.maxJerk = 0.12;
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY &&
                event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
            playerData.setLastAttackTime(System.currentTimeMillis());
        }
    }

    @Override
    public void onRotation(RotationUpdate update) {
        boolean inCombat = (System.currentTimeMillis() - playerData.getLastAttackTime()) < 1000L;
        if (!inCombat) return;

        playerData.getHeuristics().addSample(playerData.getHeuristics().getCombatYawDeltas(), update.getDeltaYaw());
        playerData.getHeuristics().addSample(playerData.getHeuristics().getCombatPitchDeltas(), update.getDeltaPitch());

        if (playerData.getHeuristics().getCombatYawDeltas().size() == 20) {
            analyze(new ArrayList<>(playerData.getHeuristics().getCombatYawDeltas()));
        }
    }

    private void analyze(List<Double> deltas) {
        double deviation = StatisticUtil.getStandardDeviation(deltas);
        double jerk = StatisticUtil.getJerk(deltas);

        boolean suspicious = deviation > 0.001 && deviation < maxDeviation && jerk < maxJerk;

        if (suspicious) {
            flag(String.format("dev:%.4f jerk:%.4f", deviation, jerk));
        } else {
            decay();
        }
    }
}
