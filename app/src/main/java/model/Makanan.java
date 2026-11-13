package model;

public abstract class Makanan {
    private final int id;
    private final String nama;
    private final String porsi;
    private final double kalori;
    private final double protein;
    private final double lemak;
    private final double karbo;
    
    protected Makanan(int id, String nama, String porsi, double kalori, double protein, double lemak, double karbo){
        this.id = id;
        this.nama = nama;
        this.porsi = porsi;
        this.kalori = kalori;
        this.protein = protein;
        this.lemak = lemak;
        this.karbo = karbo;
    }
    
    public int getId(){
        return id;
    }
    
    public String getNama(){
        return nama;
    }
    
    public String getPorsi(){
        return porsi;
    }
    
    public double getKalori(){
        return kalori;
    }
    
    public double getProtein(){
        return protein;
    }
    
    public double getLemak(){
        return lemak;
    }
    
    public double getKarbo(){
        return karbo;
    }
    
    public double hitungKalori(double jumlahPorsi){
        return kalori * jumlahPorsi;
    }
    
    public abstract String getSumber();
    
    public abstract int getPemilikId();
    
    @Override
    public String toString(){
        return nama + " (" + porsi + ") - " + Math.round(kalori) + " kkal [" + getSumber() + "]";
    }
}
