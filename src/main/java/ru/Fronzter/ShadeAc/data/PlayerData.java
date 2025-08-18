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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.Fronzter.ShadeAc.ShadeAc;
import ru.Fronzter.ShadeAc.analysis.HeuristicsProcessor;
import ru.Fronzter.ShadeAc.analysis.RotationProcessor;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.config.MitigationConfig;
import ru.Fronzter.ShadeAc.data.module.MovementData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class PlayerData {


    private final Player player;
    private final UUID uuid;
    private final List<Check> checks;
    private final HeuristicsProcessor heuristics;
    private final RotationProcessor rotationProcessor;
    private final MovementData movement;
    private final Set<String> punishedChecks = new HashSet<>();
    private MitigationConfig.MitigationType mitigationType = MitigationConfig.MitigationType.NONE;
    private long mitigationExpiry;
    private double damageReductionFactor;
    private long lastAttackTime;
    private Location lastLocation;
    private boolean inLiquid;
    private boolean onClimbable;
    private boolean inWeb;
    private int ticksSinceSlime;
    private boolean underBlock;


    public PlayerData(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();

        this.checks = ShadeAc.getInstance().getCheckManager().loadChecks(this);
        this.heuristics = new HeuristicsProcessor(this);
        this.rotationProcessor = new RotationProcessor(this);
        this.movement = new MovementData(this);

        this.lastLocation = player.getLocation().clone();
        this.lastAttackTime = 0L;
    }

    public void setMitigation(MitigationConfig.MitigationType type, long expiry, double reduction) {
        this.mitigationType = type;
        this.mitigationExpiry = expiry;
        this.damageReductionFactor = 1.0 - reduction;
    }

    public boolean isMitigated() {
        if (System.currentTimeMillis() > mitigationExpiry) {
            if (mitigationType != MitigationConfig.MitigationType.NONE) {
                mitigationType = MitigationConfig.MitigationType.NONE;
            }
            return false;
        }
        return mitigationType != MitigationConfig.MitigationType.NONE;
    }

    public boolean isPunished(Check check) {
        return punishedChecks.contains(check.getFullName());
    }

    public void setPunished(Check check, boolean punished) {
        if (punished) {
            punishedChecks.add(check.getFullName());
        } else {
            punishedChecks.remove(check.getFullName());
        }
    }
}