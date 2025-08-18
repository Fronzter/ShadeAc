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
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PunishmentConfig {
    private final boolean animate;
    private final int maxVl;
    private final String reason;
    private final List<String> commands;
}
