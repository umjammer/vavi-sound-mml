/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.FMP;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Settings;


public class FMPTempo extends Tempo {

    public FMPTempo(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        return "T" + value;
    }
}
