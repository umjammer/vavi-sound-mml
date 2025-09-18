/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.fmp7;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Tempo;
import convfmml.Settings;


public class FMP7Tempo extends Tempo {

    public FMP7Tempo(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        return "T" + value;
    }
}
