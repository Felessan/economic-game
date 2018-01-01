package ru.necklace.rebel.economic.model;

public class TOVAR {
    String nameTov;
    int startColvoTov;
    int colvoTov;    //количество товара на рынке
    double tsenaTov;
    int colvoPocupTov; //вводится игроком
    double tsenaPokupTov;
    double tsenaTekush;

    public TOVAR(String nameTov, int startColvoTov, double tsenaTov) {
        this.nameTov = nameTov;
        this.startColvoTov = startColvoTov;
        this.tsenaTov = tsenaTov;
    }

    public static double StoimostTov(double tsenaTov, double tsenaPokupTov, int colvoPocupTov) {
        tsenaPokupTov = colvoPocupTov*tsenaTov;
        return tsenaPokupTov;
    }      //рассчет цены товара во время покупки.
           //количество покупаемого товара вводится игроком - где?? Предположительно отдельный метод

    public static int colvoTovFin(int startColvoTov, int colvoTov, int colvoPocupTov){
        colvoTov=startColvoTov-colvoPocupTov;
        return colvoTov;
    }

    public static double tsenaTovOne(double tsenaTov, int startColvoTov, int colvoTov) {
        int k=0; //коэффициент из фаила
        tsenaTov = tsenaTov*(1+k)*(startColvoTov-colvoTov)/startColvoTov;  //k - коэффициент из фаила
        //новая цена = старая цена * (1 + коэффициент * (начальное количество на рынке - текущее количество на рынке) / начальное количество на рынке
        return tsenaTov;
    }
}
