/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Settings;


public abstract class Command {

    private MMLCommandRelation commandRelation;

    public MMLCommandRelation getCommandRelation() {
        return commandRelation;
    }

    public void setCommandRelation(MMLCommandRelation commandRelation) {
        this.commandRelation = commandRelation;
    }

    protected Command(MMLCommandRelation relation) {
        this.commandRelation = relation;
    }

    public String ToString(Settings settings, SoundModule module) {
        return GenerateString(settings, module);
    }

    protected abstract String GenerateString(Settings settings, SoundModule module);
}
