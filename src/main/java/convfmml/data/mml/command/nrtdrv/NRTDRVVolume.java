/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.nrtdrv;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class NRTDRVVolume extends Volume {

    public NRTDRVVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        String sign;
        int newValue;
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        if (volumeSettings.getCommandNRTDRV() == 0) {
            sign = "v";
            double temp;
            if (module == SoundModule.FM) {
                temp = value * volumeSettings.getVStep().doubleValue() / 127.0;
            } else {
                temp = value * 15.0 / 127.0;
            }
            newValue = (int) Math.round(temp);
        } else {
            sign = "V";
            if (module == SoundModule.FM) {
                newValue = value;
            } else {
                double temp = value * 15.0 / 127.0;
                newValue = (int) Math.round(temp);
            }
        }

        return sign + newValue;
    }
}
