package model;

public class Admin extends Pengguna {
    public Admin(int id, String nama){
        super(id, nama);
    }
    
    @Override
    public String getPeran(){
        return "Admin";
    }
    
    @Override
    public Makanan buatMakanan(String nama, String porsi, double kalori, double protein, double lemak, double karbo){
        return new MakananBawaan(0, nama, porsi, kalori, protein, lemak, karbo);
    }
}
