/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Settings;


public abstract class Pan extends ControlCommand {

    protected Pan(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected abstract String GenerateString(Settings settings, SoundModule module);
}
