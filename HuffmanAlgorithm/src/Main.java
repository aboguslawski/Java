// Adam Bogusławski 264926

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

// class Node: określa atrybuty pojedyńczego obiektu
// w tym przypadku jest to odpowiednik struktury w C

class Node {
    // znak występujący w tekście
    char c;
    // ilość jego wystąpień
    int occurences;
    // lewy i prawy syn
    Node left;
    Node right;

    // Konstruktor klasy: określa w jaki sposób przebiega inicjalizacja obiektu
    // przy inicjalizacji przekazywane są parametry, które nadają wartości poszczególnych pól
    Node(char chr, int occr, Node l, Node r){
        // this: prefix "this" zapewnia, że wartości są rozpatrywane w obrębie obecnego obiektu
        // w tym przypadku można go pominąć, jednak dobrą praktyką jest zawsze go umieszczać
        this.c = chr;
        this.occurences = occr;
        this.left = l;
        this.right = r;
    }
}
// główna klasa
public class Main {

    // zmienne globalne
    // static: dostęp do wartości wartości przechowywanych w zmiennych jest w każdym miejscu, zmienna posiada "jedną kopię"

    // sciezka do pliku
    static final String filePath = "src\\input";
    // tablica bitów przyjmowanych z pliku
    // indeks - kod ascii znaku
    // wartosc - ilość wystąpień znaku w pliku
    static int[] ascii = new int[256];
    // kolejka w której są "pudelka" na których operuje algorytm
    static ArrayList<Node> queue = new ArrayList<>();
    // wielkość przed i po kompresji (póki co == 0)
    static int initSize;
    static int compressedSize;

    // wszystko jest wywoływane w funkcji main
    // public: funkcja dostępna w zewnętrznych klasach
    // static: funkcja statyczna
    public static void main(String[] args) {
        readFile(filePath);

        // dodawanie wczytanych danych do kolejki
        System.out.println("\nData: ");
        for(int i = 0; i < ascii.length; i++){
            if(ascii[i] > 0){
                System.out.println((char)i + ": " + ascii[i]);
                queue.add(new Node((char)i, ascii[i], null, null));
            }
        }

        System.out.println("\nCalculations: ");
        Node result = huffman(queue);

        System.out.println("\nOccurences at root node: ");
        System.out.println(result.occurences);

        System.out.println("\nCounting compressed size: ");
        countSize(result, 0);

        System.out.println("\nInitial size: " + initSize + ", compressed size:" + compressedSize);

    }

    // funkcja wczytująca dane z pliku
    private static void readFile(String filePath){
        byte[] file;

        // try, catch: wykrywanie wyjątków (np. gdyby plik nie istniał)
        // zapewnia, że płynność programu będzie nienaruszona bez względu na to czy plik został zmieniony
        try {

            // wczytuje wszystkie bajty do tablicy
            file = Files.readAllBytes(Paths.get("src/input"));
            for (int i = 0; i < file.length; i++){
                ascii[file[i]] ++;
                initSize+=3;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // zwraca obiekt z najmniejszą ilością wystąpień
    private static Node extractMin(ArrayList<Node> q){

        // pierwszym porównaniem jest pierwszy element w kolejce
        Node min = q.get(0);
        Node temp = min;

        for(int i = 0; i < q.size(); i++){
            temp = q.get(i);
            if(temp.occurences < min.occurences){
                min = temp;
            }
        }

        // funkcja zanim zwróci minimalną wartość, najepierw usuwa ten obiekt z kolejki
        System.out.println("min: " + min.occurences);
        q.remove(min);
        return min;
    }

    // glowny algorytm, oparty na pseudokodzie Dr. Pączkowskiego
    private static Node huffman(ArrayList<Node> q){
        Node x, y;

        // pętla działa aż w kolejce zostanie ostatni obiekt, który jest sumą pozostałych
        // z elementów utworzy się kopiec (albo drzewo binarne?)
        while(q.size() > 1){
            x = extractMin(q);
            y = extractMin(q);

            // tworzy nowy obiekt ktory sumuje dwa najmniejsze obiekty wyciągniete z kolejki
            q.add(new Node('-', x.occurences + y.occurences, x, y));
            System.out.println("queue size: " + q.size());
        }
        return extractMin(q);
    }

    // funkcja zliczająca wielkosc po kompresji
    // wywolywana rekurencyjnie, dochodzi az do lisci w ktorych dokonuje zliczania zgodnego z algorytmem
    private static void countSize(Node node, int depth){
        if(node.left != null && node.right != null){
            depth++;
            countSize(node.left, depth);
            countSize(node.right, depth);
        }
        else if(node.right != null){
            depth++;
            countSize(node.right, depth);
        }
        else if(node.left != null){
            depth++;
            countSize(node.left, depth);
        }
        else{
            System.out.println(" " + depth + " * " + node.occurences + " = " + node.occurences * depth + " + ");
            compressedSize += depth * node.occurences;
        }
    }
}