package com.scalegen.generation;

import com.scalegen.defs.GenerationLength;
import com.scalegen.defs.Location;
import com.scalegen.defs.Mode;
import com.scalegen.defs.Note;

import java.io.EOFException;

public class ArpeggioGenerator implements Generator {

    @Override
    public GenerationResult generate(GenerationLength length, Note root, Mode mode, Location rootPosition) {
        return genArpeggio(getNotes(root, mode), length, rootPosition, mode);
    }

    private GenerationResult genArpeggio(Note[] notes, GenerationLength length, Location rootPosition, Mode mode) {
        Location[] locations = new Location[length == GenerationLength.ONE_OCTAVE ? notes.length : notes.length * 2 - 1];
        boolean incomplete = false;
        Location lastPlaced;
        Location firstPlacedOnCurrentString = rootPosition;
        try {
            int i;
            for (i = 0; i < notes.length; ++i) {
                locations[i] = (lastPlaced = Location.findFirstOptimized(notes[i], firstPlacedOnCurrentString, PLACING_TOLERANCE));
                if (lastPlaced.string != firstPlacedOnCurrentString.string)
                    firstPlacedOnCurrentString = lastPlaced;
            }
            --i;
            firstPlacedOnCurrentString = locations[getFirstOnHighestString(locations)];
            if (length == GenerationLength.TWO_OCTAVES)
                for (int k = 0; i < locations.length && k < notes.length; ++k, ++i) {
                    locations[i] = (lastPlaced = Location.findFirstOptimized(notes[k], firstPlacedOnCurrentString, PLACING_TOLERANCE));
                    if (lastPlaced.string != firstPlacedOnCurrentString.string)
                        firstPlacedOnCurrentString = lastPlaced;
                }
        } catch (EOFException e) {
            incomplete = true;
        }

        return new GenerationResult(String.format("%s %s arpeggio", rootPosition.note.toString(), mode.toString().toLowerCase()), locations, incomplete);
    }

    private static int getFirstOnHighestString(Location[] locations) {
        Location sentinel = null;
        for(int i = locations.length - 1; i >= 0; --i){
            if(i > 0) sentinel = locations[i - 1];
            if(sentinel != null && locations[i] != null && sentinel.string != locations[i].string) return i;
        }
        return 0;
    }

    private static Note[] getNotes(Note key, Mode type) {
        switch (type) {
            case MAJOR:
                return new Note[]{key, key.interval(3, Mode.MAJOR), key.interval(5, Mode.PERFECT), key};
            case MINOR:
                return new Note[]{key, key.interval(3, Mode.MINOR), key.interval(5, Mode.PERFECT), key};
            case AUGMENTED:
                return new Note[]{key, key.interval(3, Mode.MAJOR), key.interval(5, Mode.AUGMENTED), key};
            case DIMINISHED:
                return new Note[]{key, key.interval(3, Mode.MINOR), key.interval(5, Mode.DIMINISHED), key};
            case HALF_DIMINISHED:
                return new Note[]{key, key.interval(3, Mode.MINOR), key.interval(5, Mode.PERFECT), key.interval(7, Mode.MINOR), key};
        }
        throw new IllegalArgumentException();
    }


    private class ResWrapper {
        private boolean incomplete;
        private Location[] locations;

        private ResWrapper(boolean incomplete, Location[] locations) {
            this.incomplete = incomplete;
            this.locations = locations;
        }
    }
}

