/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class NoteOn extends Note
    {
        public NoteOn(int deltaTime, int channel, int number, int velocity) {
 super(deltaTime, channel, number, velocity); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tNote On\t{Channel}, {Number}, {Velocity}";
        }
    }
}
