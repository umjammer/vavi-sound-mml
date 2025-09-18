/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.converter;

import java.util.EnumSet;
import java.util.List;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.MMLStyle;
import convfmml.data.mml.command.Instrument;
import convfmml.data.mml.command.Length;
import convfmml.data.mml.command.mml2vgm.Mml2vgmInstrument;
import convfmml.data.mml.command.mml2vgm.Mml2vgmNote;
import convfmml.data.mml.command.mml2vgm.Mml2vgmPan;
import convfmml.data.mml.command.mml2vgm.Mml2vgmRest;
import convfmml.data.mml.command.mml2vgm.Mml2vgmTempo;
import convfmml.data.mml.command.mml2vgm.Mml2vgmVolume;
import convfmml.data.mml.command.Note;
import convfmml.data.mml.command.Pan;
import convfmml.data.mml.command.Rest;
import convfmml.data.mml.command.Tempo;
import convfmml.data.mml.command.Volume;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;


public class IntermediateToMml2vgmMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo createTempoInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmTempo(value, relation);
    }

    @Override
    protected Instrument createInstrumentInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmInstrument((value + 1), relation);
    }

    @Override
    protected Volume createVolumeInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmVolume(value, relation);
    }

    @Override
    protected Pan createPanInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmPan(value, relation);
    }

    @Override
    protected Note createNoteInstance(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmNote(octave, name, length, relation);
    }

    @Override
    protected Rest createRestInstance(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new Mml2vgmRest(length, relation);
    }

    @Override
    protected Length createLengthInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return null;
    }

    @Override
    protected MML createMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.Mml2vgm);
    }
}
