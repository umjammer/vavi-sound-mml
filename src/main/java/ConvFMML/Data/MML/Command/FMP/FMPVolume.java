/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.FMP;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Settings;


public class FMPVolume extends Volume {

    public FMPVolume(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        double temp = value * 15.0 / 127.0;
        return "v" + (int) Math.round(temp);
    }
}
