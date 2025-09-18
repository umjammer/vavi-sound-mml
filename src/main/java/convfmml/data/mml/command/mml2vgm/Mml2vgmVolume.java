/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.mml2vgm;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Volume;
import convfmml.Settings;


public class Mml2vgmVolume extends Volume {

    public Mml2vgmVolume(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        Settings.ControlCommand.Volume volumeSettings = settings.getControlCommand().getVolume();

        int newValue;
        if (module == SoundModule.SSG) {
            double temp = value * 15.0 / 127.0;
            newValue = (int) Math.round(temp);
        } else {
            newValue = value;
        }

        return "v" + newValue;
    }
}
