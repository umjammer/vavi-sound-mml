/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Custom;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Instrument;
import ConvFMML.Settings;


public class CustomInstrument extends Instrument {

    public CustomInstrument(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        Settings.ControlCommand.ProgramChange pcSettings = settings.getControlCommand().getProgramChange();

        return pcSettings.getCommandCustom() + value;
    }
}
