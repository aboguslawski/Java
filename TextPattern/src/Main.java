import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static char[] pattern;// = {'a', 'a'};
    static char[] text;// = {'d', 'a', 'a', 'a', 'a', 'd'};
    static ArrayList<String> patternPosition = new ArrayList<>();
    static ArrayList<String> textPosition = new ArrayList<>();
    static long start;
    static float naiveTime1, rkTime1, kmpTime1, naiveTime2, rkTime2, kmpTime2;

    public static void main(String[] args) {
        //--------------------------------------------------------pierwszy przyklad
        try{
            pattern = readFile("pattern1.txt", patternPosition);
            text = readFile("text1.txt", textPosition);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\nNaive:");
        start = System.currentTimeMillis();
        naive(pattern, text);
        naiveTime1 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + naiveTime1);

        System.out.println("\nRabin-Karp algorithm:");
        start = System.currentTimeMillis();
        rabinKarp(pattern, text, 101);
        rkTime1 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + rkTime1);

        System.out.println("\nKnuth-Morris-Pratt algorithm:");
        start = System.currentTimeMillis();
        kmp(pattern,text);
        kmpTime1 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + kmpTime1);

        //--------------------------------------------------------drugi przyklad
        try{
            pattern = readFile("pattern2.txt", patternPosition);
            text = readFile("text2.txt", textPosition);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        System.out.println("\nNaive:");
        start = System.currentTimeMillis();
        naive(pattern, text);
        naiveTime2 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + naiveTime2);

        System.out.println("\nRabin-Karp algorithm:");
        start = System.currentTimeMillis();
        rabinKarp(pattern, text, 101);
        rkTime2 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + rkTime2);

        System.out.println("\nKnuth-Morris-Pratt algorithm:");
        start = System.currentTimeMillis();
        kmp(pattern,text);
        kmpTime2 = (System.currentTimeMillis() - start)/1000F;
        System.out.println("time: " + kmpTime2);

        //----------------------------------------------------wynik
        System.out.println("\n\npattern 1 text 1 times:" +
                "\nNaive: \t" + naiveTime1 +
                "\nR-K: \t" + rkTime1 +
                "\nK-M-P: \t" + kmpTime1);
        System.out.println("\npattern 2 text 2 times:" +
                "\nNaive: \t" + naiveTime2 +
                "\nR-K: \t" + rkTime2 +
                "\nK-M-P: \t" + kmpTime2);
    }


    static void naive(char[] p, char[] t){
        int m = p.length;
        int n = t.length;
        boolean x;

        for(int i = 0; i < n-m; i++){
            x = true;
            for(int j = 0; j < m; j++){
                if( p[j] != t[i+j]) x = false;
            }
            if(x) System.out.println(textPosition.get(i));
        }
    }

    static void rabinKarp(char[]p, char[]t, int q){
        int m = p.length;
        int n = t.length;
        int ph = 0;
        int th = 0;
        int h = 1;
        int d = 256;
        int i, j;

        for(i = 0; i < m-1; i++){
            h = (h*d)%q;
        }

        for(i = 0; i < m; i++){
            ph = (d*ph + p[i])%q;
            th = (d*th + t[i])%q;
        }

        for(i = 0; i <= n - m; i++){
            if(ph == th){
                for(j = 0; j < m; j++){
                    if(t[i+j] != p[j]){
                        break;
                    }
                }
                if(j == m){
                    System.out.println(textPosition.get(i));
                }
            }
            if(i < n-m){
                th = (d * (th - t[i] * h) + t[i+m]) % q;
                if(th < 0){
                    th = th + q;
                }
            }
        }
    }

    static void prefixFunction(char[] p, int m, int pi[]){
        int len = 0;
        int i = 1;
        pi[0] = 0;

        while(i < m){
            if(p[i] == p[len]){
                len++;
                pi[i] = len;
                i++;
            }
            else{
                if(len != 0){
                    len = pi[len - 1];
                }
                else{
                    pi[i] = len;
                    i++;
                }
            }
        }
    }

    static void kmp(char[] p, char[] t){
        int m = p.length;
        int n = t.length;

        int pi[] = new int[m];
        int j = 0, i = 0;

        prefixFunction(p, m, pi);

        while(i < n){
            if(p[j] == t[i]){
                i++;
                j++;
            }
            if(j == m){
                System.out.println(textPosition.get(i - j));
                j = pi[j - 1];
            }
            else if(i < n && p[j] != t[i]){
                if(j != 0){
                    j = pi[j-1];
                }
                else{
                    i++;
                }
            }
        }


    }

    static char[] readFile(String path, ArrayList<String> position) throws FileNotFoundException {
        ArrayList<Character> values = new ArrayList<>();
        String str = "", line;
        int lineCount = 1;
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            for(int j = 0; j < line.length(); j++){
                position.add("line " + lineCount + ", char " + (j+1));
            }
            str += line;
            lineCount++;
        }

        return str.toCharArray();
    }
}
