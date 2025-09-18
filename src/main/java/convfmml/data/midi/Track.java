/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi;

import java.util.LinkedList;

import convfmml.data.midi.event.Event;


public class Track {

    protected LinkedList<Event> eventList;

    public LinkedList<Event> getEventList() {
        return eventList;
    }

    public Track(LinkedList<Event> eventList) {
        this.eventList = eventList;
    }
}
