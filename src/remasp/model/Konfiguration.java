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

import java.util.ArrayList;
import java.util.List;

public class Konfiguration {

    private int bz; // Befehlszähler
    private ArrayList<Long> registers; //Register
    private List<Befehl> befehlsSpeicher;

    public Konfiguration() {
        setBz(0);
        registers = new ArrayList<Long>(1000);
        for (int i = 0; i < 100; i++) {
            registers.add(i, 0L);
        }
        this.befehlsSpeicher = new ArrayList<Befehl>(1000);
    }

    int findeIndexVonBefehlMitLabel(String lName) {
        int gesuchterIndex = -1;
        for (int i = 0; i < befehlsSpeicher.size(); i++) {
            if (befehlsSpeicher.get(i).getLabel().equals(lName)) {
                gesuchterIndex = i;
                break;
            }
        }
        return gesuchterIndex;
    }

    public Befehl getAktuelllerBefehl() {
        return this.befehlsSpeicher.get(bz);
    }

    // Getters, Setter....    
    public List<Befehl> getBefehlsSpeicher() {
        return befehlsSpeicher;
    }

    public void setBz(int wert) {
        this.bz = wert;
    }

    public int getBz() {
        return this.bz;
    }

    public void incBz() {
        this.setBz(this.getBz() + 1);
    }

    public long getRegister(int i) {
        return this.registers.get(i);
    }

    public void setRegister(int i, long wert) {
        this.registers.set(i, wert);
    }

    public ArrayList<Long> getRegisters() {
        return registers;
    }
}
