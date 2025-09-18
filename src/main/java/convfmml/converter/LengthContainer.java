/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.converter;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import convfmml.Settings;


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

    public void addLengthElement(LengthElement e) {
        length.add(e);
        length.sort((x, y) -> x.getLength() - y.getLength());
        this.gate += e.getGate();
    }

    public int[] getLength(Settings.NoteRest settings) {
        List<LengthElement> list = new ArrayList<>();

        if (settings.isDotEnable()) {
            LinkedList<LengthElement> temp = new LinkedList<>(length);
            list.add(temp.getFirst());
            temp.removeFirst();

            while (!temp.isEmpty()) {
                int prevlen = list.getLast().getLength();
                Iterator<LengthElement> n = temp.iterator();
                while (true) {
                    LengthElement nValue = n.next();
                    if (nValue.getLength() == prevlen / 2) {
                        list.add(nValue);
                        temp.remove(nValue);
                        break;
                    }

                    if (!n.hasNext()) {
                        list.add(temp.getFirst());
                        temp.removeFirst();
                        break;
                    }
                }
            }
        } else {
            list = length;
        }

        return list.stream().mapToInt(LengthElement::getLength).toArray();
    }

    @Override
    public LengthContainer clone() {
        var clone = new LengthContainer();
        length.forEach(clone::addLengthElement);
        return clone;
    }
}
