package com.scalegen.defs;

/** Enumeration representing a note.
 *  This class also contains definitions that are used
 *  throughout the program, regarding the fretboard,
 *  intervals, distances and scales.
 * */
public enum Note {
    /** The twelve notes in the chromatic scale.
     * */
    C("C"), C_SHARP("C#"),
    D("D"), D_SHARP("D#"),
    E("E"),
    F("F"), F_SHARP("F#"),
    G("G"), G_SHARP("G#"),
    A("A"), A_SHARP("A#"),
    B("B");

    /** Matrix representing the first <code>MAX_FRETS</code> frets
     *  in a guitar fretboard, standard tuning. The first element of
     *  each row is the note played by the open string.
     * */
    public static final Note[][] FRETBOARD = {
            {}, // leave first sub-array empty so we can actually use 1 as index for the first string
            {E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G},
            {B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, C, C_SHARP, D},
            {G, G_SHARP, A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A},
            {D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F},
            {A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, C},
            {E, F, F_SHARP, G, G_SHARP, A, A_SHARP, B, C, C_SHARP, D, D_SHARP, E, F, F_SHARP, G},
    };

    /** The maximum numbers of frets used in the generation. */
    public static final int MAX_FRETS = 15;

    /** String representation of the note. */
    private final String s;

    Note(String s) {
        this.s = s;
    }

    /**
     * @return the string representation of this note.*/
    @Override
    public String toString() {
        return s;
    }

    /** Returns the note corresponding to the indicated degree, from this
     *  note. The mode element must be applicable to the given degree (e.g.
     *  the value <code>Mode.MAJOR</code> can't be combined with a degree value
     *  of 5, because fifths are perfect intervals).
     * */
    public Note interval(int degree, Mode mode){
        if(degree < 1 || degree > 8)
            throw new IndexOutOfBoundsException("degree = " + degree);
        if(mode == Mode.HARMONIC_MINOR || mode == Mode.HALF_DIMINISHED)
            throw new IllegalArgumentException("mode = " + mode.toString());
        switch(degree){
            case 1:
            case 8:
                return this;
            case 2:
                return plus(2 + modifierAsOfMajor(mode));
            case 3:
                return plus(4 + modifierAsOfMajor(mode));
            case 4:
                return plus(5 + modifierAsOfPerfect(mode));
            case 5:
                return plus(7 + modifierAsOfPerfect(mode));
            case 6:
                return plus(9 + modifierAsOfMajor(mode));
            case 7:
                return plus(11 + modifierAsOfMajor(mode));
        }
        throw new IllegalArgumentException();
    }

    /** Returns the distance between the given mode and the perfect mode.
     *  If the given mode is not applicable to a perfect interval,
     *  this method throws an IllegalArgumentException.
     *  Specifying a note is not necessary for this method because it
     *  only returns a distance modifier.*/
    private int modifierAsOfPerfect(Mode mode) {
        switch(mode){
            case AUGMENTED:
                return 1;
            case DIMINISHED:
                return -1;
            case PERFECT:
                return 0;
        }
        throw new IllegalArgumentException("wrong mode for perfect interval: " + mode.toString());
    }

    /** Returns the distance between the given mode and the major mode.
     *  If the given mode is not applicable to an imperfect interval,
     *  this method throws an IllegalArgumentException.
     *  Specifying a note is not necessary for this method because it
     *  only returns a distance modifier.*/
    private static int modifierAsOfMajor(Mode mode) {
        switch(mode){
            case MAJOR:
                return 0;
            case MINOR:
                return -1;
            case DIMINISHED:
                return -2;
            case AUGMENTED:
                return 1;
        }
        throw new IllegalArgumentException("wrong mode for imperfect interval: " + mode.toString());
    }

    /** Returns the sum between this note and <code>p</code> semitones. */
    public Note plus(int p){
        return Note.values()[Math.abs(this.ordinal() + p) % Note.values().length];
    }

    /** Returns the scale for the given root in the given mode. */
    public static Note[] scale(Note root, Mode mode){
        switch(mode){
            case MAJOR:
                return majorScale(root);
            case MINOR:
            case NATURAL_MINOR:
                return naturalMinorScale(root);
            case HARMONIC_MINOR:
                return harmonicMinorScale(root);
        }
        throw new IllegalArgumentException("wrong mode for a scale: " + mode);
    }

    /** Returns the major scale for the given root. */
    public static Note[] majorScale(Note root){
        return new Note[]{
                root,
                root.interval(2, Mode.MAJOR),
                root.interval(3, Mode.MAJOR),
                root.interval(4, Mode.PERFECT),
                root.interval(5, Mode.PERFECT),
                root.interval(6, Mode.MAJOR),
                root.interval(7, Mode.MAJOR),
                root
        };
    }

    /** Returns the natural minor scale for the given root. */
    public static Note[] naturalMinorScale(Note root){
        return new Note[]{
                root,
                root.interval(2, Mode.MAJOR),
                root.interval(3, Mode.MINOR),
                root.interval(4, Mode.PERFECT),
                root.interval(5, Mode.PERFECT),
                root.interval(6, Mode.MINOR),
                root.interval(7, Mode.MINOR),
                root
        };
    }

    /** Returns the harmonic minor scale for the given root. */
    private static Note[] harmonicMinorScale(Note root) {
        return new Note[]{
                root,
                root.interval(2, Mode.MAJOR),
                root.interval(3, Mode.MINOR),
                root.interval(4, Mode.PERFECT),
                root.interval(5, Mode.PERFECT),
                root.interval(6, Mode.MINOR),
                root.interval(7, Mode.MAJOR),
                root
        };
    }

    /** Returns the distance between two notes, in semitones. */
    public static int distance(Note a, Note b){
        if(a.ordinal() > b.ordinal())
            return 12 - (a.ordinal() - b.ordinal());
        return b.ordinal() - a.ordinal();
    }

}
