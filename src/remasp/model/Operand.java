/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.model;

import java.util.*;

public class Operand {

    /**
     * @return the sprungMarke
     */
    public String getSprungMarke() {
        return sprungMarke;
    }

    /**
     * @return the istKonstante
     */
    boolean istIndirekteAdresse = false;
    private boolean istKonstante = false;
    private boolean istSprungMarke;
    long konstanterWert;
    int registerNr;
    private String sprungMarke;

    public boolean getIstSprungMarke() {
        return this.istSprungMarke;
    }

    public Operand(String EineOpZeichenkette, Konfiguration eineKonfiguration, boolean istSprungMarke) throws Exception {
        this.istSprungMarke = istSprungMarke;
        if (!istSprungMarke) {
//Wir prüfen, ob wir eine gültige Zeichenkette für einen Operator vorliegen haben.
            List<Character> zahlenAlphabet = new LinkedList<>();
            zahlenAlphabet.add('0');
            zahlenAlphabet.add('1');
            zahlenAlphabet.add('2');
            zahlenAlphabet.add('3');
            zahlenAlphabet.add('4');
            zahlenAlphabet.add('5');
            zahlenAlphabet.add('6');
            zahlenAlphabet.add('7');
            zahlenAlphabet.add('8');
            zahlenAlphabet.add('9');

            char erstesZeichen = EineOpZeichenkette.charAt(0);

            if (erstesZeichen == '#' || erstesZeichen == '*' || zahlenAlphabet.contains(erstesZeichen)) {
                if (erstesZeichen == '#') {//Fall: Operand ist eine Konstante (#KonstantenWert)
                    this.istKonstante = true;
                    this.konstanterWert = Long.decode(EineOpZeichenkette.substring(1));
                }
                if (erstesZeichen == '*') {//Fall: indirekte Adressierung: Inhalt der Registerzelle c(i) ist gesucht
                    this.istIndirekteAdresse = true;
                    this.registerNr = Integer.decode(EineOpZeichenkette.substring(1).replaceFirst("0*", "")); //führende Nullen werden entfert: 023->23
                    /*
                Exception werfen wenn die registerNr. größer ist als die Anzahl der Register
                
                     */
                    if (this.registerNr > eineKonfiguration.getRegisters().size()) {
                        throw new Exception("Register Nr. " + this.registerNr + " existiert nicht.");
                    }

                }
                if (zahlenAlphabet.contains(erstesZeichen)) {// Fall: direkte Adressierung: Inhalt von Registerzelle i
                    this.registerNr = Integer.decode(EineOpZeichenkette.replaceFirst("0*", ""));
                }
            } else {
                throw new Exception("Ungültiges Operanden Format: " + EineOpZeichenkette);
            }
        } else {
            this.sprungMarke = EineOpZeichenkette;
        }

    }

    /**
     * @param eineKonfiguration
     * @return the wert
     */
    public long getZahlenWert(Konfiguration eineKonfiguration) {

        if (this.getIstKonstante()) {
            return this.konstanterWert;
        }

        if (this.istIndirekteAdresse) {
            //der Cast von long zu int ist gerechtfertigt, da es nicht mehr als max_range(int) = 2^32-1 Register geben kann.
            return eineKonfiguration.getRegister((int) eineKonfiguration.getRegister(this.registerNr));
        } else {//Es liegt eine direkte Adresse vor
            return eineKonfiguration.getRegister(this.registerNr);
        }
    }

    public int getRegisterNr() {
        return this.registerNr;
    }

    public boolean getIstKonstante() {
        return istKonstante;
    }
}
