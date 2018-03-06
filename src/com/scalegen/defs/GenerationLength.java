package com.scalegen.defs;

public enum GenerationLength {
    ONE_OCTAVE("One octave"), TWO_OCTAVES("Two octaves");

    private final String s;

    GenerationLength(String s) {
        this.s = s;
    }

    @Override
    public String toString(){
        return s;
    }
}
