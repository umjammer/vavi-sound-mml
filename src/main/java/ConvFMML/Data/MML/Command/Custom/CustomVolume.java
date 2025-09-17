/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Custom;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Settings;


public class CustomVolume extends Volume {

    public CustomVolume(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        int newValue;
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        if (volumeSettings.getRangeCustom().intValue() != 0) {
            double temp = value * (double) volumeSettings.getRangeCustom() / 127.0;
            newValue = (int) Math.round(temp);
        } else {
            newValue = value;
        }

        return volumeSettings.getCommandCustom() + newValue;
    }
}
