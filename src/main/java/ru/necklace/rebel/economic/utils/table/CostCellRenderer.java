package ru.necklace.rebel.economic.utils.table;

import ru.necklace.rebel.economic.data.ItemInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class CostCellRenderer extends JLabel implements TableCellRenderer {
    Hashtable<String, Long> baseCostMap = new Hashtable();
    private ImageIcon increaseIcon;
    private ImageIcon decreaseIcon;

    public CostCellRenderer(ArrayList<ItemInfo> items) {
        this.increaseIcon = createImageIcon("icons" + File.separator + "increase.png");
        this.decreaseIcon = createImageIcon("icons" + File.separator + "decrease.png");
        Iterator i$ = items.iterator();

        while(i$.hasNext()) {
            ItemInfo item = (ItemInfo)i$.next();
            this.baseCostMap.put(item.getItemName(), item.getBaseCost());
        }

    }

    public Component getTableCellRendererComponent(JTable table, Object cellData, boolean isSelected, boolean hasFocus, int row, int column) {
        Component defaultComponent = (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, cellData, isSelected, hasFocus, row, column);
        JLabel label = new JLabel();
        if (table.getModel() instanceof MarketTableModel && column == 2) {
            try {
                Long newCost = Long.parseLong((String)cellData);
                Long oldCost = (Long)this.baseCostMap.get(table.getModel().getValueAt(row, 0));
                if (newCost.longValue() > oldCost.longValue()) {
                    label.setBackground(Color.GREEN);
                    label.setFont(defaultComponent.getFont());
                    label.setText((String)cellData);
                    label.setIcon(this.increaseIcon);
                    label.setOpaque(true);
                    return label;
                }

                if (newCost.longValue() < oldCost.longValue()) {
                    label.setBackground(Color.RED);
                    label.setFont(defaultComponent.getFont());
                    label.setText((String)cellData);
                    label.setIcon(this.decreaseIcon);
                    label.setOpaque(true);
                    return label;
                }
            } catch (NumberFormatException var11) {
                ;
            }
        }

        return defaultComponent;
    }

    protected static ImageIcon createImageIcon(String path) {
        try {
            return new ImageIcon(path);
        } catch (Exception var2) {
            return null;
        }
    }
}
