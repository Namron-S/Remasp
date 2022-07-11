/*
Copyright (C) 2017 Norman Sutatyo


This file is part of ReMaSp.

    ReMaSp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    ReMaSp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with ReMaSp.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von ReMaSp.

    ReMaSp ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    ReMaSp wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
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
