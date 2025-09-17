/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.Intermediate;


import java.util.Iterator;
import java.util.LinkedList;

import ConvFMML.Data.Intermediate.Event.TimeSignature;
import ConvFMML.Data.MML.Bar;


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

    public Position(Position prevPosition, int deltaTime, Iterator<TimeSignature> tsNode) {
        this.bar = prevPosition.bar;
        this.tick = prevPosition.tick + deltaTime;

        while (true) {
            if (tsNode.Next == null || this.bar != tsNode.Next.Value.PrevSignedPosition.Bar) {
                if (this.tick >= tsNode.Value.TickPerBar) {
                    this.bar++;
                    this.tick -= tsNode.Value.TickPerBar;
                } else {
                    break;
                }
            } else if (compareTo(tsNode.Next.Value.PrevSignedPosition) < 0) {
                break;
            } else {
                if (tsNode.Next.Value.PrevSignedPosition.Tick > 0) {
                    this.bar++;
                    this.tick -= tsNode.Next.Value.PrevSignedPosition.Tick;
                }
                tsNode = tsNode.Next;
            }
        }
    }

    public static Position ConvertByTimeDivisionRatio(Position position, double ratio, LinkedList<TimeSignature> tsList) {
        var newTick = (int) Math.round(position.tick * ratio);
        var pos = new Position(position.bar, newTick);

        if (tsList != null) {
            Iterator<TimeSignature> tsNode = tsList.iterator();
            while (true) {
                if (tsNode.Next == null) {
                    if (pos.tick == tsNode.Value.TickPerBar) {
                        pos = new Position(pos.bar + 1, 0);
                    }
                    break;
                } else {
                    if (tsNode.Next.Value.Position.compareTo(pos) > 0) {
                        if (pos.tick == tsNode.Value.TickPerBar) {
                            pos = new Position(pos.Bar + 1, 0);
                        }
                        if (tsNode.Next.Value.PrevSignedPosition.compareTo(pos) == 0) {
                            pos = tsNode.Next.Value.Position.clone();
                        }
                        break;
                    } else {
                        tsNode = tsNode.Next;
                    }
                }
            }
        }

        return pos;
    }

    public static Position ConvertByTicksPerBar(Position position, int prevTicksPerBar, int ticksPerBar) {
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

    public Position Add(Position a, int ticksPerBar) {
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

    public Position Subtract(Position a, int ticksPerWholeNote) {
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
        return (Position) MemberwiseClone();
    }

    @Override
    public String toString() {
        return "%3d:%4d".formatted(bar, tick);
    }
}
