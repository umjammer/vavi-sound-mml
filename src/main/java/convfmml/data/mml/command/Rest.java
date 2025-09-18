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


public abstract class Rest extends NoteRest {

    public Rest(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        super(length, relation);
    }

    @Override
    protected abstract String generateString(Settings settings, SoundModule module);
}
