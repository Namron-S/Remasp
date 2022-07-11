/*
Copyright (c) 2017, Norman Sutatyo
All rights reserved.

This source code is licensed under the BSD-style license found in the
LICENSE file in the root directory of this source tree. 
 */
package remasp.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomCellRenderer extends DefaultTableCellRenderer {

    /**
     * @return the zuFaerbendeZeile
     */
    public int getZuFaerbendeZeile() {
        return zuFaerbendeZeile;
    }

    /**
     * @param zuFaerbendeZeile the zuFaerbendeZeile to set
     */
    public void setZuFaerbendeZeile(int zuFaerbendeZeile) {
        this.zuFaerbendeZeile = zuFaerbendeZeile;
    }

    //zuFaerbendeZeile=-1 bedeutet, dass keine Zeile gefärbt werden soll.
    private int zuFaerbendeZeile = -1;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
        cell.setForeground(Color.black);

        if (row == this.zuFaerbendeZeile) {
            cell.setBackground(Color.yellow);
        } else if (row % 2 == 0 && row != this.zuFaerbendeZeile) {
            cell.setBackground(Color.white);
        } else if (row % 2 == 1 && row != this.zuFaerbendeZeile) {
            cell.setBackground(Color.lightGray);
        }
        return cell;
    }
}
