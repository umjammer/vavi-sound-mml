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
import convfmml.data.mml.command.nrtdrv.NRTDRVInstrument;
import convfmml.data.mml.command.nrtdrv.NRTDRVPan;
import convfmml.data.mml.command.nrtdrv.NRTDRVTempo;
import convfmml.data.mml.command.nrtdrv.NRTDRVVolume;


public class NRTDRVMusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new NRTDRVInstrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new NRTDRVPan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new NRTDRVTempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new NRTDRVVolume(v.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }
}
