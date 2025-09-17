/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.NRTDRV;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Length;
import ConvFMML.Data.MML.Command.Note;
import ConvFMML.Settings;


public class NRTDRVNote extends Note {

    public NRTDRVNote(int octave, String name, List<Integer> length, MMLCommandRelation relation) {
        super(octave, name, length, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        int len = length.get(0);
        StringBuilder str = new StringBuilder();
        Settings.NoteRest nrSettings = settings.getNoteRest();

        if (nrSettings.getTieStyle() == 0 ||
                (!CommandRelation.HasFlag(MMLCommandRelation.TieBefore) ||
                        CommandRelation.HasFlag(MMLCommandRelation.PrevControl))) {
            str.append(name);
        }
        str.append(String.valueOf(len));

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() &&
                    (nrSettings.getDotLength().intValue() == 0 || dotlen < nrSettings.getDotLength().intValue())) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.getTieStyle() == 0)       // Tie and Name
                {
                    str.append("&").append(name).append(length.get(i));
                } else       // Tie only
                {
                    str.append("^").append(length.get(i));
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (CommandRelation.HasFlag(MMLCommandRelation.TieAfter)) {
            if (nrSettings.getTieStyle() == 1 &&
                    !CommandRelation.HasFlag(MMLCommandRelation.NextControl)) {
                str.append("^");
            } else {
                str.append("&");
            }
        }

        return str.toString();
    }
}
