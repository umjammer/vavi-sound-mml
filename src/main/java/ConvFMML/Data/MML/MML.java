/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML;


import java.util.List;

import ConvFMML.Common.MMLStyle;


public class MML {

    private final List<Part> partList;
    private final String title;
    private final int countsPerWholeNote;
    private final MMLStyle style;

    public List<Part> getPartList() {
        return partList;
    }

    public String getTitle() {
        return title;
    }

    public int getCountsPerWholeNote() {
        return countsPerWholeNote;
    }

    public MMLStyle getStyle() {
        return style;
    }

    public int getPartSize() {
        return partList != null ? partList.size() : 0;
    }

    public MML(List<Part> partList, String title, int countsPerWholeNote, MMLStyle style) {
        this.partList = partList;
        this.title = title;
        this.countsPerWholeNote = countsPerWholeNote;
        this.style = style;
    }
}
