/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.modifier;

import convfmml.Common.SoundModule;
import convfmml.data.intermediate.event.ChangeEvent;
import convfmml.data.intermediate.Position;
import convfmml.data.mml.command.ControlCommand;
import convfmml.Settings;


public class ChangeEventSet {

    private final ChangeEvent intermediate;
    private final ControlCommand mml;

    public ChangeEvent getData() {
        return intermediate;
    }

    public Position getPosition() {
        return intermediate.getPosition();
    }

    public ChangeEventSet(ChangeEvent intermediate, ControlCommand mml) {
        this.intermediate = intermediate;
        this.mml = mml;
    }

    public String toString(Settings settings, SoundModule module) {
        return mml.toString(settings, module);
    }
}
