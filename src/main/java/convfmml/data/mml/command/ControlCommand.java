/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.Settings;


public abstract class ControlCommand extends Command {

    protected int value;

    public int getValue() {
        return value;
    }

    protected ControlCommand(int value, EnumSet<MMLCommandRelation> relation) {
        super(relation);
        this.value = value;
    }

    @Override
    protected abstract String generateString(Settings settings, SoundModule module);
}
