/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class Store extends Befehl implements Instruktion {

    public Store(Operand operand, String einLabel, int startOffset, int endOffset) {
        super(operand, einLabel, startOffset, endOffset);
    }

    @Override
    public void eval(Konfiguration konfig) {

        if (operand.istIndirekteAdresse) {
            if ((int) konfig.getRegister(operand.getRegisterNr()) >= 1) {
                konfig.setRegister((int) konfig.getRegister(operand.getRegisterNr()), konfig.getRegister(0));
            } else {//Fall: Die indirekte Adresse darf nicht auf Register 0 zeigen, in diesem Fall soll das Programm abgebrochen werden.
                konfig.setBz(-1);
                return;
            }
        } else {
            konfig.setRegister(operand.getRegisterNr(), konfig.getRegister(0));
        }
        konfig.incBz();
    }

}
