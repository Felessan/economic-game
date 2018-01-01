package ru.necklace.rebel.economic.model;

public class ONE {
    public static void main(){
        //рынок

        //метод "добавить товар", реализует нижеуказанное:
        TOVAR tovar1 = new TOVAR("сало", 30, 10);
        TOVAR tovar2 = new TOVAR("мясо", 30, 10);
        System.out.println(tovar1.tsenaTov);

        //метод "добавить игрока", реализует добавление и высчет нижеуказанного:
        GAMER gamer1 = new GAMER("Саша", 30000);
    }
}
     //читать массивы и коллекции!!!!