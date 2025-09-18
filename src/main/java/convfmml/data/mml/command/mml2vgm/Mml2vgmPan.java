/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.mml2vgm;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Pan;
import convfmml.Settings;


public class Mml2vgmPan extends Pan {

    public Mml2vgmPan(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
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
