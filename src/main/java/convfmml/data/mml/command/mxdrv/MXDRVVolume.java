/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.mxdrv;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class MXDRVVolume extends Volume {

    public MXDRVVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        String sign;
        int newValue;
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        if (volumeSettings.getCommandMXDRV() == 0) {
            sign = "v";
            double temp = value * 15.0 / 127.0;
            newValue = (int) Math.round(temp);
        } else {
            sign = "@v";
            newValue = value;
        }

        return sign + newValue;
    }
}
