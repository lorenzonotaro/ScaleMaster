package com.scalegen.generation;

import com.scalegen.defs.*;

public interface Generator {

    int PLACING_TOLERANCE = 3;

    GenerationResult generate(GenerationLength length, Note root, Mode mode, Location rootPosition);
}
