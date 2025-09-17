/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Modifier;

import java.util.LinkedList;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.Intermediate.Event.Instrument;
import ConvFMML.Data.Intermediate.Event.Pan;
import ConvFMML.Data.Intermediate.Event.Tempo;
import ConvFMML.Data.Intermediate.Event.Volume;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Instrument;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Pan;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Tempo;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Volume;
import ConvFMML.Settings;


public class MUCOM88MusicDataModifier extends MusicDataModifier {

    @Override
    protected ChangeEventSet CreateInstrumentSet(Instrument i) {
        return new ChangeEventSet(i, new MUCOM88Instrument((i.getValue() + 1), MMLCommandRelation.Clear));
    }

    @Override
    protected ChangeEventSet CreatePanSet(Pan p) {
        return new ChangeEventSet(p, new MUCOM88Pan(p.getValue(), MMLCommandRelation.Clear));
    }

    @Override
    protected LinkedList<Pan> ClonePanList(LinkedList<Pan> newList, LinkedList<Pan> srcList, Settings settings, SoundModule module) {
        return null;
    }

    @Override
    protected TempoSet CreateTempoSet(Tempo t) {
        return new TempoSet(t, new MUCOM88Tempo(t.getValue(), MMLCommandRelation.Clear));
    }

    @Override
    protected ChangeEventSet CreateVolumeSet(Volume v) {
        return new ChangeEventSet(v, new MUCOM88Volume(v.getValue(), MMLCommandRelation.Clear));
    }
}

