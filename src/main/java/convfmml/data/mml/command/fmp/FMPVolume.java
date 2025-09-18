/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.fmp;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class FMPVolume extends Volume {

    public FMPVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        double temp = value * 15.0 / 127.0;
        return "v" + (int) Math.round(temp);
    }
}
