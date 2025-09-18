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


public abstract class Command {

    private EnumSet<MMLCommandRelation> commandRelation;

    public EnumSet<MMLCommandRelation> getCommandRelation() {
        return commandRelation;
    }

    public void setCommandRelation(EnumSet<MMLCommandRelation> commandRelation) {
        this.commandRelation = commandRelation;
    }

    protected Command(EnumSet<MMLCommandRelation> relation) {
        this.commandRelation = relation;
    }

    public String toString(Settings settings, SoundModule module) {
        return generateString(settings, module);
    }

    protected abstract String generateString(Settings settings, SoundModule module);
}
