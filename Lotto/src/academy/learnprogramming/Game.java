package academy.learnprogramming;

import java.io.*;
import java.util.*;

public class Game implements Serializable {
    int[][] numbers = new int[7][7];//tablica cyfr
    char[][] field = new char[7][7];//pola oznaczone jako 'W' - white 'B' - black
    boolean win;
    int moves;
    int maxMoves;//limit dla nastepnego ruchu
    char[][][]history = new char[999][7][7];//poprzedni - nastepny ruch

    //wypelnianie tablicy cyframi z pliku czytanymi przez Scanner
    void fillArray(){
        try{
            FileInputStream fis = new FileInputStream("cyfry2.txt");
            Scanner sc=  new Scanner(fis);
            for(int i = 0; i < 7; i++){
                for(int j = 0 ; j < 7; j++){
                    numbers[i][j] = sc.nextInt();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void saveMove(){
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                history[moves][i][j] = field[i][j];
            }
        }
    }

    //wywolane przez zrobienie ruchu (zamalowanie pola na czarno lub odmalowanie na bialo)
    void makeMove(int i, int j){
        moves++;
        win = true;//ustawiany jest na true. Jesli zagadka nie jest jeszcze rozwiazana, zostanie zmieniony na false
        maxMoves = moves;

        //sprawdza, czy ruch jest zgodny z zasadami. Jesli tak to go wykonuje
        if(field[i][j] == 'W'){
            if(rightToMove(i, j)){
                field[i][j] = 'B';
            }
        }
        else{
            field[i][j] = 'W';
        }

        //jesli znajdzie jeden niepasujacy rzad lub kolumne - gra sie nie skonczyla
        for(int k = 0; k < 7; k++){
            if(!rowWin(k)){
                win = false;
            }
            if(!columnWin(k)){
                win = false;
            }
        }
        saveMove();
    }

    //inicjalizuje wszystkie pola na biale przed wykonaniem pierwszego ruchu
    void setFields(){
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                field[i][j] = 'W';
                history[0][i][j] = 'W';
            }
        }
    }
    //sprawdza czy kolumna k jest rozwiazana
    boolean columnWin(int k){//
        int[] counter = new int[10];
        for(int i = 0; i < 7; i++){
            if(field[i][k] == 'W'){
                counter[numbers[i][k]]++;
            }
        }
        for(int i = 0; i < 10; i++){
            if(counter[i] > 1){
                return false;
            }
        }
        return true;
    }

    //sprawdza czy rzad r jest rozwiazany
    boolean rowWin(int r){// game
        int[] counter = new int[10];
        for(int i = 0; i < 7; i++){
            if(field[r][i] == 'W'){
                counter[numbers[r][i]]++;
            }
        }
        for(int i = 0; i < 10; i++){
            if(counter[i] > 1){
                return false;
            }
        }
        return true;
    }

    //sprawdza czy ruch jest zgodny z zasadami
    boolean rightToMove(int x, int y){//game
        if(x == 0 && y == 0){
            if(field[x][y+1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            return true;
        }
        if(x == 0 && y == 6){
            if(field[x][y-1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            return true;
        }
        if(x == 6 && y == 0){
            if(field[x-1][y] == 'B') return false;
            if(field[x][y+1] == 'B') return false;
            return true;
        }
        if(x == 6 && y == 6){
            if(field[x][y-1] == 'B') return false;
            if(field[x-1][y] == 'B') return false;
            return true;
        }
        if(x == 0){
            if(field[x][y+1] == 'B') return false;
            if(field[x][y-1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            return true;
        }
        if(x == 6){
            if(field[x][y+1] == 'B') return false;
            if(field[x][y-1] == 'B') return false;
            if(field[x-1][y] == 'B') return false;
            return true;
        }
        if(y == 0){
            if(field[x][y+1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            if(field[x-1][y] == 'B') return false;
            return true;
        }
        if(y == 6){
            if(field[x][y-1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            if(field[x-1][y] == 'B') return false;
            return true;
        }
        if(x >= 0 && x <= 6 && y >= 0 && y <= 6){
            if(field[x][y+1] == 'B') return false;
            if(field[x][y-1] == 'B') return false;
            if(field[x+1][y] == 'B') return false;
            if(field[x-1][y] == 'B') return false;
            return true;
        }
        return true;
    }

     void prevMove() {
        if(moves > 0){
            moves--;
            for(int i = 0; i < 7; i++){
                for(int j = 0; j < 7; j++){
                    field[i][j]= history[moves][i][j];
                }
            }
        }
    }

    void nextMove(){
        if(moves < maxMoves){
            moves++;
            for(int i = 0; i < 7; i++){
                for(int j = 0; j < 7; j++){
                    field[i][j]= history[moves][i][j];
                }
            }
        }
    }
}
