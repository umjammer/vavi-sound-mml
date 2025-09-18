/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.custom;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class CustomVolume extends Volume {

    public CustomVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        int newValue;
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        if (volumeSettings.getRangeCustom().intValue() != 0) {
            double temp = value * volumeSettings.getRangeCustom().doubleValue() / 127.0;
            newValue = (int) Math.round(temp);
        } else {
            newValue = value;
        }

        return volumeSettings.getCommandCustom() + newValue;
    }
}
