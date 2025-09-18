/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.pmd;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class PMDVolume extends Volume {

    public PMDVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        String sign;
        int newValue;
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        if (volumeSettings.getCommandPMD() == 0) {
            sign = "v";
            double temp;
            if (module == SoundModule.SSG) {
                temp = value * 15.0 / 127.0;
            } else {
                temp = value * 16.0 / 127.0;
            }
            newValue = (int) Math.round(temp);
        } else {
            sign = "V";
            if (module == SoundModule.SSG) {
                double temp = value * 15.0 / 127.0;
                newValue = (int) Math.round(temp);
            } else {
                newValue = value;
            }
        }

        return sign + newValue;
    }
}
