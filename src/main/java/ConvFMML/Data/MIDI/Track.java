/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI;


    public class Track
    {
        public LinkedList<Event.Event> EventList { get; }

        public Track(LinkedList<Event.Event> eventList)
        {
            EventList = eventList;
        }
    }
}
