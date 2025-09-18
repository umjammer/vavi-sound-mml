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
import convfmml.data.mml.command.custom.CustomInstrument;
import convfmml.data.mml.command.custom.CustomNote;
import convfmml.data.mml.command.custom.CustomPan;
import convfmml.data.mml.command.custom.CustomRest;
import convfmml.data.mml.command.custom.CustomTempo;
import convfmml.data.mml.command.custom.CustomVolume;
import convfmml.data.mml.command.Instrument;
import convfmml.data.mml.command.Length;
import convfmml.data.mml.command.Note;
import convfmml.data.mml.command.Pan;
import convfmml.data.mml.command.Rest;
import convfmml.data.mml.command.Tempo;
import convfmml.data.mml.command.Volume;
import convfmml.data.mml.MML;
import convfmml.data.mml.Part;


public class IntermediateToCustomMMLConverter extends IntermediateToMMLConverter {

    @Override
    protected Tempo createTempoInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new CustomTempo(value, relation);
    }

    @Override
    protected Instrument createInstrumentInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new CustomInstrument((value + 1), relation);
    }

    @Override
    protected Volume createVolumeInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new CustomVolume(value, relation);
    }

    @Override
    protected Pan createPanInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return new CustomPan(value, relation);
    }

    @Override
    protected Note createNoteInstance(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new CustomNote(octave, name, length, relation);
    }

    @Override
    protected Rest createRestInstance(List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        return new CustomRest(length, relation);
    }

    @Override
    protected Length createLengthInstance(int value, EnumSet<MMLCommandRelation> relation) {
        return null;
    }

    @Override
    protected MML createMMLInstance(List<Part> partList, String title, int countsPerWholeNote) {
        return new convfmml.data.mml.MML(partList, title, countsPerWholeNote, MMLStyle.Custom);
    }
}
