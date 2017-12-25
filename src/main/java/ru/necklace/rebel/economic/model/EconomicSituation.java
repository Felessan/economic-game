package ru.necklace.rebel.economic.model;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import ru.necklace.rebel.economic.data.Coefficients;
import ru.necklace.rebel.economic.data.CommandInfo;
import ru.necklace.rebel.economic.data.ItemInfo;
import ru.necklace.rebel.economic.utils.PropertiesName;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EconomicSituation {
    int step = 0;
    private ArrayList<ItemInfo> items = new ArrayList<>();
    private ArrayList<CommandInfo> commands = new ArrayList<>();
    private Coefficients coefficients;
    private ArrayList<EconomicSituation> history = new ArrayList<>();
    private Map<String, Integer> itemsIncomeCorrection = new HashMap<>();

    public Map<String, Integer> getItemsIncomeCorrection() {
        return this.itemsIncomeCorrection;
    }

    public EconomicSituation(Coefficients coefficientsMap) {
        this.coefficients = coefficientsMap;
    }

    public ArrayList<ItemInfo> getMarket() {
        return this.items;
    }

    public ArrayList<CommandInfo> getCommands() {
        return this.commands;
    }

    public void load(String dataPath) throws IOException {
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();
        Workbook workbook = new HSSFWorkbook(new FileInputStream(dataPath));
        Sheet sheet1 = workbook.getSheetAt(0);
        boolean allItemsRead = false;
        Iterator rowIterator = sheet1.iterator();

        while(rowIterator.hasNext()) {
            Row row = (Row)rowIterator.next();
            if (row.getRowNum() != 0) {
                if (allItemsRead) {
                    break;
                }

                ItemInfo addingItem = new ItemInfo();
                Iterator cellIterator = row.iterator();

                while(cellIterator.hasNext()) {
                    Cell cell = (Cell)cellIterator.next();
                    if (cell.getColumnIndex() == 0) {
                        if (cell.getCellType() != 1) {
                            allItemsRead = true;
                        } else {
                            addingItem.setItemName(cell.getRichStringCellValue().getString());
                        }
                    } else if (cell.getColumnIndex() == 1) {
                        try {
                            addingItem.setAvailable(Math.round(cell.getNumericCellValue()));
                            addingItem.setIncome(Math.round((double)addingItem.getAvailable() * 0.2D));
                        } catch (NumberFormatException var15) {
                            //logger.error("Неверно указан параметр \"Доступно\" для " + addingItem.getItemName());
                        }
                    } else if (cell.getColumnIndex() == 2) {
                        try {
                            addingItem.setCost(Math.round(cell.getNumericCellValue()));
                        } catch (NumberFormatException var14) {
                            //logger.error("Неверно указан параметр \"Цена\" для " + addingItem.getItemName());
                        }
                    }
                }

                this.items.add(addingItem);
                this.itemsIncomeCorrection.put(addingItem.getItemName(), 0);
            }
        }

        Sheet sheet2 = workbook.getSheetAt(1);
        boolean allCommandsRead = false;
        Iterator commandRowIterator = sheet2.iterator();

        while(commandRowIterator.hasNext()) {
            Row row = (Row)commandRowIterator.next();
            if (row.getRowNum() != 0) {
                if (allCommandsRead) {
                    break;
                }

                CommandInfo addingCommand = new CommandInfo();
                Iterator cellIterator = row.iterator();

                while(cellIterator.hasNext()) {
                    Cell cell = (Cell)cellIterator.next();
                    if (cell.getColumnIndex() == 0) {
                        if (cell.getCellType() != 1) {
                            allCommandsRead = true;
                        } else {
                            addingCommand.setCommandName(cell.getRichStringCellValue().getString());
                        }
                    } else if (cell.getColumnIndex() == 1) {
                        try {
                            addingCommand.setCurrentCash(Math.round(cell.getNumericCellValue()));
                            addingCommand.setProbableCash(addingCommand.getCurrentCash());
                        } catch (NumberFormatException var13) {
                            //logger.error("Неверно указан параметр \"Баланс\" для " + addingCommand.getCommandName());
                        }
                    }
                }

                if (addingCommand.getCommandName() != null && !addingCommand.getCommandName().equals("")) {
                    this.commands.add(addingCommand);
                }
            }
        }

        workbook.close();
    }

    public void nextStep() {
        ++this.step;
        EconomicSituation prevTurnSituation = new EconomicSituation(this.coefficients);
        Iterator i$ = this.items.iterator();

        while(i$.hasNext()) {
            ItemInfo item = (ItemInfo)i$.next();
            ItemInfo updatedItem = new ItemInfo();
            if ((double)item.getSold() > this.coefficients.getCoefficient(PropertiesName.EXTRA_LARGE_SALES) * (double)item.getBaseAvailable()) {
                updatedItem.setIncome(Math.round((double)item.getBaseIncome() * (1.0D + this.coefficients.getCoefficient(PropertiesName.EXTRA_LARGE_INPUT))));
            } else if ((double)item.getSold() > this.coefficients.getCoefficient(PropertiesName.LARGE_SALES) * (double)item.getBaseAvailable()) {
                updatedItem.setIncome(Math.round((double)item.getBaseIncome() * (1.0D + this.coefficients.getCoefficient(PropertiesName.LARGE_INPUT))));
            } else {
                updatedItem.setIncome(item.getBaseIncome());
            }

            updatedItem.setIncome(updatedItem.getIncome() + (long) (Integer) this.itemsIncomeCorrection.get(item.getItemName()));
            this.itemsIncomeCorrection.put(item.getItemName(), 0);
            updatedItem.setAvailable(item.getAvailable() + updatedItem.getIncome() - item.getSold());
            updatedItem.setCost(Math.round((double)item.getCost() * (1.0D + this.coefficients.getCoefficient(PropertiesName.COST_ENLARGE) * (double)(item.getBaseAvailable() - updatedItem.getAvailable()) / (double)item.getBaseAvailable())));
            prevTurnSituation.getMarket().add(item.getCopy());
            item.setSold(0L);
            item.setAvailable(updatedItem.getAvailable());
            item.setIncome(updatedItem.getIncome());
            item.setCost(updatedItem.getCost());
        }

        i$ = this.commands.iterator();

        while(i$.hasNext()) {
            CommandInfo command = (CommandInfo)i$.next();
            CommandInfo oldCommandState = new CommandInfo();
            oldCommandState.setCommandName(command.getCommandName());
            oldCommandState.setCurrentCash(command.getCurrentCash());
            oldCommandState.setExpenditure(command.getExpenditure());
            oldCommandState.setIncome(command.getIncome());
            oldCommandState.setProbableCash(command.getProbableCash());
            prevTurnSituation.getCommands().add(oldCommandState);
            command.setCurrentCash(command.getProbableCash());
            command.setExpenditure(0L);
            command.setIncome(0L);
        }

        this.history.add(prevTurnSituation);
    }

    public void prevStep() {
        if (this.step > 0) {
            --this.step;
            this.items = ((EconomicSituation)this.history.get(this.history.size() - 1)).getMarket();
            this.commands = ((EconomicSituation)this.history.get(this.history.size() - 1)).getCommands();
            this.history.remove(this.history.size() - 1);
        }

    }
}
