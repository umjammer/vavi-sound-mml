/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Modifier;

import ConvFMML.Common.SoundModule;
import ConvFMML.Data.Intermediate.Event.ChangeEvent;
import ConvFMML.Data.Intermediate.Position;
import ConvFMML.Data.MML.Command.ControlCommand;
import ConvFMML.Settings;


public class ChangeEventSet {

    private ChangeEvent intermediate;
    private ControlCommand mml;

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

    public String ToString(Settings settings, SoundModule module) {
        return mml.ToString(settings, module);
    }
}
