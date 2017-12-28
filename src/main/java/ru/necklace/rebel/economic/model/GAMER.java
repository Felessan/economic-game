package ru.necklace.rebel.economic.model;

import org.apache.commons.codec.StringDecoder;

import java.io.BufferedReader;
import java.io.FileReader;

public class GAMER {
    String nameGamer; //имя игрока
    double startMany; //фиксированное стартовое количество финансов. Загрузить из фаила?
    double manyGames; //финансы на конец хода
    double stTov; //стоимость купленного товара - вынести в другой класс подсчет?


    public static double many (double startMany, double stTov)
    {
        double manyGames = startMany+stTov+prirFin();
        return manyGames;
    }

    public static double prirFin()   //цикл впихнуть в структуру хода,
    {
        double a = 0;     //if (фаил пуст, не нажата галочка "прирост финансов") то а=0
     /*   BufferedReader bufferedReader = new BufferedReader(new FileReader("prirFin.txt"));   //считать коэффициэнт прироста из фаила, уточнить формат фаила
        a= Integer.parseInt(bufferedReader.readLine());   */
     return a;
    }


    
}
       //с методами установки текущей налички, и списания/зачисления при покупке/продаже товара