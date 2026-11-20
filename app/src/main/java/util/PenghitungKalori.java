package util;

public interface PenghitungKalori {
    String getNama();
    
    double hitungBmr(char gender, double berat, double tinggi, int usia);
    
    default double hitungTdee(char gender, double berat, double tinggi, int usia, double faktorAktivitas){
        return hitungBmr(gender, berat, tinggi, usia) * faktorAktivitas;
    }
}