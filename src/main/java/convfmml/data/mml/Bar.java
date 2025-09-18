/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.mml;

import java.util.LinkedList;

import convfmml.data.mml.command.Command;


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
