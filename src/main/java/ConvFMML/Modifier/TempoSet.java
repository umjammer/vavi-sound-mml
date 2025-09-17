/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Modifier;

import ConvFMML.Common.SoundModule;
import ConvFMML.Settings;


public class TempoSet {

    private ConvFMML.Data.Intermediate.Event.Tempo intermediate;
    private ConvFMML.Data.MML.Command.Tempo mml;

    public ConvFMML.Data.Intermediate.Event.Tempo getData() {
        return intermediate;
    }

    public ConvFMML.Data.Intermediate.Position getPosition() {
        return intermediate.getPosition();
    }

    public TempoSet(ConvFMML.Data.Intermediate.Event.Tempo intermediate, ConvFMML.Data.MML.Command.Tempo mml) {
        this.intermediate = intermediate;
        this.mml = mml;
    }

    public String ToString(Settings settings, SoundModule module) {
        return mml.ToString(settings, module);
    }
}
