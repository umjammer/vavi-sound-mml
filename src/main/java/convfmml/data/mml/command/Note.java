/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command;

import java.util.EnumSet;
import java.util.List;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.Settings;


public abstract class Note extends NoteRest {

    private final int octave;
    protected String name;

    public int getOctave() {
        return octave;
    }

    public String getName() {
        return name;
    }

    public Note(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        super(length, relation);

        this.octave = octave;
        this.name = name;
    }

    @Override
    protected abstract String generateString(Settings settings, SoundModule module);
}
