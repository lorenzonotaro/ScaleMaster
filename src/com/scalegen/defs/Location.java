package com.scalegen.defs;

import com.sun.istack.internal.NotNull;

import java.io.EOFException;
import java.util.ArrayList;

public class Location {
    public final int string;
    public final int fret;

    public Location(int string, int fret, Note note) {
        this.string = string;
        this.fret = fret;
        this.note = note;
    }

    public Location(int string, int fret) {
        this(string, fret, Note.FRETBOARD[string][fret]);
    }

    public final Note note;

    @Override
    public String toString(){
        if(fret == 0) return String.format("open %s string", ordinal(string));
        return String.format("%s fret of the %s string", ordinal(fret), ordinal(string));
    }

    public static String ordinal(int i){
        String postfix = "th";
        switch(i){
            case 1:
                postfix = "st";
                break;
            case 2:
                postfix = "nd";
                break;
            case 3:
                postfix = "rd";
                break;
        }
        return i + postfix;
    }

    public static Location[] getAll(Note note){
        ArrayList<Location> locations = new ArrayList<>();
        for(int string = Note.FRETBOARD.length - 1; string > 0; --string)
            for(int fret = 0; fret < Note.FRETBOARD[string].length; ++fret)
                if(Note.FRETBOARD[string][fret] == note || note == null)
                    locations.add(new Location(string,fret));
        return locations.toArray(new Location[locations.size()]);
    }

    public static Location findFirstOptimized(Note note, Location from, int tolerance) throws EOFException {
        if(note == from.note) return from;
        int distance = Note.distance(from.note, note);
        Location loc = new Location(from.string, from.fret + distance, note);
        while((distance > tolerance || loc.fret > Note.MAX_FRETS)){
            if(loc.string == 1 && loc.fret >= Note.MAX_FRETS) throw new EOFException();
            try {
                loc = scaleDown(loc);
            }catch(NullPointerException e){
                break;
            }
            distance = Math.abs(loc.fret - from.fret);
        }
        return loc;
    }

    public static Location scaleDown(Location loc){
        int fretsToNextString = loc.string == 3 ? 4 : 5;
        if(loc.string == 1 || loc.fret < fretsToNextString) throw new NullPointerException();
        return new Location(loc.string - 1, loc.fret - fretsToNextString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (string != location.string) return false;
        if (fret != location.fret) return false;
        return note == location.note;
    }

    @Override
    public int hashCode() {
        int result = string;
        result = 31 * result + fret;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }
}
