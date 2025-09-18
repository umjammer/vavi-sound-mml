/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml.command.nrtdrv;

import java.util.EnumSet;
import java.util.List;

import convfmml.Common.MMLCommandRelation;
import convfmml.Common.SoundModule;
import convfmml.data.mml.command.Note;
import convfmml.Settings;


public class NRTDRVNote extends Note {

    public NRTDRVNote(int octave, String name, List<Integer> length, EnumSet<MMLCommandRelation> relation) {
        super(octave, name, length, relation);
    }

    @Override
    protected String generateString(Settings settings, SoundModule module) {
        int len = length.getFirst();
        StringBuilder str = new StringBuilder();
        Settings.NoteRest nrSettings = settings.getNoteRest();

        if (nrSettings.getTieStyle() == 0 ||
                (!getCommandRelation().contains(MMLCommandRelation.TieBefore) ||
                        getCommandRelation().contains(MMLCommandRelation.PrevControl))) {
            str.append(name);
        }
        str.append(len);

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() &&
                    (nrSettings.getDotLength().intValue() == 0 || dotlen < nrSettings.getDotLength().intValue())) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.getTieStyle() == 0) { // Tie and Name
                    str.append("&").append(name).append(length.get(i));
                } else { // Tie only
                    str.append("^").append(length.get(i));
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (getCommandRelation().contains(MMLCommandRelation.TieAfter)) {
            if (nrSettings.getTieStyle() == 1 &&
                    !getCommandRelation().contains(MMLCommandRelation.NextControl)) {
                str.append("^");
            } else {
                str.append("&");
            }
        }

        return str.toString();
    }
}
