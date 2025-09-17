/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Converter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ConvFMML.Settings;


public class LengthContainer implements Cloneable {

    private List<LengthElement> length = new ArrayList<>();
    private int gate = 0;

    public int getCount() {
        return length.size();
    }

    public int getGate() {
        return gate;
    }

    public int getTripletCount() {
        int cnt = 0;
        for (LengthElement e : length) {
            if (e.isTripletFlag()) {
                cnt++;
            }
        }
        return cnt;
    }

    public void AddLengthElement(LengthElement e) {
        length.add(e);
        length = length.sort(x -> x.getLength()).ToList();
        this.gate += e.getGate();
    }

    public int[] GetLength(Settings.NoteRest settings) {
        List<LengthElement> list = new ArrayList<>();

        if (settings.isDotEnable()) {
            LinkedList<LengthElement> temp = new LinkedList<>(length);
            list.add(temp.iterator().Value);
            temp.removeFirst();

            while (!temp.isEmpty()) {
                int prevlen = list.get(list.size() - 1).getLength();
                Iterator<LengthElement> n = temp.iterator();
                while (true) {
                    if (n.Value.Length == prevlen / 2) {
                        list.add(n.Value);
                        temp.remove(n);
                        break;
                    }

                    if (n.Next == null) {
                        list.add(temp.iterator().Value);
                        temp.removeFirst();
                        break;
                    } else {
                        n = n.Next;
                    }
                }
            }
        } else {
            list = length;
        }

        return list.stream().map(x -> x.getLength()).toArray();
    }

    @Override
    public LengthContainer clone() {
        var clone = new LengthContainer();
        length.forEach(x -> clone.AddLengthElement(x));
        return clone;
    }
}
