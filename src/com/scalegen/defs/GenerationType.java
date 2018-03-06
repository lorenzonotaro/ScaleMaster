package com.scalegen.defs;

public enum GenerationType {
    SCALE("Scale"), SIMPLE_TRIAD_ARPEGGIO("Simple triad arpeggio");
    private final String s;

    GenerationType(String r) {
        this.s = r;
    }

    @Override
    public String toString() {
        return s;
    }
}
