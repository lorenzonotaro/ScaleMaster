package com.scalegen.generation;

import com.scalegen.defs.Location;
import com.scalegen.defs.Note;

public class GenerationResult {
    private final boolean incomplete;
    private final String description;
    private final Location locations[];

    GenerationResult(String description, Location[] locations, boolean incomplete) {
        this.description = description;
        this.locations = locations;
        this.incomplete = incomplete;
    }

    public String getDescription() {
        return description;
    }

    public Location[] getLocations() {
        return locations;
    }

    public boolean isIncomplete() {
        return incomplete;
    }
}
