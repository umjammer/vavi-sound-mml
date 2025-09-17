/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class NoteOff extends Note
    {
        public NoteOff(int deltaTime, int channel, int number, int velocity) {
 super(deltaTime, channel, number, velocity); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tNote Off\t{Channel}, {Number}, {Velocity}";
        }
    }
}
