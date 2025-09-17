/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Converter;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.MMLStyle;
import ConvFMML.Data.MML.Command.Custom.CustomInstrument;
import ConvFMML.Data.MML.Command.Custom.CustomNote;
import ConvFMML.Data.MML.Command.Custom.CustomPan;
import ConvFMML.Data.MML.Command.Custom.CustomRest;
import ConvFMML.Data.MML.Command.Custom.CustomTempo;
import ConvFMML.Data.MML.Command.Custom.CustomVolume;
import ConvFMML.Data.MML.Command.Instrument;
import ConvFMML.Data.MML.Command.Length;
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;


public class IntermediateToCustomMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo CreateTempoInstance(int value, MMLCommandRelation relation) {
        return new CustomTempo(value, relation);
    }

    @Override
    protected Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation) {
        return new CustomInstrument((value + 1), relation);
    }

    @Override
    protected Volume CreateVolumeInstance(int value, MMLCommandRelation relation) {
        return new CustomVolume(value, relation);
    }

    @Override
    protected Pan CreatePanInstance(int value, MMLCommandRelation relation) {
        return new CustomPan(value, relation);
    }

    @Override
    protected Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        return new CustomNote(octave, name, length, relation);
    }

    @Override
    protected Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation) {
        return new CustomRest(length, relation);
    }

    @Override
    protected Length CreateLengthInstance(int value, MMLCommandRelation relation) {
        return null;
    }

    @Override
    protected MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new ConvFMML.Data.MML.MML(partList, title, countsPerWholeNote, MMLStyle.Custom);
    }
}
