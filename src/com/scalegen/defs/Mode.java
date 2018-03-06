package com.scalegen.defs;

public enum Mode {
    MAJOR("Major"),
    MINOR("Minor"),
    NATURAL_MINOR("Natural Minor"), HARMONIC_MINOR("Harmonic Minor"),
    AUGMENTED("Augmented"), DIMINISHED("Diminished"),
    HALF_DIMINISHED("Half diminished"),
    PERFECT("Perfect");

    public static final Mode[] FOR_SCALES = { MAJOR, NATURAL_MINOR, HARMONIC_MINOR};
    public static final Mode[] FOR_ARPEGGIOS = { MAJOR, MINOR, AUGMENTED, DIMINISHED, HALF_DIMINISHED};

    private final String s;

    Mode(String r) {
        this.s = r;
    }

    @Override
    public String toString() {
        return s;
    }

    public static String postfix(Mode mode){
        switch(mode){
            case MAJOR:
                return "";
            case MINOR:
            case NATURAL_MINOR:
            case HARMONIC_MINOR:
                return "m";
            case DIMINISHED:
                return "m♭5";
            case AUGMENTED:
                return "+";
            case HALF_DIMINISHED:
                return "m7♭5";
        }
        throw new IllegalArgumentException("function postfix(Mode) can only be used for arpeggios and scales.");
    }
}
