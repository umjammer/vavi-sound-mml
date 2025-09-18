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
import convfmml.data.mml.command.custom.CustomInstrument;
import convfmml.data.mml.command.custom.CustomPan;
import convfmml.data.mml.command.custom.CustomTempo;
import convfmml.data.mml.command.custom.CustomVolume;


public class CustomMusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new CustomInstrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new CustomPan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new CustomTempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new CustomVolume(v.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }
}
