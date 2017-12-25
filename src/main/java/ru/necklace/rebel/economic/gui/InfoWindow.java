package ru.necklace.rebel.economic.gui;

import ru.necklace.rebel.economic.data.CommandInfo;
import ru.necklace.rebel.economic.model.EconomicSituation;
import ru.necklace.rebel.economic.data.ItemInfo;
import ru.necklace.rebel.economic.utils.table.CommandTableModel;
import ru.necklace.rebel.economic.utils.table.CostCellRenderer;
import ru.necklace.rebel.economic.utils.table.MarketTableModel;
import ru.necklace.rebel.economic.utils.table.TableColumnAdjuster;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.util.ArrayList;

public class InfoWindow extends JDialog {
    private JPanel marketPanel;
    private JPanel playerPanel;
    private TableModelListener contorller;

    public InfoWindow(JFrame parentFrame, ControlWindow controlWindow1, EconomicSituation gameLogic) {
        super(parentFrame);
        this.contorller = controlWindow1;
        this.setDefaultCloseOperation(0);
        this.initGui();
    }

    private void initGui() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, 3));
        this.marketPanel = new JPanel();
        this.marketPanel.setLayout(new FlowLayout());
        contentPane.add(this.marketPanel);
        this.playerPanel = new JPanel();
        this.playerPanel.setLayout(new FlowLayout());
        contentPane.add(this.playerPanel);
        contentPane.setOpaque(true);
        this.setContentPane(contentPane);
    }

    public void showMarket(ArrayList<ItemInfo> items) {
        JTable market = new JTable(new MarketTableModel());
        MarketTableModel model = (MarketTableModel)market.getModel();
        model.load(items);
        market.setFont(new Font(market.getFont().getName(), 0, 20));
        market.getTableHeader().setFont(new Font(market.getFont().getName(), 1, 24));
        market.setDefaultRenderer(String.class, new CostCellRenderer(items));
        market.setAutoResizeMode(0);
        TableColumnAdjuster tca = new TableColumnAdjuster(market);
        tca.adjustColumns();
        model.addTableModelListener(this.contorller);
        this.marketPanel.removeAll();
        this.marketPanel.setLayout(new BorderLayout());
        this.marketPanel.add(market.getTableHeader(), "First");
        this.marketPanel.add(market, "Center");
        this.pack();
        this.repaint();
    }

    public void showPlayers(ArrayList<CommandInfo> commands) {
        JTable players = new JTable(new CommandTableModel());
        CommandTableModel model = (CommandTableModel)players.getModel();
        model.load(commands);
        players.setFont(new Font(players.getFont().getName(), 0, 20));
        players.getTableHeader().setFont(new Font(players.getFont().getName(), 1, 24));
        players.setAutoResizeMode(0);
        TableColumnAdjuster tca = new TableColumnAdjuster(players);
        tca.adjustColumns();
        model.addTableModelListener(this.contorller);
        this.playerPanel.removeAll();
        this.playerPanel.setLayout(new BorderLayout());
        this.playerPanel.add(players.getTableHeader(), "First");
        this.playerPanel.add(players, "Center");
        this.pack();
        this.repaint();
    }
}
