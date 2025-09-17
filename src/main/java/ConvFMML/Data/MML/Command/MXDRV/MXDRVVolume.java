/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.MXDRV;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Settings;


public class MXDRVVolume extends Volume {

    public MXDRVVolume(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
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
