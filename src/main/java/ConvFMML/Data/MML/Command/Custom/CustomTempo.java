/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Custom;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Settings;


public class CustomTempo extends Tempo {

    public CustomTempo(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        Settings.ControlCommand.Tempo tempoSettings = settings.getControlCommand().getTempo();

        return tempoSettings.getCommandCustom() + value;
    }
}
