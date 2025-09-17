/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML.Command.MUCOM88;

import java.util.List;

import ConvFMML.Common.MMLCommandRelation;
import ConvFMML.Common.SoundModule;
import ConvFMML.Data.MML.Command.Rest;
import ConvFMML.Settings;


public class MUCOM88Rest extends Rest {

    public MUCOM88Rest(List<Integer> length, MMLCommandRelation relation) {
        super(length, relation);
    }

    @Override
    protected String GenerateString(Settings settings, SoundModule module) {
        int len = length.get(0);
        StringBuilder str = new StringBuilder();
        Settings.NoteRest nrSettings = settings.getNoteRest();

        if (nrSettings.isUnuseTiedRest() || nrSettings.getTieStyle() == 0 ||
                (!CommandRelation.HasFlag(MMLCommandRelation.TieBefore) ||
                        CommandRelation.HasFlag(MMLCommandRelation.PrevControl))) {
            str.append("r");
        }
        if (len != (int) nrSettings.getDefaultLength().intValue())
            str.append(String.valueOf(len));

        int dotlen = 0;
        for (int i = 1; i < length.size(); i++) {
            if (len * 2 == length.get(i) && nrSettings.isDotEnable() && dotlen < 1) {
                str.append(".");
                dotlen++;
            } else {
                if (nrSettings.isUnuseTiedRest() || nrSettings.getTieStyle() == 0)     // No Tie
                {
                    if (length.get(i) != nrSettings.getDefaultLength().intValue())
                        str.append("r").append(length.get(i));
                    else
                        str.append("r");
                } else       // Tie only
                {
                    str.append("^").append(length.get(i));
                }
                dotlen = 0;
            }
            len = length.get(i);
        }

        if (!nrSettings.isUnuseTiedRest() &&
                CommandRelation.HasFlag(MMLCommandRelation.TieAfter) &&
                !CommandRelation.HasFlag(MMLCommandRelation.NextControl)) {
            if (nrSettings.getTieStyle() == 1) {
                str.append("^");
            }
        }

        return str.toString();
    }
}
