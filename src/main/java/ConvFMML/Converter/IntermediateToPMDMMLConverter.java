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
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Data.MML.Command.PMD.PMDInstrument;
import ConvFMML.Data.MML.Command.PMD.PMDNote;
import ConvFMML.Data.MML.Command.PMD.PMDPan;
import ConvFMML.Data.MML.Command.PMD.PMDRest;
import ConvFMML.Data.MML.Command.PMD.PMDTempo;
import ConvFMML.Data.MML.Command.PMD.PMDVolume;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;


public class IntermediateToPMDMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo CreateTempoInstance(int value, MMLCommandRelation relation) {
        return new PMDTempo(value, relation);
    }

    @Override
    protected Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation) {
        return new PMDInstrument((value + 1), relation);
    }

    @Override
    protected Volume CreateVolumeInstance(int value, MMLCommandRelation relation) {
        return new PMDVolume(value, relation);
    }

    @Override
    protected Pan CreatePanInstance(int value, MMLCommandRelation relation) {
        return new PMDPan(value, relation);
    }

    @Override
    protected Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        return new PMDNote(octave, name, length, relation);
    }

    @Override
    protected Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation) {
        return new PMDRest(length, relation);
    }

    @Override
    protected Length CreateLengthInstance(int value, MMLCommandRelation relation) {
        return null;
    }

    @Override
    protected MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.PMD);
    }
}
