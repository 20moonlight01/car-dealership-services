package ru.glebova.valueobjects;

public record Price(float value) {
    public boolean isLessOrEqual(Price other) { return this.value <= other.value(); }
    public boolean isGreaterOrEqual(Price other) { return this.value >= other.value(); }
    public Price add(Price other) { return new Price(this.value + other.value()); }
    public Price subtract(Price other) { return new Price(this.value - other.value()); }
}