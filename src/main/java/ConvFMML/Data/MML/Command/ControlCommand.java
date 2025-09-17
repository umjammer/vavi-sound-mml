/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Settings;


public abstract class ControlCommand extends Command {

    protected int value;

    public int getValue() {
        return value;
    }

    protected ControlCommand(int value, MMLCommandRelation relation) {
        super(relation);
        this.value = value;
    }

    @Override
    protected abstract String GenerateString(Settings settings, SoundModule module);
}
