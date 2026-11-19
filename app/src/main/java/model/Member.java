package model;

public class Member extends Pengguna {
    public Member(int id, String nama){
        super(id, nama);
    }
    
    @Override
    public String getPeran(){
        return "Member";
    }
    
    @Override
    public Makanan buatMakanan(String nama, String porsi, double kalori, double protein, double lemak, double karbo){
        return new MakananCustom(0, nama, porsi, kalori, protein, lemak, karbo, getId());
    }
}
