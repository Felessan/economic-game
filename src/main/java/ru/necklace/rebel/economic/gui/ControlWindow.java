package ru.necklace.rebel.economic.gui;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import ru.necklace.rebel.economic.data.CommandInfo;
import ru.necklace.rebel.economic.model.EconomicSituation;
import ru.necklace.rebel.economic.data.ItemInfo;
import ru.necklace.rebel.economic.utils.table.CommandTableModel;
import ru.necklace.rebel.economic.utils.table.MarketTableModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ControlWindow extends JFrame implements ActionListener, TableModelListener {
    private int fontSize = 20;
    private ImageIcon cancelIcon;
    private ImageIcon okIcon;
    private ImageIcon chooseInputDataIcon;
    private ImageIcon loadInputDataIcon;
    private ImageIcon nextIcon;
    private ImageIcon previousIcon;
    private ImageIcon finishIcon;
    private JButton openButton;
    private JButton runGameButton;
    private JButton changeInputButton;
    private JButton prevTurnButton;
    private JButton nextTurnButton;
    private JButton finishGameButton;
    private JFileChooser fc;
    private JTextField choosedFilePathField;
    private JComboBox fontSizeChooser;
    //static Logger logger = LogManager.getLogger(ControlWindow.class);
    private EconomicSituation gameLogic;
    private InfoWindow infoWindow;

    public ControlWindow(EconomicSituation gameTurnListener) {
        super("Экономическая игра - управление");
        this.cancelIcon = createImageIcon("icons" + File.separator + "cancel.png");
        this.okIcon = createImageIcon("icons" + File.separator + "check.png");
        this.chooseInputDataIcon = createImageIcon("icons" + File.separator + "choose.png");
        this.loadInputDataIcon = createImageIcon("icons" + File.separator + "load.png");
        this.nextIcon = createImageIcon("icons" + File.separator + "next.png");
        this.previousIcon = createImageIcon("icons" + File.separator + "previous.png");
        this.finishIcon = createImageIcon("icons" + File.separator + "finish.png");
        this.openButton = new JButton("Выбрать файл", this.chooseInputDataIcon);
        this.runGameButton = new JButton("Загрузить данные из файла", this.loadInputDataIcon);
        this.changeInputButton = new JButton("Настроить добычу");
        this.prevTurnButton = new JButton("Вернуться на ход", this.previousIcon);
        this.nextTurnButton = new JButton("Совершить ход", this.nextIcon);
        this.finishGameButton = new JButton("Завершить игру", this.finishIcon);
        this.choosedFilePathField = new JTextField(80);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        fontSizeChooser = new JComboBox(new Integer[]{6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
        26, 28, 30, 32, 36, 40, 44});
        this.gameLogic = gameTurnListener;
        this.initGui();
    }

    private void initGui() {
        JPanel contentPane = new JPanel(new GridBagLayout());
        JPanel fileLoaderPane = new JPanel(new BorderLayout());
        this.fc = new JFileChooser();
        this.fc.setCurrentDirectory(new File("."));
        this.fc.setFileFilter(this.getXlsFileFilter());
        this.fc.setAcceptAllFileFilterUsed(false);
        this.openButton.addActionListener(this);
        this.openButton.setFont(new Font(this.openButton.getFont().getName(), Font.BOLD, this.fontSize));
        this.runGameButton.setEnabled(false);
        this.runGameButton.setFont(new Font(this.runGameButton.getFont().getName(), Font.BOLD, this.fontSize));
        this.runGameButton.addActionListener(this);
        fontSizeChooser.setFont(new Font(fontSizeChooser.getFont().getName(), Font.BOLD, fontSize));
        fontSizeChooser.setSelectedItem(fontSize);
        fontSizeChooser.addActionListener(this);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.openButton);
        buttonPanel.add(this.runGameButton);
        buttonPanel.add(fontSizeChooser);
        this.choosedFilePathField.setFont(new Font(this.choosedFilePathField.getFont().getName(), Font.BOLD, this.fontSize));
        fileLoaderPane.add(buttonPanel, "First");
        fileLoaderPane.add(this.choosedFilePathField, "Center");
        fileLoaderPane.setBorder(BorderFactory.createEtchedBorder());
        contentPane.add(fileLoaderPane, new GridBagConstraints(2, 0, 2, 2, 1.0D, 1.0D, 11, 2, new Insets(0, 0, 0, 0), 0, 0));
        JPanel controlPane = new JPanel(new FlowLayout());
        this.prevTurnButton.setFont(new Font(this.prevTurnButton.getFont().getName(), Font.PLAIN, this.fontSize));
        this.prevTurnButton.setEnabled(false);
        this.prevTurnButton.addActionListener(this);
        controlPane.add(this.prevTurnButton);
        this.nextTurnButton.setFont(new Font(this.nextTurnButton.getFont().getName(), Font.PLAIN, this.fontSize));
        this.nextTurnButton.addActionListener(this);
        this.nextTurnButton.setEnabled(false);
        controlPane.add(this.nextTurnButton);
        this.finishGameButton.setFont(new Font(this.finishGameButton.getFont().getName(), Font.PLAIN, this.fontSize));
        this.finishGameButton.setEnabled(false);
        this.finishGameButton.addActionListener(this);
        controlPane.add(this.finishGameButton);
        this.changeInputButton.addActionListener(this);
        this.changeInputButton.setEnabled(false);
        this.changeInputButton.setFont(new Font(this.changeInputButton.getFont().getName(), Font.BOLD, this.fontSize));
        controlPane.add(this.changeInputButton);
        controlPane.setBorder(BorderFactory.createEtchedBorder());
        contentPane.add(controlPane, new GridBagConstraints(0, 3, 4, 1, 1.0D, 1.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
        this.setContentPane(contentPane);
        //this.setPreferredSize(new Dimension(1024, 600));
        //this.setResizable(false);
    }

    protected FileFilter getXlsFileFilter() {
        return new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String extension = null;
                    String s = file.getName();
                    int i = s.lastIndexOf(46);
                    if (i > 0 && i < s.length() - 1) {
                        extension = s.substring(i + 1).toLowerCase();
                    }

                    return extension != null && extension.equals("xls");
                }
            }

            public String getDescription() {
                return ".xls Files";
            }
        };
    }

    protected static ImageIcon createImageIcon(String path) {
        try {
            return new ImageIcon(path);
        } catch (Exception var2) {
            //logger.error("Ошибка загрузки файла " + path + " : " + var2.getMessage());
            return null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.openButton) {
            int returnVal = this.fc.showOpenDialog(this);
            if (returnVal == 0) {
                this.choosedFilePathField.setText(this.fc.getSelectedFile().getPath());
                this.runGameButton.setEnabled(true);
            }
        } else if (e.getSource() == this.runGameButton) {
            try {
                this.gameLogic.load(this.choosedFilePathField.getText());
                this.infoWindow = new InfoWindow(this, this, this.gameLogic, (Integer) fontSizeChooser.getSelectedItem());
                this.infoWindow.setVisible(true);
                this.infoWindow.showMarket(this.gameLogic.getMarket());
                this.infoWindow.showPlayers(this.gameLogic.getCommands());
                this.infoWindow.pack();
                this.infoWindow.setLocationRelativeTo(this);
                this.nextTurnButton.setEnabled(true);
                this.finishGameButton.setEnabled(true);
                this.changeInputButton.setEnabled(true);
            } catch (IOException var3) {
                //logger.error("Ошибка при загрузке файла " + this.choosedFilePathField.getText() + ":" + var3.getMessage());
                JOptionPane.showMessageDialog(this, "Ошибка при загрузке файла с установками игры. Загрузите другой или проверьте этот!", "Ошибка установок", 0);
            }
        } else if (e.getSource().equals(this.nextTurnButton)) {
            this.gameLogic.nextStep();
            this.prevTurnButton.setEnabled(true);
            this.infoWindow.showMarket(this.gameLogic.getMarket());
            this.infoWindow.showPlayers(this.gameLogic.getCommands());
        } else if (e.getSource().equals(this.prevTurnButton)) {
            this.gameLogic.prevStep();
            this.infoWindow.showMarket(this.gameLogic.getMarket());
            this.infoWindow.showPlayers(this.gameLogic.getCommands());
        } else if (e.getSource().equals(this.finishGameButton)) {
            System.exit(0);
        } else if (e.getSource().equals(this.changeInputButton)) {
            (new ChangeInputWindow(this, this.gameLogic, (Integer) fontSizeChooser.getSelectedItem())).setVisible(true);
        }
        else if (e.getSource().equals(fontSizeChooser)){
            openButton.setFont(new Font(openButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            runGameButton.setFont(new Font(runGameButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            changeInputButton.setFont(new Font(changeInputButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            prevTurnButton.setFont(new Font(prevTurnButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            nextTurnButton.setFont(new Font(nextTurnButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            finishGameButton.setFont(new Font(finishGameButton.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            choosedFilePathField.setFont(new Font(choosedFilePathField.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            fontSizeChooser.setFont(new Font(fontSizeChooser.getFont().getName(), Font.BOLD, (Integer) fontSizeChooser.getSelectedItem()));
            validate();
            pack();
            repaint();
            if (infoWindow != null) {
                infoWindow.dispose();
                infoWindow.setVisible(false);
                this.infoWindow = new InfoWindow(this, this, this.gameLogic, (Integer) fontSizeChooser.getSelectedItem());
                this.infoWindow.setVisible(true);
                this.infoWindow.showMarket(this.gameLogic.getMarket());
                this.infoWindow.showPlayers(this.gameLogic.getCommands());
                this.infoWindow.pack();
                this.infoWindow.setLocationRelativeTo(this);
            }
        }

    }

    public void tableChanged(TableModelEvent event) {
        TableModel model = (TableModel)event.getSource();
        int row;
        int column;
        int data;
        if (model instanceof MarketTableModel) {
            row = event.getFirstRow();
            column = event.getColumn();

            try {
                data = Integer.parseInt((String)model.getValueAt(row, column));
                switch(column) {
                    case 1:
                        ((ItemInfo)this.gameLogic.getMarket().get(row)).setAvailable((long)data);
                        break;
                    case 2:
                        ((ItemInfo)this.gameLogic.getMarket().get(row)).setCost((long)data);
                        break;
                    case 3:
                        ((ItemInfo)this.gameLogic.getMarket().get(row)).setSold(Math.min((long)data, ((ItemInfo)this.gameLogic.getMarket().get(row)).getAvailable()));
                        break;
                    case 4:
                        ((ItemInfo)this.gameLogic.getMarket().get(row)).setIncome((long)data);
                }

                this.infoWindow.showMarket(this.gameLogic.getMarket());
            } catch (NumberFormatException var8) {
                model.setValueAt("", row, column);
            }
        } else if (model instanceof CommandTableModel) {
            row = event.getFirstRow();
            column = event.getColumn();

            try {
                data = Integer.parseInt((String)model.getValueAt(row, column));
                CommandInfo command = (CommandInfo)this.gameLogic.getCommands().get(row);
                switch(column) {
                    case 1:
                        command.setCurrentCash((long)data);
                        break;
                    case 2:
                        if ((long)data <= command.getCurrentCash() + command.getIncome()) {
                            command.setExpenditure((long)data);
                            command.setProbableCash(command.getCurrentCash() + command.getIncome() - command.getExpenditure());
                        } else {
                            JOptionPane.showMessageDialog(this.infoWindow, "Невозможно потратить денег больше, чем есть на балансе, траты приведены к максимальным", "Невозможно потратить!", 0);
                            command.setExpenditure(command.getCurrentCash() + command.getIncome());
                            command.setProbableCash(0L);
                        }
                        break;
                    case 3:
                        command.setIncome((long)data);
                        command.setProbableCash(command.getCurrentCash() + command.getIncome() - command.getExpenditure());
                }

                this.infoWindow.showPlayers(this.gameLogic.getCommands());
            } catch (NumberFormatException var7) {
                model.setValueAt("", row, column);
            }
        }

    }
}
