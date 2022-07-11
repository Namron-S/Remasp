/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class Division extends Befehl implements Instruktion {

    public Division(Operand operand, String einLabel, int startOffset, int endOffset) {
        super(operand, einLabel, startOffset, endOffset);
    }

    @Override
    public void eval(Konfiguration konfig) {
        if (operand.getZahlenWert(konfig) > 0) {
            long ergebnis = Math.floorDiv(konfig.getRegister(0), operand.getZahlenWert(konfig));
            konfig.setRegister(0, ergebnis);
            konfig.incBz();
        } else {
            // Der Divisor ist 0 ->Programmabbruch wird durch Befehlszähler=-1 dargestellt.
            konfig.setBz(-1);
        }
    }

}
