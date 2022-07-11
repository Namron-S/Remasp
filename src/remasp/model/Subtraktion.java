/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class Subtraktion extends Befehl implements Instruktion {

    public Subtraktion(Operand operand, String einLabel, int startOffset, int endOffset) {
        super(operand, einLabel, startOffset, endOffset);
    }

    public void eval(Konfiguration konfig) {
        long ergebnis = konfig.getRegister(0) - operand.getZahlenWert(konfig);
        if (ergebnis > 0) {
            konfig.setRegister(0, ergebnis);
        } else {
            konfig.setRegister(0, 0);
        }
        konfig.incBz();
    }
}
