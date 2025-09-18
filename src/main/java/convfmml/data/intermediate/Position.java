/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.intermediate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import convfmml.data.intermediate.event.TimeSignature;


public class Position implements Comparable<Position>, Cloneable {

    private int bar;
    private int tick;

    public int getBar() {
        return bar;
    }

    public int getTick() {
        return tick;
    }

    public Position(int bar, int tick) {
        this.bar = bar;
        this.tick = tick;
    }

    public Position(Position prevPosition, int deltaTime, ListIterator<TimeSignature> tsNodeI) {
        this.bar = prevPosition.bar;
        this.tick = prevPosition.tick + deltaTime;

        TimeSignature tsNode = tsNodeI.next();

        while (true) {
            TimeSignature nextTS = tsNodeI.hasNext() ? tsNodeI.next() : null;
            if (nextTS != null) tsNodeI.previous();
            if (nextTS == null || bar != nextTS.getPrevSignedPosition().bar) {
                if (tick >= tsNode.getTickPerBar()) {
                    bar++;
                    tick -= tsNode.getTickPerBar();
                } else {
                    break;
                }
            } else if (compareTo(nextTS.getPrevSignedPosition()) < 0) {
                break;
            } else {
                if (nextTS.getPrevSignedPosition().tick > 0) {
                    bar++;
                    tick -= nextTS.getPrevSignedPosition().tick;
                }
                tsNode = tsNodeI.next();
            }
        }
    }

    public static Position convertByTimeDivisionRatio(Position position, double ratio, LinkedList<TimeSignature> tsList) {
        var newTick = (int) Math.round(position.tick * ratio);
        var pos = new Position(position.bar, newTick);

        if (tsList != null) {
            Iterator<TimeSignature> tsNodeI = tsList.iterator();
            TimeSignature tsNodeValue = tsNodeI.next();
            while (true) {
                if (!tsNodeI.hasNext()) {
                    if (pos.tick == tsNodeValue.getTickPerBar()) {
                        pos = new Position(pos.bar + 1, 0);
                    }
                    break;
                } else {
                    TimeSignature tsNodeNext = tsNodeI.next();
                    if (tsNodeNext.getPosition().compareTo(pos) > 0) {
                        if (pos.tick == tsNodeValue.getTickPerBar()) {
                            pos = new Position(pos.bar + 1, 0);
                        }
                        if (tsNodeNext.getPrevSignedPosition().compareTo(pos) == 0) {
                            pos = tsNodeNext.getPosition().clone();
                        }
                        break;
                    }
                }
            }
        }

        return pos;
    }

    public static Position convertByTicksPerBar(Position position, int prevTicksPerBar, int ticksPerBar) {
        int newBar = 0;
        int newTick = 0;
        int tmpBar = position.bar;
        int tmpTick = position.tick;

        do {
            if (tmpTick == 0) {
                newTick += prevTicksPerBar;
                while (newTick >= ticksPerBar) {
                    newBar++;
                    newTick -= ticksPerBar;
                }
                tmpBar--;
            } else {
                newTick += tmpTick;
                while (newTick >= ticksPerBar) {
                    newBar++;
                    newTick -= ticksPerBar;
                }
                tmpTick = 0;
            }
        } while (tmpBar > 0);

        return new Position(newBar, newTick);
    }

    public Position add(Position a, int ticksPerBar) {
        int newBar = this.bar;
        int newTick = this.tick;

        newTick += a.tick;
        if (newTick >= ticksPerBar) {
            newBar++;
            newTick -= ticksPerBar;
        }
        newBar += a.bar;

        return new Position(newBar, newTick);
    }

    public Position subtract(Position a, int ticksPerWholeNote) {
        int newBar = this.bar;
        int newTick = this.tick;

        if (newTick < a.tick) {
            newTick += ticksPerWholeNote;
            newBar--;
        }

        newBar -= a.bar;
        newTick -= a.tick;

        return new Position(newBar, newTick);
    }

    @Override
    public int compareTo(Position other) {
        if (other == null) throw new NullPointerException("other");

        int cmp = this.bar - other.bar;
        if (cmp == 0) {
            return this.tick - other.tick;
        } else {
            return cmp;
        }
    }

    @Override
    public Position clone() {
        return new Position(this.bar, this.tick);
    }

    @Override
    public String toString() {
        return "%3d:%4d".formatted(bar, tick);
    }
}
