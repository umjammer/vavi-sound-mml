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
import convfmml.data.mml.command.mucom88.MUCOM88Instrument;
import convfmml.data.mml.command.mucom88.MUCOM88Length;
import convfmml.data.mml.command.mucom88.MUCOM88Note;
import convfmml.data.mml.command.mucom88.MUCOM88Pan;
import convfmml.data.mml.command.mucom88.MUCOM88Rest;
import convfmml.data.mml.command.mucom88.MUCOM88Tempo;
import convfmml.data.mml.command.mucom88.MUCOM88Volume;
import convfmml.data.mml.command.Note;
import convfmml.data.mml.command.Pan;
import convfmml.data.mml.command.Rest;
import convfmml.data.mml.command.Tempo;
import convfmml.data.mml.command.Volume;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;


public class IntermediateToMUCOM88MMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo createTempoInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Tempo(value, relation);
    }

    @Override
    protected Instrument createInstrumentInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Instrument((value + 1), relation);
    }

    @Override
    protected Volume createVolumeInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Volume(value, relation);
    }

    @Override
    protected Pan createPanInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Pan(value, relation);
    }

    @Override
    protected Note createNoteInstance(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Note(octave, name, length, relation);
    }

    @Override
    protected Rest createRestInstance(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Rest(length, relation);
    }

    @Override
    protected Length createLengthInstance(int length, EnumSet<MMLCommandRelation> relation) {
        return new MUCOM88Length(length, relation);
    }

    @Override
    protected MML createMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new MML(partList, title, countsPerWholeNote, MMLStyle.MUCOM88);
    }
}
