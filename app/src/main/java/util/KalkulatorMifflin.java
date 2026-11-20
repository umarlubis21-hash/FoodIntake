package util;

public class KalkulatorMifflin implements PenghitungKalori {

    @Override
    public String getNama() {
        return "Mifflin-St Jeor";
    }

    @Override
    public double hitungBmr(char gender, double berat, double tinggi, int usia) {
        double dasar = (10 * berat) + (6.25 * tinggi) - (5 * usia);
        return (gender == 'L' || gender == 'l') ? dasar + 5 : dasar - 161;
    }
}