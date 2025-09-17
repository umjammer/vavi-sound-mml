/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate.Event;

import ConvFMML.Data.Intermediate.Position;


public class TimeSignature implements Cloneable {

    private Position position;
    private Position prevSignedPosition;
    private int numerator;
    private int denominator;
    private int tickPerBar;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPrevSignedPosition() {
        return prevSignedPosition;
    }

    public void setPrevSignedPosition(Position prevSignedPosition) {
        this.prevSignedPosition = prevSignedPosition;
    }

    public int getNumerator() {
        return numerator;
    }

    public void setNumerator(int numerator) {
        this.numerator = numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public void setDenominator(int denominator) {
        this.denominator = denominator;
    }

    public int getTickPerBar() {
        return tickPerBar;
    }

    public void setTickPerBar(int tickPerBar) {
        this.tickPerBar = tickPerBar;
    }

    public TimeSignature(Position position, Position prevSignedPosition, int numerator, int denominator, int tickPerBar) {
        this.position = position;
        this.prevSignedPosition = prevSignedPosition;
        this.numerator = numerator;
        this.denominator = denominator;
        this.tickPerBar = tickPerBar;
    }

    public TimeSignature clone() {
        var clone = (TimeSignature) MemberwiseClone();
        clone.position = position.clone();
        clone.prevSignedPosition = prevSignedPosition.clone();
        return clone;
    }
}
