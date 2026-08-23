package ru.glebova.valueobjects;

public record Power(float value) {
    public boolean isLessOrEqual(Power other) { return this.value <= other.value(); }
    public boolean isGreaterOrEqual(Power other) { return this.value >= other.value(); }
}
