/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class End extends Befehl implements Instruktion {

    public End(Operand operand, String einLabel, int startOffset, int endOffset) {
        super(operand, einLabel, startOffset, endOffset);
    }

    public void eval(Konfiguration konfig) {
        konfig.setBz(-1);// Befehlszähler bz=-1 bedeutet, dass das Programm beendet wird
    }

}
