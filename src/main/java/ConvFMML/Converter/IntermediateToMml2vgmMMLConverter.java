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
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmInstrument;
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmNote;
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmPan;
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmRest;
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmTempo;
import ConvFMML.Data.MML.Command.Mml2vgm.Mml2vgmVolume;
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;


public class IntermediateToMml2vgmMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo CreateTempoInstance(int value, MMLCommandRelation relation) {
        return new Mml2vgmTempo(value, relation);
    }

    @Override
    protected Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation) {
        return new Mml2vgmInstrument((value + 1), relation);
    }

    @Override
    protected Volume CreateVolumeInstance(int value, MMLCommandRelation relation) {
        return new Mml2vgmVolume(value, relation);
    }

    @Override
    protected Pan CreatePanInstance(int value, MMLCommandRelation relation) {
        return new Mml2vgmPan(value, relation);
    }

    @Override
    protected Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        return new Mml2vgmNote(octave, name, length, relation);
    }

    @Override
    protected Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation) {
        return new Mml2vgmRest(length, relation);
    }

    @Override
    protected Length CreateLengthInstance(int value, MMLCommandRelation relation) {
        return null;
    }

    @Override
    protected MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.Mml2vgm);
    }
}
