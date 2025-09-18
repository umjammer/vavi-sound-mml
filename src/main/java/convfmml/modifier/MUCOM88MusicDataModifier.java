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
import convfmml.data.mml.command.mucom88.MUCOM88Instrument;
import convfmml.data.mml.command.mucom88.MUCOM88Pan;
import convfmml.data.mml.command.mucom88.MUCOM88Tempo;
import convfmml.data.mml.command.mucom88.MUCOM88Volume;


public class MUCOM88MusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new MUCOM88Instrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new MUCOM88Pan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new MUCOM88Tempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new MUCOM88Volume(v.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }
}
