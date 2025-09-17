/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Mml2vgm;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Settings;


public class Mml2vgmVolume extends Volume {

    public Mml2vgmVolume(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
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
