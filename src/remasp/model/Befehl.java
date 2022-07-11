/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

public class Befehl {

    private String label;
    protected Operand operand;

    //Die folgenden 2 Variablen speichern die Textposition des Befehls im Editor.
    //Sie gehören eig. nicht zum Modell, sondern zur View. Etwas unschick, ließ sich aber nicht vermeiden.
    int startOffset, endOffset;

    public Befehl(Operand operand, String einLabel, int startOffset, int endOffset) {
        this.operand = operand;
        this.label = einLabel;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public void eval(Konfiguration konfig) {

    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public int getEndOffset() {
        return this.endOffset;
    }

    public Operand getOperand() {
        return this.operand;
    }

    /**
     * @return the zeilenNummer
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the zeilenNummer to set
     */
    public void setLabel(String label) {
        this.label = label;
    }
}
