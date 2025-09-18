/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.custom;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Pan;
import convfmml.Settings;


public class CustomPan extends Pan {

    public CustomPan(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        String sign;
        int newValue;
        Settings.ControlCommand.Pan panSettings = settings.getControlCommand().getPan();

        if (panSettings.getCommandCustom() == 0) {
            sign = panSettings.getMidiCommandCustom();
            newValue = value;
        } else {
            newValue = -1;
            if (value <= panSettings.getBorderLeft().intValue()) {
                sign = panSettings.getLeftCommandCustom();
            } else if (panSettings.getBorderRight().intValue() <= value) {
                sign = panSettings.getRightCommandCustom();
            } else {
                sign = panSettings.getCenterCommandCustom();
            }
        }

        if (newValue < 0) {
            return sign;
        } else {
            return sign + newValue;
        }
    }
}
