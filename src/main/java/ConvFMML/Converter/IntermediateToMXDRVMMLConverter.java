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
import ConvFMML.Data.MML.Command.MXDRV.MXDRVInstrument;
import ConvFMML.Data.MML.Command.MXDRV.MXDRVNote;
import ConvFMML.Data.MML.Command.MXDRV.MXDRVPan;
import ConvFMML.Data.MML.Command.MXDRV.MXDRVRest;
import ConvFMML.Data.MML.Command.MXDRV.MXDRVTempo;
import ConvFMML.Data.MML.Command.MXDRV.MXDRVVolume;
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Data.MML.Command.Pan;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Data.MML.Command.Tempo;
import ConvFMML.Data.MML.Command.Volume;
import ConvFMML.Data.MML.MML;
import ConvFMML.Data.MML.Part;


public class IntermediateToMXDRVMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo CreateTempoInstance(int value, MMLCommandRelation relation) {
        return new MXDRVTempo(value, relation);
    }

    @Override
    protected Instrument CreateInstrumentInstance(int value, MMLCommandRelation relation) {
        return new MXDRVInstrument((value + 1), relation);
    }

    @Override
    protected Volume CreateVolumeInstance(int value, MMLCommandRelation relation) {
        return new MXDRVVolume(value, relation);
    }

    @Override
    protected Pan CreatePanInstance(int value, MMLCommandRelation relation) {
        return new MXDRVPan(value, relation);
    }

    @Override
    protected Note CreateNoteInstance(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        return new MXDRVNote(octave, name, length, relation);
    }

    @Override
    protected Rest CreateRestInstance(List<Integer> length, MMLCommandRelation relation) {
        return new MXDRVRest(length, relation);
    }

    @Override
    protected Length CreateLengthInstance(int value, MMLCommandRelation relation) {
        return null;
    }

    @Override
    protected MML CreateMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.MXDRV);
    }
}
