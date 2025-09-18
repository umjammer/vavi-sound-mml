/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.fmp7;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Pan;
import convfmml.Settings;


public class FMP7Pan extends Pan {

    public FMP7Pan(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        String sign;
        int jud;
        int newValue;
        Settings.ControlCommand.Pan panSettings = settings.getControlCommand().getPan();

        if (value == 64) {
            jud = 2;
            newValue = 128;
        } else {
            if (value > 64) {
                jud = 0;
                newValue = (value - 64) * 127 / 63;
            } else {
                jud = 1;
                newValue = (64 - value) * 127 / 64;
            }
        }

        if (panSettings.getCommandFMP7() == 0) {
            sign = "P";
            switch (jud) {
                case 0:
                    newValue = 128 + newValue;
                    break;
                case 1:
                    newValue = 128 - newValue;
                    break;
                default:
                    break;
            }
        } else {
            switch (jud) {
                case 0:
                    sign = "PR";
                    break;
                case 1:
                    sign = "PL";
                    break;
                default:
                    sign = "PC";
                    newValue = -1;
                    break;
            }
        }

        if (newValue < 0) {
            return sign;
        } else {
            return sign + newValue;
        }
    }
}
