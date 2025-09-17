/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Converter;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.MMLStyle;
import ConvFMML.Data.MML.Command.Instrument;
import ConvFMML.Data.MML.Command.Length;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Instrument;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Length;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Note;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Pan;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Rest;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Tempo;
import ConvFMML.Data.MML.Command.MUCOM88.MUCOM88Volume;
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;


public class IntermediateToMUCOM88MMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo CreateTempoInstance(int value, MMLCommandRelation relation) {
        return new MUCOM88Tempo(value, relation);
    }

    @Override
    protected Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation) {
        return new MUCOM88Instrument((value + 1), relation);
    }

    @Override
    protected Volume CreateVolumeInstance(int value, MMLCommandRelation relation) {
        return new MUCOM88Volume(value, relation);
    }

    @Override
    protected Pan CreatePanInstance(int value, MMLCommandRelation relation) {
        return new MUCOM88Pan(value, relation);
    }

    @Override
    protected Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        return new MUCOM88Note(octave, name, length, relation);
    }

    @Override
    protected Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation) {
        return new MUCOM88Rest(length, relation);
    }

    @Override
    protected Length CreateLengthInstance(int length, MMLCommandRelation relation) {
        return new MUCOM88Length(length, relation);
    }

    @Override
    protected MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.MUCOM88);
    }
}
