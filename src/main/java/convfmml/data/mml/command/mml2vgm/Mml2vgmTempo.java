/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.mml2vgm;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Tempo;
import convfmml.Settings;


public class Mml2vgmTempo extends Tempo {

    public Mml2vgmTempo(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        return "T" + value;
    }
}
