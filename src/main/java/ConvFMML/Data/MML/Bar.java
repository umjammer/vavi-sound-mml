/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML.Data.MML;

import java.util.LinkedList;

import ConvFMML.Data.MML.Command.Command;


public class Bar {

    private final LinkedList<Command> commandList;
    private final int number;
    private final String separateSign;

    public LinkedList<Command> getCommandList() {
        return commandList;
    }

    public int getNumber() {
        return number;
    }

    public String getSeparateSign() {
        return separateSign;
    }

    public Bar(LinkedList<Command> commandList, int number, String separateSign) {
        this.commandList = commandList;
        this.number = number;
        this.separateSign = separateSign;
    }
}
