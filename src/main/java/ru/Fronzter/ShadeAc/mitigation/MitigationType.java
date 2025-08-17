package ru.Fronzter.ShadeAc.mitigation;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

public enum MitigationType {
    // никаких действий
    NONE,
    // отмена удара (потом сделаю чтобы на время)
    CANCEL_DAMAGE,
    // уменьшение урона от атаки на 50%
    REDUCE_DAMAGE
}