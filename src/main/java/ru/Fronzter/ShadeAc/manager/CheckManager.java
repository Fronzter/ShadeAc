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
import lombok.Getter;
import ru.Fronzter.ShadeAc.check.Check;
import ru.Fronzter.ShadeAc.check.impl.combat.autoclicker.AutoclickerA;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraA;
import ru.Fronzter.ShadeAc.check.impl.combat.killaura.KillauraB;
import ru.Fronzter.ShadeAc.check.impl.combat.ml.KillauraHandmadeML;

import java.util.List;

@Getter
public class CheckManager {

    private final List<Class<? extends Check>> checkClasses = Lists.newArrayList();

    public CheckManager() {
        registerChecks();
    }

    private void registerChecks() {
        // регистрация проверок очень легкая, просто добните проверку сюда
        checkClasses.add(AutoclickerA.class);
        checkClasses.add(KillauraA.class);
        checkClasses.add(KillauraB.class);
        checkClasses.add(KillauraHandmadeML.class);
    }
}