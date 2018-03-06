package com.scalegen.generation;

import com.scalegen.defs.GenerationLength;
import com.scalegen.defs.Location;
import com.scalegen.defs.Mode;
import com.scalegen.defs.Note;

import java.io.EOFException;

public class ScaleGenerator implements Generator {

    @Override
    public GenerationResult generate(GenerationLength length, Note root, Mode mode, Location rootPosition) {
        Location[] locations = new Location[length == GenerationLength.ONE_OCTAVE ? 8 : 15];
        Note[] scale = Note.scale(root,mode);

        StringBuilder s = new StringBuilder("\n");
        for(Note t : scale)
             s.append(t).append(" ");
        System.out.println(s.toString());

        boolean error;

        if(length == GenerationLength.ONE_OCTAVE)
            error = gen1Octave(locations, scale, rootPosition);
        else
            error = gen2Octaves(locations, scale, rootPosition);

        return new GenerationResult(String.format("%s %s scale", root.toString(), mode.toString().toLowerCase()),locations,error);
    }

    private boolean gen2Octaves(Location[] locations, Note[] scale, Location rootPosition) {
        if (gen1Octave(locations, scale, rootPosition)) return true;
        //rootPosition = locations[8];
        Location lastPlaced;
        Location firstPlacedOnCurrentString = locations[getFirstOnHighestString(locations)];
        try {
            for(int degree = 0, index = 7; degree < scale.length; ++degree, ++index){
                locations[index] = (lastPlaced = Location.findFirstOptimized(scale[degree],firstPlacedOnCurrentString,PLACING_TOLERANCE));
                if(lastPlaced.string != firstPlacedOnCurrentString.string)
                    firstPlacedOnCurrentString = lastPlaced;
            }
        } catch (EOFException e) {
            return true;
        }
        return false;
    }

    private int getFirstOnHighestString(Location[] locations) {
        Location sentinel = null;
        for(int i = locations.length - 1; i >= 0; --i){
            if(i > 0) sentinel = locations[i - 1];
            if(sentinel != null && locations[i] != null && sentinel.string != locations[i].string) return i;
        }
        return 0;
    }

    private boolean gen1Octave(Location[] locations, Note[] scale, Location rootPosition) {
        Location lastPlaced;
        Location firstPlacedOnCurrentString = rootPosition;
        try {
            for(int degree = 0; degree < scale.length; ++degree){
                locations[degree] = (lastPlaced = Location.findFirstOptimized(scale[degree],firstPlacedOnCurrentString,PLACING_TOLERANCE));
                if(lastPlaced.string != firstPlacedOnCurrentString.string)
                    firstPlacedOnCurrentString = lastPlaced;
            }
        } catch (EOFException e) {
            return true;
        }
        return false;
    }


}
