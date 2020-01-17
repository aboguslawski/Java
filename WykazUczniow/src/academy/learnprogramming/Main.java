package academy.learnprogramming;
// klasa testujaca do zadania arrayL1C, tj. testujaca klasy Uczen i WykazU
import java.io.*;
import java.util.*;

class TestWykazU{
    public static void main(String[] a){
        WykazU wu = new WykazU();

        wu.wstawUcznia("K","Kazik");
        wu.wstawUcznia("K1","Kazik");
        wu.wstawUcznia("N","Nikodem");
        wu.wstawUcznia("J","Jan");
        wu.wstawUcznia("W","Wiesiek");

//        wu.wstawOcene("K",5);
//        wu.wstawOcene("K",4);
//        wu.wstawOcene("K",3);
//        wu.wstawOcene("K",5);
//        wu.wstawOcene("K",2);
//
//        wu.wstawOcene("K1",5);
//        wu.wstawOcene("K1",6);
//
//        wu.wstawOcene("N",4);
//        wu.wstawOcene("N",5);
//        wu.wstawOcene("N",4);
//
//        wu.wstawOcene("J",3);
//        wu.wstawOcene("W",5);
        try{
            wu.wstawOcene("K",5);
            wu.wstawOcene("K",4);
            wu.wstawOcene("K",3);
            wu.wstawOcene("K",5);
            wu.wstawOcene("K",2);

            wu.wstawOcene("K1",5);
            wu.wstawOcene("K1",6);

            wu.wstawOcene("N",4);
            wu.wstawOcene("N",5);
            wu.wstawOcene("N",4);

            wu.wstawOcene("J",3);
            wu.wstawOcene("W",5);
            wu.wstawOcene("W", 7);
        } catch (zlaOcena e){
            System.out.println("Przekazano niewlasciwa ocene (" + e.i + ") dla ucznia o ID: " + e.s + ". Nie zostala ona dodana do wykazu.");
        }
        System.out.println("wypisz wykaz:");
        wu.wypisz();
        System.out.println("wypisz kazik:");
        wu.wypisz("K");
        System.out.println("sortuj alfabetycznie i wypisz:");
        wu.sortujA();
        wu.wypisz();
        System.out.println("sortuj wedlug sredniej i wypisz:");
        wu.sortujS();
        wu.wypisz();
        System.out.println("Srednia ocen wszystkich uczniow to: " + wu.srednia());

    }
}

class Uczen{
    String id;
    String imie;
    ArrayList<Integer> oceny = new ArrayList<Integer>();

    Uczen(String id, String imie){
        this.id = id; this.imie = imie;
    }

    double srednia(){
        int sum = 0;
        for(int i = 0; i < oceny.size(); i++){
            sum += oceny.get(i);
        }
        return (double)sum / oceny.size();
    }

    Uczen(){
        this("no_id", "no_name");
    }

    public String toString(){
        return this.id + " - " + this.imie + "[" + this.oceny + " | " + this.srednia() + "]";
    }
}

class zlaOcena extends Exception{
    int i;
    String s;
    zlaOcena(){ super();}
    zlaOcena(String msg){ super(msg);}
    zlaOcena(int i){ this.i=i; }
    zlaOcena(String s, int i){
        this.s = s;
        this.i = i;
    }
}

class WykazU{
    private ArrayList<Uczen> wykaz = new ArrayList<Uczen>();

//    Comparator<Uczen> SortujA = new Comparator<Uczen>() {
//        @Override
//        public int compare(Uczen u1, Uczen u2) {
//            return u1.imie.compareTo(u2.imie);
//        }
//    };

    double srednia(){
        double sum = 0;

        for(Uczen uczen : wykaz){
            sum+=uczen.srednia();
        }

        return sum/wykaz.size();
    }

    void sortujA(){
        Collections.sort(wykaz, (a,b) -> {
          return a.imie.compareTo(b.imie);
        });
    }

    void sortujS(){
        Collections.sort(wykaz, (a,b) -> {
            return Double.compare(a.srednia(), b.srednia());
        });
    }
    WykazU(){
    }

    void wstawUcznia(String id, String imie){
        wykaz.add(new Uczen(id, imie));
    }

    void wstawOcene(String id, int ocena) throws zlaOcena{
        if(ocena > 6 || ocena < 1) throw new zlaOcena(id, ocena);
        for(int i = 0; i<wykaz.size();i++){
                Uczen a = wykaz.get(i);
                if (a.id == id) {
                    a.oceny.add(ocena);
                    i = wykaz.size();
                }
            }
    }

    void wypisz(String id){
        for(int i = 0; i < wykaz.size(); i++){
            Uczen a = wykaz.get(i);
            if(a.id == id){
                System.out.println(a);
                i = wykaz.size();
            }
        }
    }

    void wypisz(){
        System.out.println(wykaz);
    }
}