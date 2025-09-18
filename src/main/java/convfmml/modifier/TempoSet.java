/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.modifier;

import convfmml.Common.SoundModule;
import convfmml.Settings;


public class TempoSet {

    private final convfmml.data.intermediate.event.Tempo intermediate;
    private convfmml.data.mml.command.Tempo mml;

    public convfmml.data.intermediate.event.Tempo getData() {
        return intermediate;
    }

    public convfmml.data.intermediate.Position getPosition() {
        return intermediate.getPosition();
    }

    public TempoSet(convfmml.data.intermediate.event.Tempo intermediate, convfmml.data.mml.command.Tempo mml) {
        this.intermediate = intermediate;
        this.mml = mml;
    }

    public String toString(Settings settings, SoundModule module) {
        return mml.toString(settings, module);
    }
}
