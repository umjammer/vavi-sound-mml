/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.modifier;


import java.util.EnumSet;
import java.util.LinkedList;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.intermediate.event.Instrument;
import convfmml.data.intermediate.event.Pan;
import convfmml.data.intermediate.event.Tempo;
import convfmml.data.intermediate.event.Volume;
import convfmml.data.mml.command.fmp.FMPInstrument;
import convfmml.data.mml.command.fmp.FMPPan;
import convfmml.data.mml.command.fmp.FMPTempo;
import convfmml.data.mml.command.fmp.FMPVolume;
import convfmml.Settings;


public class FMPMusicDataModifier extends MusicDataModifier {

    public static LinkedList<Pan> clonePanList(LinkedList<Pan> newList, LinkedList<Pan> srcList, Settings settings, SoundModule module) {
        if (settings.getMmlExpression().getExtensionFMP() == 2) {
            return MusicDataModifier.clonePanList(newList, srcList, settings, module);
        } else {
            return newList;
        }
    }

    @Override
    protected ChangeEventSet createInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new FMPInstrument((i.getValue() + 1), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createPanSet(Pan p) {
        return new ChangeEventSet(p, new FMPPan(p.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected TempoSet createTempoSet(Tempo t) {
        return new TempoSet(t, new FMPTempo(t.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }

    @Override
    protected ChangeEventSet createVolumeSet(Volume v) {
        return new ChangeEventSet(v, new FMPVolume(v.getValue(), EnumSet.of(MMLCommandRelation.Clear)));
    }
}
