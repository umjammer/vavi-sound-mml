/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Settings;


public abstract class Note extends NoteRest {

    private final int octave;
    protected String name;

    public int getOctave() {
        return octave;
    }

    public String getName() {
        return name;
    }

    public Note(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        super(length, relation);

        this.octave = octave;
        this.name = name;
    }

    @Override
    protected abstract String GenerateString(Settings settings, SoundModule module);
}
