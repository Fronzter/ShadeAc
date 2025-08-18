package ru.Fronzter.ShadeAc.config;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MitigationConfig {
    private final boolean enabled;
    private final MitigationType mode;
    private final int durationTicks;
    private final double reduceAmount;

    public enum MitigationType {
        NONE, CANCEL_HITS, REDUCE_DAMAGE
    }
}