package ru.Fronzter.ShadeAc.ml.handmade;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */

import java.util.function.DoubleUnaryOperator;

public class ActivationFunctions {
    public static final DoubleUnaryOperator SIGMOID = x -> 1 / (1 + Math.exp(-x));
    public static final DoubleUnaryOperator SIGMOID_DERIVATIVE = y -> y * (1 - y);
}