/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package convfmml.data.midi.event;


public class KeySignature extends MetaEvent {

    public int signatureNumber;

    public int getSignatureNumber() {
        return signatureNumber;
    }

    public int minorFlagNumber;

    public int getMinorFlagNumber() {
        return minorFlagNumber;
    }

    public KeySignature(int deltaTime, int signatureNumber, int minorFlagNumber) {
        super(deltaTime);
        this.signatureNumber = signatureNumber;
        this.minorFlagNumber = minorFlagNumber;
    }

    @Override
    protected String generateString() {
        return "%d:\tKey Signature\t%d, %d".formatted(deltaTime, signatureNumber, minorFlagNumber);
    }
}
