public class Node {
    int[] k; //klucze
    int t; // stopien
    Node[] c; //synowie
    int n; //ilosc kluczy
    boolean isLeaf;

    public Node(int t, boolean isLeaf) {
        this.t = t;
        this.isLeaf = isLeaf;
        this.k = new int[2*t-1];
        this.c = new Node[2*t];
        this.n = 0;
    }

    public void print(int p){
        int i, j;
        if(isLeaf){
            for(i = 0; i < p; i++){
                System.out.print(" ");
            }
            for(i = 0; i < n; i++){
                System.out.print(k[i] + " ");
            }
            System.out.println();
        }
        else{
            c[n].print(p+4);
            for(i = n-1; i >= 0; i--){
                for(j = 0; j < p; j++){
                    System.out.print(" ");
                }
                System.out.println(k[i]);
                c[i].print(p+4);
            }
        }
    }

    Result search(int key){
        int i = 0;
        while(i < n && key > k[i]) {
            i++;
        }
        if(k[i] == key){
            Result result = new Result(this, i);
            return result;
        }
        if(isLeaf){
            return null;
        }
        //nie znaleziono klucza, wezel nie jest lisciem - zejdz nizej
        return c[i].search(key);
    }

    public void split(int a, Node node){
        Node x = new Node(node.t, node.isLeaf);
        x.n = t-1;

        for(int i = 0; i < t-1; i++){
            x.k[i] = node.k[i+t];
        }

        if(!node.isLeaf){
            for(int i = 0; i < t; i++){
                x.c[i] = node.c[i+t];
            }
        }

        node.n = t - 1;

        for(int i = n; i >= a+1; i--){
            c[i+1] = c[i];
        }

        c[a+1] = x;

        for(int i = n-1; i >= a; i--){
            k[i+1] = k[i];
        }

        k[a] = node.k[t-1];
        n++;
    }

    public void insertNotComplete(int key){
        int i = n-1;

        if(isLeaf){
            while(i >= 0 && k[i] > key){
                k[i+1] = k[i];
                i--;
            }

            k[i+1] = key;
            n++;
        }
        else{
            while(i >= 0 && k[i] > key){
                i--;
            }
            if(c[i+1].n == 2*t-1){
                split(i+1, c[i+1]);
                if(k[i+1] < key){
                    i++;
                }
            }
            c[i+1].insertNotComplete(key);
        }
    }
}
