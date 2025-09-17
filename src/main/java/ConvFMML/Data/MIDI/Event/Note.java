/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MIDI.Event;


    public abstract class Note extends MIDIEvent
    {
        public int Number { get; }
        public int Velocity { get; }

        protected Note(int deltaTime, int channel, int number, int velocity) {
    super(deltaTime, channel);
Number = number;
            Velocity = velocity;
        }

        @Override
        protected abstract String GenerateString();
    }
}
