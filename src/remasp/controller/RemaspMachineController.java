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
package remasp.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import remasp.model.Addition;
import remasp.model.Befehl;
import remasp.model.Division;
import remasp.model.End;
import remasp.model.Goto;
import remasp.model.Jzero;
import remasp.model.JNZero;
import remasp.model.KeinGueltigerBefehlException;
import remasp.model.KeinGueltigerOperandException;
import remasp.model.Konfiguration;
import remasp.model.Load;
import remasp.model.Multiplikation;
import remasp.model.Operand;
import remasp.model.Store;
import remasp.model.Subtraktion;
import remasp.view.RemaspView;

public class RemaspMachineController {

    private final RemaspView remaspView;
    private final Konfiguration konfiguration;
    private Timer timerProgramm = null;
    ZeilenFaerber zf;
    TabellenFaerber tf;
    ProgrammAusfuehrerAL programmAusfuehrer;
    ArrayList<Long> registerZwischenSpeicher;

    class ProgrammAusfuehrerAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            Befehl aktBefehl = konfiguration.getAktuelllerBefehl();
            try {
                aktBefehl.eval(konfiguration);
                aktualisiereTableView();
                if (konfiguration.getBz() != -1) {
                    if (konfiguration.getBz() >= konfiguration.getBefehlsSpeicher().size()) {
                        //Fall: Der letzte Befehl war kein End-Befehl
                        throw new Exception("Der letzte Befehl war kein End-Befehl.");
                    }
                    zf.faerbeZeile(Color.yellow, konfiguration.getAktuelllerBefehl().getStartOffset(), konfiguration.getAktuelllerBefehl().getEndOffset());
                    tf.frbeZeile();
                } else {
                    String nachricht = "";
                    if (aktBefehl instanceof End) {
                        nachricht = "Grund: End-Befehl gefunden.";
                    } else if (aktBefehl instanceof Division) {
                        nachricht = "Grund: Es wurde durch 0 geteilt.";
                    } else if (aktBefehl instanceof Store) {
                        nachricht = "Grund: Eine indirekte Adresse darf sich beim Store-Befehl nicht auf die Registerzelle 0 beziehen.";
                    } else if (aktBefehl instanceof Goto || aktBefehl instanceof JNZero || aktBefehl instanceof Jzero) {
                        nachricht = "Die Sprungmarke " + aktBefehl.getOperand().getSprungMarke() + " konnte nicht gefunden werden.";
                    }
                    programmAbbrechen(nachricht, false);
                }
            } catch (BadLocationException ex) {
                programmAbbrechen(ex.getLocalizedMessage(), false);
            } catch (ArithmeticException ex) {
                programmAbbrechen("Grund: Der Befehl führt zu einem arithmetischen Überlauf in einer Registerzelle.", false);
            } catch (IndexOutOfBoundsException ex) {
                programmAbbrechen("Es wurde auf eine nicht existierende Registerzelle zugegriffen.", false);
            } catch (Exception ex) {
                programmAbbrechen(ex.getLocalizedMessage(), false);
            }
        }
    }

    class StarteProgrammACtionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                transferTableViewToTableModel();
                iteriereUeberEditorZeilen();
                if (konfiguration.getBefehlsSpeicher().isEmpty()) {
                    meldungAusgeben("Es konnten keine Befehle gefunden werden.");
                    return;
                }
                //Timer starten!! :D
                int zeitProBefehl = (int) remaspView.getjSpinnerMilliSekProBefehl().getValue();
                timerProgramm.setDelay(zeitProBefehl);
                timerProgramm.setInitialDelay(zeitProBefehl);
                enableGui(false);
                remaspView.getjButtonStarteProgramm().setEnabled(false);
                remaspView.getjButtonProgrammAbbrechen().setEnabled(true);
                remaspView.getjButtonStarteEinzelschrittModus().setEnabled(false);
                remaspView.getjButtonEinzelSchritt().setEnabled(false);
                timerProgramm.start();
                zf.faerbeZeile(Color.yellow, konfiguration.getAktuelllerBefehl().getStartOffset(), konfiguration.getAktuelllerBefehl().getEndOffset());
                tf.frbeZeile();
            } catch (NumberFormatException nfex) {

                programmAbbrechen(nfex.getLocalizedMessage(), true);

            } catch (KeinGueltigerBefehlException e) {
                try {
                    zf.faerbeZeile(Color.RED, e.getStartOffset(), e.getEndOffset());
                    programmAbbrechen(e.getLocalizedMessage(), true);
                } catch (BadLocationException ex) {
                    Logger.getLogger(RemaspMachineController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (KeinGueltigerOperandException e) {
                try {
                    zf.faerbeZeile(Color.RED, e.getStartOffset(), e.getEndOffset());
                    programmAbbrechen(e.getLocalizedMessage(), true);
                } catch (BadLocationException ex) {
                    Logger.getLogger(RemaspMachineController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                programmAbbrechen(ex.getLocalizedMessage(), true);
            }
        }
    }

    class StarteEinzelSchirttModusAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                transferTableViewToTableModel();
                iteriereUeberEditorZeilen();
                if (konfiguration.getBefehlsSpeicher().isEmpty()) {
                    meldungAusgeben("Es konnten keine Befehle gefunden werden.");
                    return;
                }
                enableGui(false);
                remaspView.getjButtonStarteProgramm().setEnabled(false);
                remaspView.getjButtonProgrammAbbrechen().setEnabled(true);
                remaspView.getjButtonStarteEinzelschrittModus().setEnabled(false);
                remaspView.getjButtonEinzelSchritt().setEnabled(true);
                zf.faerbeZeile(Color.yellow, konfiguration.getAktuelllerBefehl().getStartOffset(), konfiguration.getAktuelllerBefehl().getEndOffset());
                tf.frbeZeile();
            } catch (KeinGueltigerBefehlException e) {
                try {
                    zf.faerbeZeile(Color.RED, e.getStartOffset(), e.getEndOffset());
                    programmAbbrechen(e.getLocalizedMessage(), true);
                } catch (BadLocationException ex) {
                    Logger.getLogger(RemaspMachineController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (KeinGueltigerOperandException e) {
                try {
                    zf.faerbeZeile(Color.RED, e.getStartOffset(), e.getEndOffset());
                    programmAbbrechen(e.getLocalizedMessage(), true);
                } catch (BadLocationException ex) {
                    Logger.getLogger(RemaspMachineController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                programmAbbrechen(ex.getLocalizedMessage(), true);
            }
        }
    }

    class EinzelSchrittAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            programmAusfuehrer.actionPerformed(ae);
        }

    }

    private void programmAbbrechen(String nachricht, boolean nurÜbergebeneNachrichtAnzeigen) {
        timerProgramm.stop();
        if (nurÜbergebeneNachrichtAnzeigen) {
            meldungAusgeben(nachricht);
        } else {
            meldungAusgeben("Programm ist beendet.\n" + nachricht);
        }

        //Wir löschen alle Färbungen
        tf.frbeZeile(-1);
        zf.allesWeiß();

        enableGui(true);
        remaspView.getjButtonStarteProgramm().setEnabled(true);
        remaspView.getjButtonProgrammAbbrechen().setEnabled(false);
        remaspView.getjButtonStarteEinzelschrittModus().setEnabled(true);
        remaspView.getjButtonEinzelSchritt().setEnabled(false);
        konfiguration.setBz(0);
        konfiguration.getBefehlsSpeicher().clear();
    }

    class TabellenFaerber {

        void frbeZeile() {
            Operand aktuellerOperand = konfiguration.getAktuelllerBefehl().getOperand();
            if (aktuellerOperand != null) {
                if (!aktuellerOperand.getIstKonstante() && !aktuellerOperand.getIstSprungMarke()) {
                    int zeilenNr = aktuellerOperand.getRegisterNr();
                    remaspView.getaCustomCellRenderer().setZuFaerbendeZeile(zeilenNr);
                } else {
                    remaspView.getaCustomCellRenderer().setZuFaerbendeZeile(-1);
                }
            } else {
                remaspView.getaCustomCellRenderer().setZuFaerbendeZeile(-1);
            }
            remaspView.getjTable1().repaint();
        }

        void frbeZeile(int zeilenNr) {
            remaspView.getaCustomCellRenderer().setZuFaerbendeZeile(zeilenNr);
            remaspView.getjTable1().repaint();
        }
    }

    class ProgrammAbbrechenAL implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            programmAbbrechen("Benutzerabbruch", false);
        }
    }

    private void transferTableViewToTableModel() throws Exception {

        //Die erste for-Schleife testet nur, ob alle Tabellenwerte echt größer Null sind
        for (int i = 0; i < konfiguration.getRegisters().size(); i++) {
            long aktuellerZellenWert = (Long) remaspView.getjTable1().getModel().getValueAt(i, 1);
            if (aktuellerZellenWert < 0) {
                throw new Exception("Registerzelle " + i + ": Zahl muss >= 0 sein");
            }
        }
        //Die zweite for-Schleife speichert die Tabellenwerte ins Modell (die konfiguration)
        for (int i = 0; i < konfiguration.getRegisters().size(); i++) {
            long aktuellerZellenWert = (Long) remaspView.getjTable1().getModel().getValueAt(i, 1);
            konfiguration.setRegister(i, aktuellerZellenWert);
        }
    }

    private void aktualisiereTableView() {
        for (int i = 0; i < konfiguration.getRegisters().size(); i++) {

            remaspView.getjTable1().getModel().setValueAt(konfiguration.getRegister(i), i, 1);
        }

    }

    private void setzeAlleRegisterAufNull() {
        for (int i = 0; i < konfiguration.getRegisters().size(); i++) {
            konfiguration.setRegister(i, 0L);
        }
    }

    private void enableGui(boolean bln) {
        remaspView.getButtonDrucken().setEnabled(bln);
        remaspView.getButtonNeu().setEnabled(bln);
        remaspView.getButtonOeffnen().setEnabled(bln);
        remaspView.getButtonSpeichern().setEnabled(bln);
        remaspView.getjButtonRegisterZureuckstzn().setEnabled(bln);
        remaspView.getjButtonSetzeNeueAnzahlRegister().setEnabled(bln);
        remaspView.getjMenuBearbeiten().setEnabled(bln);
        remaspView.getjMenuFormat().setEnabled(bln);
        remaspView.getjMenuDatei().setEnabled(bln);
        remaspView.getjSpinnerMilliSekProBefehl().setEnabled(bln);
        remaspView.getjTextPane().setEditable(bln);
    }

    private void meldungAusgeben(String nachricht) {
        JTextArea anzeigeText = new JTextArea();
        anzeigeText.setText(nachricht);
        Font currentFont = anzeigeText.getFont();
        anzeigeText.setFont(new Font(currentFont.getFontName(), currentFont.getStyle(), currentFont.getSize() + 4));
        JOptionPane.showMessageDialog(this.remaspView, anzeigeText);
    }

    private void iteriereUeberEditorZeilen() throws BadLocationException, Exception {
        Element root = remaspView.getjTextPane().getDocument().getDefaultRootElement();
        Element zeilenElement;
        String zeilenText;
        int anzahlZeilen = root.getElementCount();
        int startOffset, endOffset;

        //Wir iterieren jetzt über die QuellCode Zeilen im Editor
        for (int i = 0; i < anzahlZeilen; i++) {
            zeilenElement = root.getElement(i);
            startOffset = zeilenElement.getStartOffset();
            endOffset = zeilenElement.getEndOffset();

            zeilenText = root.getDocument().getText(startOffset, endOffset - startOffset);
            erstelleProgrammBefehl(zeilenText, startOffset, endOffset);
        }
    }

    private void erstelleProgrammBefehl(String zeilenText, int startOffset, int endOffset) throws NumberFormatException, Exception {
        String label;
        label = "";
        // Jetzt entfernen wir eventuelle Kommentare die sich am Ende der Zeile befinden //zeilenText.matches("regEx");
        int kommentarStart = zeilenText.indexOf("//");
        if (kommentarStart != -1) {
            zeilenText = zeilenText.substring(0, kommentarStart);
        }
        //Wir entfernen alle mehrfach auftretende Leerzeichen und Tabulatoren:     
        zeilenText = zeilenText.trim().replaceAll("\t+", " ");
        zeilenText = zeilenText.replaceAll(" +", " ");
        List<String> befehlGesplittet = new ArrayList(Arrays.asList(zeilenText.split(" ")));

// Fall 1: Es lag eine Kommentarzeile vor, also ist der zeilenText jetzt leer.
        if (zeilenText.isEmpty()) {
            return;
        }

// Wir schauen, ob das erste Wort ein Label ist (in der Form "LabelName:")
        if (befehlGesplittet.get(0).endsWith(":")) {
            label = befehlGesplittet.get(0).substring(0, befehlGesplittet.get(0).length() - 1); // Wir lassen den ":" weg
            befehlGesplittet.remove(0);
            if(befehlGesplittet.size()==0){
                throw new KeinGueltigerBefehlException("Ein Label muss vor einem Befehl stehen. " + "", startOffset, endOffset);
            }
        }

//Fall 2: Wir testen, ob der END-Befehl vorliegt 
        if (befehlGesplittet.size() == 1) {
            // Die Zeile hat 1 String: Der END-Befehl

            Pattern p = Pattern.compile("END", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(befehlGesplittet.get(0));
            boolean gueltigerEndbefehl = m.matches();
            if (gueltigerEndbefehl == false) {
                String nachricht = label + " " + befehlGesplittet.get(0);
                throw new KeinGueltigerBefehlException("Kein gültiger Befehl " + nachricht, startOffset, endOffset);
            } else {
                //befehlsSpeicher.size() bestimmt die interneZeilenNr. des Befehls.
                End end = new End(null, label, startOffset, endOffset);
                konfiguration.getBefehlsSpeicher().add(end);
            }
        }
        // Fall3: Es liegt ein Befehl mit einem Argument  vor
        if (befehlGesplittet.size() == 2) {
            String befehlString = befehlGesplittet.get(0);
            String operandString = befehlGesplittet.get(1);
            Operand operand;

            Pattern loadPattern = Pattern.compile("LOAD", Pattern.CASE_INSENSITIVE);
            Matcher loadMatcher = loadPattern.matcher(befehlString);
            boolean gueltigerLoadBefehl = loadMatcher.matches();

            Pattern storePattern = Pattern.compile("STORE", Pattern.CASE_INSENSITIVE);
            Matcher storeMatcher = storePattern.matcher(befehlString);
            boolean gueltigerStoreBefehl = storeMatcher.matches();

            Pattern addPattern = Pattern.compile("ADD", Pattern.CASE_INSENSITIVE);
            Matcher addMatcher = addPattern.matcher(befehlString);
            boolean gueltigerAdddBefehl = addMatcher.matches();

            Pattern subPattern = Pattern.compile("SUB", Pattern.CASE_INSENSITIVE);
            Matcher subMatcher = subPattern.matcher(befehlString);
            boolean gueltigerSubBefehl = subMatcher.matches();

            Pattern mulPattern = Pattern.compile("MUL", Pattern.CASE_INSENSITIVE);
            Matcher mulMatcher = mulPattern.matcher(befehlString);
            boolean gueltigerMulBefehl = mulMatcher.matches();

            Pattern divPattern = Pattern.compile("DIV", Pattern.CASE_INSENSITIVE);
            Matcher divMatcher = divPattern.matcher(befehlString);
            boolean gueltigerDivdBefehl = divMatcher.matches();

            Pattern gotoPattern = Pattern.compile("GOTO", Pattern.CASE_INSENSITIVE);
            Matcher gotoMatcher = gotoPattern.matcher(befehlString);
            boolean gueltigerGotoBefehl = gotoMatcher.matches();

            Pattern jzeroPattern = Pattern.compile("JZERO", Pattern.CASE_INSENSITIVE);
            Matcher jzeroMatcher = jzeroPattern.matcher(befehlString);
            boolean gueltigerJzeroBefehl = jzeroMatcher.matches();

            Pattern jnzeroPattern = Pattern.compile("JNZERO", Pattern.CASE_INSENSITIVE);
            Matcher jNzeroMatcher = jnzeroPattern.matcher(befehlString);
            boolean gueltigerJNzeroBefehl = jNzeroMatcher.matches();

            if (gueltigerJzeroBefehl || gueltigerGotoBefehl || gueltigerJNzeroBefehl) {
                operand = new Operand(operandString, konfiguration, true);
            } else {
                try {
                    operand = new Operand(operandString, konfiguration, false);
                } catch (Exception e) {
                    throw new KeinGueltigerOperandException("Kein gültiger Operand: " + operandString, startOffset, endOffset);
                }
            }
            if (gueltigerAdddBefehl || gueltigerDivdBefehl || gueltigerGotoBefehl || gueltigerJzeroBefehl || gueltigerJNzeroBefehl || gueltigerLoadBefehl || gueltigerMulBefehl || gueltigerStoreBefehl || gueltigerSubBefehl) {

                if (gueltigerLoadBefehl) {
                    Load einLoad = new Load(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(einLoad);
                }
                if (gueltigerAdddBefehl) {
                    Addition eineAddition = new Addition(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(eineAddition);
                }

                if (gueltigerDivdBefehl) {
                    Division eineDivision = new Division(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(eineDivision);
                }
                if (gueltigerGotoBefehl) {
                    Goto einGoto = new Goto(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(einGoto);
                }
                if (gueltigerJzeroBefehl) {
                    Jzero einJzero = new Jzero(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(einJzero);
                }
                if (gueltigerJNzeroBefehl) {
                    JNZero einJNzero = new JNZero(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(einJNzero);
                }
                if (gueltigerMulBefehl) {
                    Multiplikation eineMulitplikation = new Multiplikation(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(eineMulitplikation);
                }
                if (gueltigerStoreBefehl) {
                    Store einStore = new Store(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(einStore);
                }

                if (gueltigerSubBefehl) {
                    Subtraktion eineSubtraktion = new Subtraktion(operand, label, startOffset, endOffset);
                    konfiguration.getBefehlsSpeicher().add(eineSubtraktion);
                }
            } else {

                throw new KeinGueltigerBefehlException("kein gültiger Befehl\n" + label + " " + befehlString + " " + operandString, startOffset, endOffset);
            }

        }

        //
        if (befehlGesplittet.size() >= 3) {
            String falscherBefehl = "";
            for (String s : befehlGesplittet) {
                falscherBefehl += s + " ";
            }
            throw new KeinGueltigerBefehlException("Kein gültiger Befehl\n" + falscherBefehl, startOffset, endOffset);
        }
    }

    private void addActionListeners() {
        this.remaspView.getjButtonStarteProgramm().addActionListener(new StarteProgrammACtionListener());
        this.remaspView.getjButtonRegisterZureuckstzn().addActionListener(new RegisterAufNullActionListener());
        this.remaspView.getjButtonSetzeNeueAnzahlRegister().addActionListener(new SetzeNeueAnzahlRegisterAL());
        this.remaspView.getjButtonProgrammAbbrechen().addActionListener(new ProgrammAbbrechenAL());
        this.remaspView.getjButtonStarteEinzelschrittModus().addActionListener(new StarteEinzelSchirttModusAL());
        this.remaspView.getjButtonEinzelSchritt().addActionListener(new EinzelSchrittAL());
        this.remaspView.getjButtonRegisterLaden().addActionListener(new RegisterLadenActionListener());
        this.remaspView.getjButtonRegisterSpeichern().addActionListener(new RegisterZwischenspeichernActionListener());
        this.remaspView.getjMenuItemUeber().addActionListener(new UeberActionListener());
    }

    class SetzeNeueAnzahlRegisterAL implements ActionListener {

        String eingabeText;
        int neueAnzahlRegister;

        @Override
        public void actionPerformed(ActionEvent ae) {

            eingabeText = remaspView.getjTextFieldNeueAnzahlRegister().getText();

            try {
                neueAnzahlRegister = Integer.parseInt(eingabeText);
                if (neueAnzahlRegister < 2) {
                    throw new Exception("Es müssen mind. 2 Register vorhanden sein.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(remaspView, "Ungültiges Zahlenformat.\n Bitte nur natürliche Zahlen größer 1 eingeben");
                return;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(remaspView, "Die eingegebene Zahl muss mindestens 2 groß sein.");
                return;

            }

            //Wir löschen die alte Tabelle in der View und im Model:
            konfiguration.getRegisters().clear();
            int alteAnzahlZeilen = remaspView.getjTable1().getRowCount();
            DefaultTableModel tableModel = ((DefaultTableModel) remaspView.getjTable1().getModel());
            for (int i = 0; i < alteAnzahlZeilen; i++) {
                tableModel.removeRow(0);
            }

            //Wir erstellen die Tabelle in der View und im Model (in d. Konfiguration) neu
            for (int i = 0; i < neueAnzahlRegister; i++) {
                tableModel.addRow(new Object[]{+ i, 0L});
                konfiguration.getRegisters().add(0L);
            }
        }
    }

    class RegisterAufNullActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {

            setzeAlleRegisterAufNull();
            aktualisiereTableView();
        }

    }

    class ZeilenFaerber {

        Highlighter meinHighlighter = remaspView.getjTextPane().getHighlighter();

        private void allesWeiß() {
            meinHighlighter.removeAllHighlights();
        }

        private void faerbeZeile(Color eineFarbe, int startOffset, int endOffset) throws BadLocationException {

            meinHighlighter.removeAllHighlights();
            meinHighlighter.addHighlight(startOffset, endOffset, new DefaultHighlighter.DefaultHighlightPainter(
                    eineFarbe));
        }
    }

    public RemaspMachineController(RemaspView remaspView) {
        this.remaspView = remaspView;
        this.konfiguration = new Konfiguration();
        this.addActionListeners();
        zf = new ZeilenFaerber();
        tf = new TabellenFaerber();
        programmAusfuehrer = new ProgrammAusfuehrerAL();
        this.timerProgramm = new Timer(0, programmAusfuehrer);
    }

    private class UeberActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            meldungAusgeben("ReMaSp: Ein RegisterMaschinen Simulationsprogramm.\n"
                    + "Copyright (C) 2017 Norman Sutatyo\n"
                    + "Lizenz: GNU GENERAL PUBLIC LICENSE, Version 3");
        }
    }

    class RegisterZwischenspeichernActionListener implements ActionListener {

        public RegisterZwischenspeichernActionListener() {
            registerZwischenSpeicher = new ArrayList(100);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                transferTableViewToTableModel();
                registerZwischenSpeicher.clear();
                registerZwischenSpeicher.addAll(konfiguration.getRegisters());
                remaspView.getjButtonRegisterLaden().setEnabled(true);
            } catch (Exception ex) {
                meldungAusgeben(ex.getMessage());
            }
        }
    }

    class RegisterLadenActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            int minSize = Math.min(konfiguration.getRegisters().size(), registerZwischenSpeicher.size());
            for (int i = 0; i < minSize; i++) {
                konfiguration.setRegister(i, registerZwischenSpeicher.get(i));
            }
            aktualisiereTableView();
        }
    }
}
