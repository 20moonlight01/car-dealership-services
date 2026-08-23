package ru.glebova.valueobjects;

public record Volume(float value) {
    public boolean isLessOrEqual(Volume other) { return this.value <= other.value(); }
    public boolean isGreaterOrEqual(Volume other) { return this.value >= other.value(); }
}
