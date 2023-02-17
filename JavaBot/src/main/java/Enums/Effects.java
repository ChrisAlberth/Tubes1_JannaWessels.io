package Enums;

public enum Effects {
    IRFAN(0),
    AFTERBURNER(1),
    ASTEROIDFIELD(2),
    GASCLOUD(4),
    SUPERFOOD(8),
    SHIELD(16);

    public final Integer value;

    Effects(Integer value) {
        this.value = value;
    }

    public static Effects valueOf(Integer value) {
        for (Effects effects : Effects.values()) {
            if (effects.value == value) return effects;
        }

        throw new IllegalArgumentException("Value not found");
    }
}
