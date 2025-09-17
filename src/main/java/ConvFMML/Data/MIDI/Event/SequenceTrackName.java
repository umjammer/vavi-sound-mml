/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class SequenceTrackName extends MetaEvent
    {
        public String Name { get; }

        public SequenceTrackName(int deltaTime, String name) {
    super(deltaTime);
Name = name;
        }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tSequence Track Name\t{Name}";
        }
    }
}
