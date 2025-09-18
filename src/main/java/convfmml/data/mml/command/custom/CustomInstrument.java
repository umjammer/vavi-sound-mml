/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.custom;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Instrument;
import convfmml.Settings;


public class CustomInstrument extends Instrument {

    public CustomInstrument(int value, EnumSet<MMLCommandRelation> relation) {
        super(value, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        Settings.ControlCommand.ProgramChange pcSettings = settings.getControlCommand().getProgramChange();

        return pcSettings.getCommandCustom() + value;
    }
}
