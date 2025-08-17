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

public class SimpleNeuralNetwork {
    public final Matrix weights_ih;
    public final Matrix weights_ho;
    public final Matrix bias_h;
    public final Matrix bias_o;
    private final DoubleUnaryOperator activationFunction = ActivationFunctions.SIGMOID;
    private final double learningRate = 0.01;

    public SimpleNeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes) {
        this.weights_ih = new Matrix(hiddenNodes, inputNodes);
        this.weights_ho = new Matrix(outputNodes, hiddenNodes);
        this.bias_h = new Matrix(hiddenNodes, 1);
        this.bias_o = new Matrix(outputNodes, 1);
    }

    public SimpleNeuralNetwork(Matrix w_ih, Matrix w_ho, Matrix b_h, Matrix b_o) {
        this.weights_ih = w_ih;
        this.weights_ho = w_ho;
        this.bias_h = b_h;
        this.bias_o = b_o;
    }

    public double[] predict(float[] inputArray) {
        Matrix inputs = Matrix.fromArray(inputArray);
        Matrix hidden = Matrix.multiply(weights_ih, inputs);
        hidden.add(bias_h);
        hidden.map(activationFunction);

        Matrix output = Matrix.multiply(weights_ho, hidden);
        output.add(bias_o);
        output.map(activationFunction);

        return output.toArray();
    }

    public void train(float[] inputArray, float[] targetArray) {
        Matrix inputs = Matrix.fromArray(inputArray);
        Matrix hidden = Matrix.multiply(weights_ih, inputs);
        hidden.add(bias_h);
        hidden.map(activationFunction);

        Matrix outputs = Matrix.multiply(weights_ho, hidden);
        outputs.add(bias_o);
        outputs.map(activationFunction);

        Matrix targets = Matrix.fromArray(targetArray);
        Matrix output_errors = Matrix.subtract(targets, outputs);

        Matrix gradients = outputs;
        gradients.map(ActivationFunctions.SIGMOID_DERIVATIVE);
        gradients.multiplyElementWise(output_errors);
        gradients.multiply(learningRate);

        Matrix hidden_T = Matrix.transpose(hidden);
        Matrix delta_weights_ho = Matrix.multiply(gradients, hidden_T);
        weights_ho.add(delta_weights_ho);
        bias_o.add(gradients);

        Matrix who_T = Matrix.transpose(weights_ho);
        Matrix hidden_errors = Matrix.multiply(who_T, output_errors);

        Matrix hidden_gradients = hidden;
        hidden_gradients.map(ActivationFunctions.SIGMOID_DERIVATIVE);
        hidden_gradients.multiplyElementWise(hidden_errors);
        hidden_gradients.multiply(learningRate);

        Matrix inputs_T = Matrix.transpose(inputs);
        Matrix delta_weights_ih = Matrix.multiply(hidden_gradients, inputs_T);
        weights_ih.add(delta_weights_ih);
        bias_h.add(hidden_gradients);
    }
}