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
import convfmml.data.mml.command.fmp7.FMP7Instrument;
import convfmml.data.mml.command.fmp7.FMP7Pan;
import convfmml.data.mml.command.fmp7.FMP7Tempo;
import convfmml.data.mml.command.fmp7.FMP7Volume;


public class FMP7MusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new FMP7Instrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new FMP7Pan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new FMP7Tempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new FMP7Volume(v.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }
}
