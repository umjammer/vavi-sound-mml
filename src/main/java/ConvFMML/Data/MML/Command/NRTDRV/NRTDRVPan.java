/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.NRTDRV;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Settings;


public class NRTDRVPan extends Pan {

    public NRTDRVPan(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        String sign;
        Settings.ControlCommand.Pan panSettings = settings.getControlCommand().getPan();

        if (value <= panSettings.getBorderLeft().intValue()) {
            sign = "P1";
        } else if (panSettings.getBorderRight().intValue() <= value) {
            sign = "P2";
        } else {
            sign = "P3";
        }

        return sign;
    }
}
