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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import remasp.view.RemaspView;

public class RemaspEditorController {

    private final RemaspView remaspView;
    //Variablen die den Zustand des Textdokumentes speichern
    private String currentFileName;
    AbstractDocument doc;
    protected UndoManager undo = new UndoManager();
    boolean textHasChanged = false;

    RemaspEditorController(RemaspView remaspView) {
        this.remaspView = remaspView;
        this.doc = (AbstractDocument) this.remaspView.getjTextPane().getStyledDocument();
        doc.addUndoableEditListener(new MyUndoableEditListener());
        this.addActionListener();
        currentFileName = "Unbenannt";
        remaspView.setTitle(currentFileName + " - ReMaSp");
    }

    private void addActionListener() {
        NeuActionListener neuListener = new NeuActionListener();
        remaspView.getButtonNeu().addActionListener(neuListener);
        remaspView.getjMenuItemNeu().addActionListener(neuListener);
        OeffnenActionListener oeffnenListener = new OeffnenActionListener();
        remaspView.getButtonOeffnen().addActionListener(oeffnenListener);
        remaspView.getjMenuItemOeffnen().addActionListener(oeffnenListener);
        SpeichernAcitonListener speichernListener = new SpeichernAcitonListener();
        remaspView.getButtonSpeichern().addActionListener(speichernListener);
        remaspView.getjMenuItemSpeichern().addActionListener(speichernListener);
        SpeichernUnterActionListener speichernUnterListener = new SpeichernUnterActionListener();
        remaspView.getjMenuItemSpeichernUnter().addActionListener(speichernUnterListener);
        DruckenActionListener druckenListener = new DruckenActionListener();
        remaspView.getjMenuItemDrucken().addActionListener(druckenListener);
        remaspView.getButtonDrucken().addActionListener(druckenListener);
        BeendenActionListener beendenListener = new BeendenActionListener();
        remaspView.getjMenuItemBeenden().addActionListener(beendenListener);

        remaspView.getjMenuItemAusschneiden().addActionListener(new DefaultEditorKit.CutAction());
        remaspView.getjMenuItemKopieren().addActionListener(new DefaultEditorKit.CopyAction());
        remaspView.getjMenuItemEinfuegen().addActionListener(new DefaultEditorKit.PasteAction());
        remaspView.getjMenuItemRueckgaengig().addActionListener(new RueckgaengigListener());
        this.remaspView.getjMenuItemRueckgaengig().setEnabled(false);

        //Formatierungs Optionen
        JMenu menu = this.remaspView.getjMenuFormat();
        Action action = new StyledEditorKit.BoldAction();
        action.putValue(Action.NAME, "Fett");
        menu.add(action);

        action = new StyledEditorKit.ItalicAction();
        action.putValue(Action.NAME, "Kursiv");
        menu.add(action);

        action = new StyledEditorKit.UnderlineAction();
        action.putValue(Action.NAME, "Unterstrichen");
        menu.add(action);

/*      
        menu.addSeparator();
        menu.add(new StyledEditorKit.FontSizeAction("12", 12));
        menu.add(new StyledEditorKit.FontSizeAction("14", 14));
        menu.add(new StyledEditorKit.FontSizeAction("18", 18));
        menu.add(new StyledEditorKit.FontSizeAction("21", 21));
        menu.add(new StyledEditorKit.FontSizeAction("24", 24));
        
        */

        menu.addSeparator();

        menu.add(new StyledEditorKit.FontFamilyAction("Serif",
                "Serif"));
        menu.add(new StyledEditorKit.FontFamilyAction("SansSerif",
                "SansSerif"));

        menu.addSeparator();

        menu.add(new StyledEditorKit.ForegroundAction("Rot",
                Color.red));
        menu.add(new StyledEditorKit.ForegroundAction("Grün",
                Color.green));
        menu.add(new StyledEditorKit.ForegroundAction("Blau",
                Color.blue));
        menu.add(new StyledEditorKit.ForegroundAction("Schwarz",
                Color.black));

    }

    protected class MyUndoableEditListener
            implements UndoableEditListener {

        public void undoableEditHappened(UndoableEditEvent e) {
            //Remember the edit and update the menus.
            undo.addEdit(e.getEdit());
            textHasChanged = true;
            remaspView.getjMenuItemRueckgaengig().setEnabled(true);

        }
    }

    class RueckgaengigListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            try {
                if (undo.canUndo()) {
                    undo.undo();
                    textHasChanged = true;
                } else {
                    remaspView.getjMenuItemRueckgaengig().setEnabled(false);
                    textHasChanged = false;
                }

            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
        }
    }

    class BeendenActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (textHasChanged) {
                int auswahl = JOptionPane.showConfirmDialog(remaspView, "Dokument wurde noch nicht gespeichert.\nWollen Sie speichern?");
                switch (auswahl) {
                    case 0://Ja wurde ausgewählt
                        if (!currentFileName.equals("Unbenannt")) {
                            saveFile(currentFileName);
                        } else {
                            saveFileAs();
                        }

                        break;
                    case 1://Nein wurde ausgewählt
                        break;
                    case 2:// Abbrechen wurde gedrückt
                        return;
                }

            }

            System.exit(0);
        }

    }

    class DruckenActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            JTextPane jTextPane = remaspView.getjTextPane();
            try {
                jTextPane.print();
            } catch (PrinterException ex) {
                Logger.getLogger(RemaspEditorController.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(jTextPane, "Das aktuelle Dokument konnte leider nicht gedruckt werden.");
            }

        }

    }

    class SpeichernUnterActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            saveFileAs();
            textHasChanged = false;
        }

    }

    class SpeichernAcitonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!currentFileName.equals("Unbenannt")) {
                saveFile(currentFileName);
            } else {
                saveFileAs();
            }
            textHasChanged = false;
        }

    }

    class NeuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (textHasChanged) {
                int auswahl = JOptionPane.showConfirmDialog(remaspView, "Dokument wurde noch nicht gespeichert.\nWollen Sie speichern?");
                switch (auswahl) {
                    case 0://Ja wurde ausgewählt
                        if (!currentFileName.equals("Unbenannt")) {
                            saveFile(currentFileName);
                        } else {
                            saveFileAs();
                        }

                        break;
                    case 1://Nein wurde ausgewählt
                        break;
                    case 2:// Abbrechen wurde gedrückt
                        return;
                }

            }
            remaspView.getjTextPane().setText("");
            currentFileName = "Unbenannt";
            remaspView.setTitle(currentFileName + " - ReMaSp");
            undo.discardAllEdits();
            remaspView.getjMenuItemRueckgaengig().setEnabled(false);
            textHasChanged = false;

        }
    }

    class OeffnenActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (textHasChanged) {
                int auswahl = JOptionPane.showConfirmDialog(remaspView, "Dokument wurde noch nicht gespeichert.\nWollen Sie speichern?");
                switch (auswahl) {
                    case 0://Ja wurde ausgewählt
                        if (!currentFileName.equals("Unbenannt")) {
                            saveFile(currentFileName);
                        } else {
                            saveFileAs();
                        }

                        break;
                    case 1://Nein wurde ausgewählt
                        break;
                    case 2:// Abbrechen wurde gedrückt
                        return;
                }

            }

            JFileChooser dialog = remaspView.getjFileChooser();
            if (dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                readInFile(dialog.getSelectedFile().getAbsolutePath());

                doc = (AbstractDocument) remaspView.getjTextPane().getStyledDocument();
                doc.addUndoableEditListener(new MyUndoableEditListener());
                undo.discardAllEdits();
                remaspView.getjMenuItemRueckgaengig().setEnabled(false);
                textHasChanged = false;
            }

        }
    }

    private void saveFileAs() {
        JFileChooser dialog = remaspView.getjFileChooser();
        if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveFile(dialog.getSelectedFile().getAbsolutePath());
            textHasChanged = false;
        }
    }

    private void saveFile(String fileName) {
        try {
            FileWriter w = new FileWriter(fileName);
            remaspView.getjTextPane().write(w);
            w.close();
            currentFileName = fileName;
            remaspView.setTitle(currentFileName + " - ReMaSp");
            textHasChanged = false;
        } catch (IOException e) {
        }

    }

    private void readInFile(String fileName) {
        try {
            FileReader r = new FileReader(fileName);
            remaspView.getjTextPane().read(r, null);
            r.close();
            currentFileName = fileName;
            remaspView.setTitle(currentFileName + " - ReMaSp");

        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(remaspView, "Remasp kann die folgende Datei nicht finden:  " + fileName);
        }
    }
}
