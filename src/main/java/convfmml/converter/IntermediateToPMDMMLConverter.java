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
import convfmml.data.mml.command.Note;
import convfmml.data.mml.command.pmd.PMDInstrument;
import convfmml.data.mml.command.pmd.PMDNote;
import convfmml.data.mml.command.pmd.PMDPan;
import convfmml.data.mml.command.pmd.PMDRest;
import convfmml.data.mml.command.pmd.PMDTempo;
import convfmml.data.mml.command.pmd.PMDVolume;
import convfmml.data.mml.command.Pan;
import convfmml.data.mml.command.Rest;
import convfmml.data.mml.command.Tempo;
import convfmml.data.mml.command.Volume;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;


public class IntermediateToPMDMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo createTempoInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new PMDTempo(value, relation);
    }

    @Override
    protected Instrument createInstrumentInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new PMDInstrument((value + 1), relation);
    }

    @Override
    protected Volume createVolumeInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new PMDVolume(value, relation);
    }

    @Override
    protected Pan createPanInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new PMDPan(value, relation);
    }

    @Override
    protected Note createNoteInstance(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new PMDNote(octave, name, length, relation);
    }

    @Override
    protected Rest createRestInstance(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new PMDRest(length, relation);
    }

    @Override
    protected Length createLengthInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return null;
    }

    @Override
    protected MML createMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.PMD);
    }
}
