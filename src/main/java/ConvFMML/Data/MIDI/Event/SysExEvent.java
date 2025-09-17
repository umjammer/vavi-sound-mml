/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public class SysExEvent extends Event
    {
        public SysExEvent(int deltaTime) {
 super(deltaTime); }

        @Override protected  String GenerateString()
        {
            return $"{DeltaTime}:\tSysEx Event";
        }
    }
}
