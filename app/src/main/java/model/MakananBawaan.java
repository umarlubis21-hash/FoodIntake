package model;

public class MakananBawaan extends Makanan{
    
    public MakananBawaan(int id, String nama, String porsi, double kalori, double protein, double lemak, double karbo){
        super(id, nama, porsi, kalori, protein, lemak, karbo);
    }
    
    @Override
    public String getSumber(){
        return "Katalog";
    }
    
    @Override
    public Integer getPemilikId(){
        return null;
    }
}
