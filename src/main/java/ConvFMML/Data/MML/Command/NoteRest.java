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


public abstract class NoteRest extends Command {

    protected final List<Integer> length;

    public List<Integer> getLength() {
        return length;
    }

    protected NoteRest(List<Integer> length, MMLCommandRelation relation) {
        super(relation);
        this.length = length;
    }

    @Override
    protected abstract String GenerateString(Settings settings, SoundModule module);
}
