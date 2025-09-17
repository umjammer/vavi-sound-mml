/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI;

import java.util.List;


public class MIDI {

    private final List<Track> trackList;
    private final int trackSize;
    private final int timeDivision;
    private final int format;

    public List<Track> getTrackList() {
        return trackList;
    }

    public int getTrackSize() {
        return trackSize;
    }

    public int getTimeDivision() {
        return timeDivision;
    }

    public int getFormat() {
        return format;
    }

    public MIDI(List<Track> trackList, int format, int trackSize, int timeDivision) {
        this.trackList = trackList;
        this.trackSize = trackList != null ? trackList.size() : 0;
        this.timeDivision = timeDivision;
        this.format = format;
    }
}
