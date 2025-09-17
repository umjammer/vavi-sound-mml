/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Mml2vgm;


import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Instrument;
import ConvFMML.Settings;


public class Mml2vgmInstrument extends Instrument {

    public Mml2vgmInstrument(int value, MMLCommandRelation relation) {
        super(value, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        return "@" + ((module == SoundModule.SSG) ? "E" : "") + value;
    }
}
