package util;

public class KalkulatorHarrisBennedict implements PenghitungKalori {

    @Override
    public String getNama() { 
        return "Harris-Benedict"; 
    }

    @Override
    public double hitungBmr(char gender, double berat, double tinggi, int usia) {
        if (Character.toUpperCase(gender) == 'L') {
            return 88.362 + 13.397 * berat + 4.799 * tinggi - 5.677 * usia;
        }
        return 447.593 + 9.247 * berat + 3.098 * tinggi - 4.330 * usia;
    }

    @Override
    public String toString() { 
        return getNama(); 
    }
}