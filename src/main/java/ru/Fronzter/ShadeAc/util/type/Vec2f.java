package ru.Fronzter.ShadeAc.util.type;
/*
 * ShadeAc
 * Copyright (C) 2025 Fronzter
 *
 * You may copy, modify, and distribute this plugin,
 * but **only with its source code included**.
 * Closed-source distribution or selling without source is prohibited.
 */
import lombok.Getter;

@Getter
public final class Vec2f {

    public static final Vec2f ZERO = new Vec2f(0.0F, 0.0F);

    private final float x;
    private final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f scale(float scalar) {
        return new Vec2f(this.x * scalar, this.y * scalar);
    }

    public float dot(Vec2f other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vec2f add(Vec2f other) {
        return new Vec2f(this.x + other.x, this.y + other.y);
    }

    public Vec2f normalized() {
        float length = (float) Math.sqrt(this.x * this.x + this.y * this.y);
        return length < 1.0E-4F ? ZERO : new Vec2f(this.x / length, this.y / length);
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2f vec2f = (Vec2f) o;
        return Float.compare(vec2f.x, x) == 0 && Float.compare(vec2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = (x != 0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != 0.0f ? Float.floatToIntBits(y) : 0);
        return result;
    }
}