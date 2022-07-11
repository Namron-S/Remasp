/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class JNZero extends Befehl implements Instruktion {

    public JNZero(Operand operand, String einLabel, int startOffset, int endOffset) {
        super(operand, einLabel, startOffset, endOffset);
    }

    @Override
    public void eval(Konfiguration konfig) {
        if (konfig.getRegister(0) != 0) {
            konfig.setBz(konfig.findeIndexVonBefehlMitLabel(operand.getSprungMarke()));
        } else {
            konfig.incBz();
        }
    }
}
