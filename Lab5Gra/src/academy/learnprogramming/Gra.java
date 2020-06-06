package academy.learnprogramming;

import  java.io.*;

class Stan{
    private int ciemnosc = 0;
    private int x ; // pozycja biezaca
    private int y ;
    private int popX ;  // pozycja poprzednia
    private int popY ;
    void wPrawo(){ popX = x; popY=y; x++;  }
    void wLewo(){ popX = x;  popY=y; x--;  }
    void wGore(){ popY = y; popX=x;  y++;  }
    void wDol(){ popY = y;  popX=x; y--;   }
    private int punkty ;  // punktacja
    void setPunkty(int punkty){
        this.punkty = punkty;
    }
    int getPunkty(){
        return this.punkty;
    }
    void sciemnij(int x){
        this.ciemnosc = x;
    }
    boolean koniec = false ;  // koniec = true gdy osiagnieto
    // pole wyjsciowe i gracz chce skonczyc
    int x() { return x ; }
    int y() { return y ; }
    void wroc() { x=popX ; y = popY ; } // powrot na poprzednia pozycje
    String opis() {
        if(ciemnosc > 0) return "(?,?)  "+punkty+"punktow\n";
        else return "("+x+","+y+")  "+punkty+"punktow\n" ;}
    // podaje: x,y, punkty
    Stan(int xPocz, int yPocz, int pPocz){
        x = xPocz ; y = yPocz ; punkty=pPocz;
    }
}

abstract class Pole{
    static BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));
    static char czytajZnak(){
        //  czyta jeden znak z klawiatury, pomija znak konca linii
        int c = '\0';
        try{
            String linia = br.readLine();   //czytanie 1 linijki z klawiatury
            if (linia.length()>0) c = linia.charAt(0) ; // pobranie jednego znaku
        }catch (IOException e){}
        return (char)c;
    }

    abstract String komentarz() ;
    // daje napis zawierajacy komentarz charakterystyczny dla danego pola

//    String koment;                  <----------------------------------

    void ruch(Stan s){
        // "ruch" modyfikuje stan s zgodnie z regulami danego pola
        if(s.getPunkty() <= 0){
            System.out.println("przegrales");
            s.koniec = true;
        }
        else {
            System.out.print(komentarz()) ;
//        System.out.print(koment);     <------------------------------------------------------
        System.out.println(s.opis()) ;
        System.out.print(" jaki ruch? [g,d,l,p] ") ;
        switch (czytajZnak()) {
            case 'g' : s.wGore(); break;
            case 'd' : s.wDol(); break;
            case 'l' : s.wLewo(); break;
            case 'p' : s.wPrawo(); break;
        }
        }
    }
}

class Sciana extends Pole{
// wypisuje komentarz i wraca na poprzednie miejsce, odejmuje jeden punkt
  String komentarz(){ return "sciana! "; }
//  String koment = "sciana";   <--------------------------------------------------
  void ruch(Stan s){
      s.setPunkty(s.getPunkty() - 1);
      s.wroc();
      super.ruch(s);
      //zmniejsz punkty
  }
}

class ZwyklePole extends Pole{
    // jak Pole, ponadto powinna odejmowac jeden punkt
    String komentarz() { return " zwykle pole" ; }
    void ruch(Stan s){
        s.setPunkty(s.getPunkty() - 1);
        super.ruch(s);
    }
}


class Wyjscie extends Pole{
// oferuje mozliwosc zakonczenia gry, a jezeli nie konczymy, to tak jak Pole
    String komentarz(){ return "powrót na poprzednie pole";}
    void ruch(Stan s){
        System.out.print("znalazles wyjście, zakończyć? [t/n]");
        switch (czytajZnak()) {
            case 't' : s.koniec = true; break;
            case 'n' : s.wroc();
                super.ruch(s);
                break;
        }
    }
}

class PolePremia extends Pole{
    String komentarz(){ return "dostales premie (+3p)";}
    void ruch(Stan s){
        s.setPunkty(s.getPunkty() + 3);
        super.ruch(s);
    }
}

class Sciemnij extends Pole{
    String komentarz(){return "ale ciemno";}
    void ruch(Stan s){
        s.sciemnij(3);
        super.ruch(s);
    }
}

class Gra{
    public static void main(String[] a){
        // inicjalizacja "jaskini"
        int i,j ;
        int rozmiar = 10 ;
        Pole[][] jaskinia = new Pole[rozmiar][rozmiar] ;
        for (i=0; i<rozmiar ; i++)
            for (j=0; j<rozmiar ; j++){
                   if( i == j) jaskinia[i][j] = new PolePremia();
                   else if ( i == 0 || i == rozmiar-1 || j == 0 || j == rozmiar-1)
                       jaskinia[i][j] = new Sciana() ;
                   else
                jaskinia[i][j] = new ZwyklePole() ;
            }
        jaskinia[5][3] = new Wyjscie();
        jaskinia[3][7] = jaskinia[7][3] = new Sciemnij();
        // gra wlasciwa
        Stan s = new Stan(4,8,10) ;
        while (!s.koniec) {
            (jaskinia[s.x()][s.y()]).ruch(s) ;
        }
    }
}

