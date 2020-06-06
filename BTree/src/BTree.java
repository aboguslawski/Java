public class BTree {
    Node root;
    int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    public void print(int p){
        if(this.root != null){
            this.root.print(p);
        }
        System.out.println();
    }

    public Result search(int key){
        if(this.root == null){
            return null;
        }
        else{
            return this.root.search(key);
        }
    }

    public void insert(int key){
        if(root == null){
            root = new Node(t, true);
            root.k[0] = key;
            root.n = 1;
        }
        else{
            if(root.n == 2*t-1){
                Node s = new Node(t, false);
                s.c[0] = root;
                s.split(0, root);
                int i = 0;
                if(s.k[0] < key){
                    i++;
                }
                s.c[i].insertNotComplete(key);
                root = s;
            }
            else{
                root.insertNotComplete(key);
            }
        }
    }
}
