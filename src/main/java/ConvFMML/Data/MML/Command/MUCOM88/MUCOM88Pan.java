/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.MUCOM88;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Settings;


public class MUCOM88Pan extends Pan {

    public MUCOM88Pan(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        String sign;
        Settings.ControlCommand.Pan panSettings = settings.getControlCommand().getPan();

        if (value <= panSettings.getBorderLeft().intValue()) {
            sign = "p2";
        } else if (panSettings.getBorderRight().intValue() <= value) {
            sign = "p1";
        } else {
            sign = "p3";
        }

        return sign;
    }
}
