/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
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
