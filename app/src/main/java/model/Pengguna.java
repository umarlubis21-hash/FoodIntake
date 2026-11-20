package model;

public abstract class Pengguna {
    private final int id;
    private final String nama;
    
    protected Pengguna(int id, String nama){
        this.id = id;
        this.nama = nama;
    }
    
    public int getId(){
        return id;
    }
    
    public String getNama(){
        return nama;
    }
    
    public abstract String getPeran();
    
    public abstract Makanan buatMakanan(String nama, String porsi, double kalori, double protein, double lemak, double karbo);
    
    public String getSapa(){
        return "Halo, " + nama + " (" + getPeran() + ")";
    }
}