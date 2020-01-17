package academy.learnprogramming;

import java .awt.* ;
import java.awt.event.* ;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;

class DebetException extends Exception {
    int i;
    DebetException(int i){
        this.i = i;
    }
}

class Konto implements Serializable {
    private int stan;
    private ArrayList<Integer> poprzedni = new ArrayList<Integer>();
    Konto() {
        stan = 0;
    }
    public void operacja(int ile) throws DebetException {
        if (stan + ile >= 0 ){
            poprzedni.add(stan);
            stan += ile;
        }
        else
            throw new DebetException(-(stan + ile));
    }
    public int dajStan() {
        return stan ;
    }
    public void cofnij() {
        if(poprzedni.size() > 0){
            stan = poprzedni.get(poprzedni.size() - 1);
            poprzedni.remove(poprzedni.size() - 1);
        }
    }
}

public class KontoInterface extends JFrame{
    JTextField
            stanT = new JTextField(20),
            operacjaT = new JTextField(20),
            rezultatT = new JTextField(20);
    Konto konto = new Konto();



    JButton
            operacjaB = new JButton("wplata/wyplata"),
            odblokujB = new JButton("odblokuj"),
            cofnijB = new JButton("cofnij"),
            zapiszB = new JButton("zapisz"),
            wczytajB = new JButton("wczytaj");

    KontoInterface(){
        setTitle("Konto bankowe");
        Container cp = getContentPane();
        cp.setLayout(new GridLayout(5,2,10,10));
        cp.add(new JLabel("stan: "));
        cp.add(stanT);
        operacjaB.addActionListener(new operB());
        odblokujB.addActionListener(new odblB());
        cofnijB.addActionListener(new cofnijB());
        zapiszB.addActionListener(new zapiszB());
        wczytajB.addActionListener(new wczytajB());
        cp.add(operacjaB);
        cp.add(operacjaT);
        cp.add(rezultatT);
        cp.add(odblokujB);
        cp.add(cofnijB);
        cp.add(zapiszB);
        cp.add(wczytajB);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    int dajLiczbe(JTextField tf){
        try{
            return Integer.parseInt(tf.getText());
        } catch(NumberFormatException e){
            rezultatT.setText("blad, przyjmuje tylko liczby typu int");
            return 0;
        }
    }

    class operB implements ActionListener{
        public void actionPerformed(ActionEvent e){
            rezultatT.setText("ok");
            try{
                konto.operacja(dajLiczbe(operacjaT));
            } catch(DebetException x){
                rezultatT.setText("o " + x.i + " za duzo");
            }
            operacjaB.setEnabled(false);
            operacjaT.setEnabled(false);
            stanT.setText(Integer.toString(konto.dajStan()));
        }
    }

    class odblB implements ActionListener{
        public void actionPerformed(ActionEvent e){
            operacjaB.setEnabled(true);
            operacjaT.setEnabled(true);
        }
    }

    class cofnijB implements ActionListener{
        public void actionPerformed(ActionEvent e){
                konto.cofnij();
                rezultatT.setText("cofnieto");
                stanT.setText(Integer.toString(konto.dajStan()));
        }
    }

    class zapiszB implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try{
                FileOutputStream f = new FileOutputStream("dane");
                ObjectOutputStream os = new ObjectOutputStream(f);
                os.writeObject(konto);
                f.close();
            }catch (IOException ex){}

            rezultatT.setText("zapisano");

        }
    }

    class wczytajB implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try{
                ObjectInputStream is = new ObjectInputStream(new FileInputStream("dane"));
                konto = (Konto)is.readObject();
                is.close();
            } catch (IOException ex){
                System.out.println("wyjatek");
            } catch (ClassNotFoundException exc){}

            stanT.setText(Integer.toString(konto.dajStan()));
            rezultatT.setText("wczytano");
        }
    }
    public static void main(String[] arg){
        JFrame a = new KontoInterface();
        a.setSize(600,600);
    }
}