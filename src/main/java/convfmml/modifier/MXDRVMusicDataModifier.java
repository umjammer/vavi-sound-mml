/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.modifier;

import java.util.EnumSet;

import convfmml.Common.MMLCommandRelation;
import convfmml.data.intermediate.event.Instrument;
import convfmml.data.intermediate.event.Pan;
import convfmml.data.intermediate.event.Tempo;
import convfmml.data.intermediate.event.Volume;
import convfmml.data.mml.command.mxdrv.MXDRVInstrument;
import convfmml.data.mml.command.mxdrv.MXDRVPan;
import convfmml.data.mml.command.mxdrv.MXDRVTempo;
import convfmml.data.mml.command.mxdrv.MXDRVVolume;


public class MXDRVMusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new MXDRVInstrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new MXDRVPan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new MXDRVTempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new MXDRVVolume(v.getValue(),EnumSet.of(MMLCommandRelation.Clear)));
    }
}
