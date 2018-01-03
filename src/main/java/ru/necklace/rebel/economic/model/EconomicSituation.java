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

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EconomicSituation implements Serializable{
    int step = 0;
    private ArrayList<ItemInfo> items = new ArrayList<>();
    private ArrayList<CommandInfo> commands = new ArrayList<>();
    private Coefficients coefficients;
    private ArrayList<EconomicSituation> history = new ArrayList<>();
    private Map<String, Integer> itemsIncomeCorrection = new HashMap<>();
    private String gameName;

    public EconomicSituation(Coefficients coefficientsMap, String gameName) {
        this.coefficients = coefficientsMap;
        this.gameName = gameName;
    }

    public EconomicSituation(Coefficients coefficientsMap) {
        this.coefficients = coefficientsMap;
        this.gameName = "TestGame";
    }

    public ArrayList<ItemInfo> getMarket() {
        return this.items;
    }

    public ArrayList<CommandInfo> getCommands() {
        return this.commands;
    }

    public Map<String, Integer> getItemsIncomeCorrection() {
        return this.itemsIncomeCorrection;
    }
    /**
     * Загрузка начальной ситуации из файла
     * @param dataPath - путь до файла с исходными данными
     * @throws IOException - А вдруг файл не найден?
     */
    public void load(String dataPath) throws IOException {
        this.items = new ArrayList<>();
        this.commands = new ArrayList<>();
        Workbook workbook = new HSSFWorkbook(new FileInputStream(dataPath));
        Sheet sheet1 = workbook.getSheetAt(0);
        boolean allItemsRead = false;
        Iterator rowIterator = sheet1.iterator();

        while(rowIterator.hasNext()) {
            //Перебираем строчки
            Row row = (Row)rowIterator.next();
            if (row.getRowNum() != 0) {
                if (allItemsRead) {
                    //Если строка не первая и у нас взведён флаг, что прочитаны все товары, принудительно выходим из цикла
                    break;
                }
                //Если строка не первая, но флаг прочитанности всех товаров ещё не взведён, перебираем ячейки в строке
                ItemInfo addingItem = new ItemInfo(); //заводим новый товар
                Iterator cellIterator = row.iterator(); //заводим итератор - переборщик - для ячеек в строке

                while(cellIterator.hasNext()) {//Пока есть следующие ячейки, работаем

                    Cell cell = (Cell)cellIterator.next();
                    //Берём ячейку
                    if (cell.getColumnIndex() == 0) {
                        //Если номер в строке - 0, это название товара
                        if (cell.getCellType() != 1) {
                            //Если тип ячейки не 1, считаем, что ячейка пустая и товары все прочитаны.
                            allItemsRead = true;
                        } else {
                            //Если тип ячейки всё таки 1, считаем, что это название товара
                            addingItem.setItemName(cell.getRichStringCellValue().getString());
                        }
                    } else if (cell.getColumnIndex() == 1) {
                        //Если номер в строке - 1, это количество на рынке - пробуем преобразовать в число и
                        // подставить начальное количество и добычу в первом раунде
                        try {
                            addingItem.setAvailable(Math.round(cell.getNumericCellValue()));
                            addingItem.setIncome(Math.round((double)addingItem.getAvailable() * 0.2D));
                        } catch (NumberFormatException var15) {
                            //logger.error("Неверно указан параметр \"Доступно\" для " + addingItem.getItemName());
                        }
                    } else if (cell.getColumnIndex() == 2) {
                        //Если номер в строке 2 - это начальная цена. Аналогично пробуем преобразовать в число
                        try {
                            addingItem.setCost(Math.round(cell.getNumericCellValue()));
                        } catch (NumberFormatException var14) {
                            //logger.error("Неверно указан параметр \"Цена\" для " + addingItem.getItemName());
                        }
                    }
                    //нужно поднять флаг по пустым ячейкам, его нет)) Короче надо учесть
                }

                //Добавляем свежезагруженный из файла товар в список товаров
                this.items.add(addingItem);
                //Устанавливаем товару ручную коррекцию имеющегося количества 0 - костыль!
                this.itemsIncomeCorrection.put(addingItem.getItemName(), 0);
            }
        }

        //Берём второй лист эксельника
        Sheet sheet2 = workbook.getSheetAt(1);
        boolean allCommandsRead = false;
        Iterator commandRowIterator = sheet2.iterator();

        //На втором листе - команды, их читаем аналогично - итератором построчно
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

        //Не забываем закрыть файл после чтения, чтобы не сломать его
        workbook.close();
    }

    /**
     * Метод перехода к следующему ходу
     */
    public void nextStep() {
        //Увеличиваем номер хода, возможно - опять костыль
        //TODO перенести в dev
        //В новой архитектуре костылём не будет
        ++this.step;

        //Костыльным образом делаем слепок текущей рыночной ситуации
        EconomicSituation prevTurnSituation = new EconomicSituation(this.coefficients, this.gameName);

        //Перебираем все товары на рынке
        Iterator i$ = this.items.iterator();

        while(i$.hasNext()) {
            ItemInfo item = (ItemInfo)i$.next();
            //Костыльным образом делаем новый товар, частично дублирующий информацию из старого
            ItemInfo updatedItem = new ItemInfo();
            //В новом слепке информации о товаре проставляем прирост товара исходя из количества проданного за ход,
            //указанного в старой информации о товаре
            if ((double)item.getSold() > this.coefficients.getCoefficient(PropertiesName.EXTRA_LARGE_SALES) * (double)item.getBaseAvailable()) {
                updatedItem.setIncome(Math.round((double)item.getBaseIncome() * (1.0D + this.coefficients.getCoefficient(PropertiesName.EXTRA_LARGE_INPUT))));
            } else if ((double)item.getSold() > this.coefficients.getCoefficient(PropertiesName.LARGE_SALES) * (double)item.getBaseAvailable()) {
                updatedItem.setIncome(Math.round((double)item.getBaseIncome() * (1.0D + this.coefficients.getCoefficient(PropertiesName.LARGE_INPUT))));
            } else {
                updatedItem.setIncome(item.getBaseIncome());
            }

            //Добавляем в прирост ручные корректировки, осуществлённые на этом ходу
            updatedItem.setIncome(updatedItem.getIncome() + (long) (Integer) this.itemsIncomeCorrection.get(item.getItemName()));
            //Трём данные о ручных корректировках
            this.itemsIncomeCorrection.put(item.getItemName(), 0);
            //Устанавливаем новое значение доступного количества товара
            updatedItem.setAvailable(item.getAvailable() + updatedItem.getIncome() - item.getSold());
            //Устанавливаем цену на следующем раунде
            updatedItem.setCost(Math.round((double)item.getCost() * (1.0D + this.coefficients.getCoefficient(PropertiesName.COST_ENLARGE) * (double)(item.getBaseAvailable() - updatedItem.getAvailable()) / (double)item.getBaseAvailable())));
            //В непонятно, зачем нужный слепок ситуации предыдущего хода записываем ЕЩЁ ОДНУ копию информации о товаре
            prevTurnSituation.getMarket().add(item.getCopy());
            //Переставляем данные из нового хода в текущий
            item.setSold(0L);
            item.setAvailable(updatedItem.getAvailable());
            item.setIncome(updatedItem.getIncome());
            item.setCost(updatedItem.getCost());
        }

        //Товары перебрали, перебираем команды
        i$ = this.commands.iterator();

        while(i$.hasNext()) {
            CommandInfo command = (CommandInfo)i$.next();
            CommandInfo oldCommandState = new CommandInfo();
            //Делаем копию текущего состояния команды
            oldCommandState.setCommandName(command.getCommandName());
            oldCommandState.setCurrentCash(command.getCurrentCash());
            oldCommandState.setExpenditure(command.getExpenditure());
            oldCommandState.setIncome(command.getIncome());
            oldCommandState.setProbableCash(command.getProbableCash());
            //Сохраняем сделанную копию в старый слепок
            prevTurnSituation.getCommands().add(oldCommandState);
            //в текущем значении команды устанавливаем новые значения - которые рассчитывались прямо в процессе хода, похоже
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

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public int getStep() {
        return step;
    }
}
