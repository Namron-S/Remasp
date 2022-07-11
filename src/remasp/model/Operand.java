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
