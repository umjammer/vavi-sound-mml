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


public abstract class Rest extends NoteRest {

    public Rest(List<Integer> length, MMLCommandRelation relation) {
        super(length, relation);
    }

    @Override
    protected abstract String GenerateString(Settings settings, SoundModule module);
}
