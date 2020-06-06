public class Main {

    public static void main(String[] args) {
        BTree tree = new BTree(3);
        tree.insert(6);
        tree.insert(19);
        tree.insert(17);
        tree.insert(11);
        tree.insert(3);
        tree.insert(12);
        tree.insert(8);
        tree.insert(20);
        tree.insert(22);
        tree.insert(23);
        tree.insert(13);
        tree.insert(18);
        tree.insert(14);
        tree.insert(16);
        tree.insert(1);
        tree.insert(2);
        tree.insert(24);
        tree.insert(25);
        tree.insert(4);
        tree.insert(26);
        tree.insert(5);
        tree.print(0);
        System.out.println();
        System.out.println(tree.search(4).node.k[tree.search(4).k]);
    }
}
