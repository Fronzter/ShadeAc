package ru.Fronzter.ShadeAc.manager;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import com.google.common.collect.Lists;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.impl.combat.aim.AimA;
import ru.Fronzter.ShadeAc.check.impl.combat.aim.AimB;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraA;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraB;
import ru.Fronzter.ShadeAc.data.PlayerData;

import java.util.List;
import java.util.stream.Collectors;

public class CheckManager {

    private final List<Class<? extends Check>> checkClasses = Lists.newArrayList();

    public CheckManager() {
        registerChecks();
    }

    private void registerChecks() {

        checkClasses.add(KillauraA.class);
        checkClasses.add(KillauraB.class);
        checkClasses.add(AimA.class);
        checkClasses.add(AimB.class);
    }

    public List<Check> loadChecks(PlayerData data) {
        return checkClasses.stream().map(clazz -> {
            try {
                return clazz.getConstructor(PlayerData.class).newInstance(data);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());
    }
}