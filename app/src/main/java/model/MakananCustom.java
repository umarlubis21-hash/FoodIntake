package model;

public class MakananCustom extends Makanan{
    private final int pemilikId;
    
    public MakananCustom(int id, String nama, String porsi, double kalori, double protein, double lemak, double karbo, int pemilikId){
        super(id, nama, porsi, kalori, protein, lemak, karbo);
        this.pemilikId = pemilikId;
    }
    
    @Override
    public String getSumber(){
        return "Buatan Anda";
    }
    
    @Override
    public Integer getPemilikId(){
        return pemilikId;
    }
}
