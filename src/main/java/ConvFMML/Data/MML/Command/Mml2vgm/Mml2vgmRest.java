/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.Mml2vgm;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Settings;


public class Mml2vgmRest extends Rest {

    public Mml2vgmRest(List<Integer> length, MMLCommandRelation relation) {
        super(length, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        int len = length.get(0);
        StringBuilder str = new StringBuilder();
        Settings.NoteRest nrSettings = settings.getNoteRest();

        if (nrSettings.getTieStyle() == 0 ||
                !CommandRelation.HasFlag(MMLCommandRelation.TieBefore) ||
                CommandRelation.HasFlag(MMLCommandRelation.PrevControl)) {
            str.append("r");
        }
        str.append(len);

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() &&
                    (nrSettings.getDotLength().intValue() == 0 || dotlen < nrSettings.getDotLength().intValue())) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.getTieStyle() == 0)       // No Tie
                {
                    str.append("r").append(length.get(i));
                } else       // Tie only
                {
                    str.append("&").append(length.get(i));
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (nrSettings.getTieStyle() == 1 &&
                CommandRelation.HasFlag(MMLCommandRelation.TieAfter) &&
                !CommandRelation.HasFlag(MMLCommandRelation.NextControl)) {
            str.append("&");
        }

        return str.toString();
    }
}
