package academy.learnprogramming;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Board extends JFrame {
    Game game1 = new Game();
    JButton[][] field = new JButton[7][7];
    JPanel gamePanel = new JPanel();
    JPanel menuPanel = new JPanel();
    JTextArea information = new JTextArea();
    JTextArea movesT = new JTextArea();
    JButton helpB = new JButton("instrukcja");
    JButton prevB = new JButton("cofnij ruch");
    JButton nextB = new JButton("nastepny ruch");
    JButton saveB = new JButton("zapisz");
    JButton loadB = new JButton("wczytaj");

    //ustawia kolor przyciskow
    void upload() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (game1.field[i][j] == 'W') {
                    field[i][j].setBackground(Color.white);
                } else {
                    field[i][j].setBackground(Color.black);
                }
            }
        }
    }

    Board() {
        game1.fillArray();
        game1.setFields();
        Container cp = getContentPane();
        cp.setLayout(new GridLayout(1, 2, 5, 5));
        cp.add(gamePanel);
        cp.add(menuPanel);
        gamePanel.setLayout(new GridLayout(7, 7, 5, 5));
        menuPanel.setLayout(new GridLayout(7, 1, 5, 5));

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                field[i][j] = new JButton(Integer.toString(game1.numbers[i][j]));
                gamePanel.add(field[i][j]);
                field[i][j].setBackground(Color.white);
                field[i][j].addActionListener(new B(i, j));
            }
        }

        menuPanel.add(information);
        menuPanel.add(movesT);
        menuPanel.add(prevB).setBackground(Color.lightGray);
        prevB.addActionListener(new PrevMove());
        nextB.addActionListener(new NextMove());
        saveB.addActionListener(new SaveToFile());
        loadB.addActionListener(new ReadFromFile());
        menuPanel.add(nextB).setBackground(Color.lightGray);
        menuPanel.add(saveB).setBackground(Color.lightGray);
        menuPanel.add(loadB).setBackground(Color.lightGray);
        menuPanel.add(helpB).setBackground(Color.lightGray);
        helpB.addActionListener(new Help());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    class B implements ActionListener {
        int i;
        int j;

        B(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void actionPerformed(ActionEvent e) {
            game1.makeMove(i, j);
            if (game1.rightToMove(i, j)) {
                information.setText("");
            } else {
                information.setText("niedozwolony ruch");
            }

            if (game1.win) {
                information.setText("wygrana");
            }


            upload();
            movesT.setText("ruchów: " + game1.moves);
        }
    }

    class Help implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String instructions = " Instrukcja:\n" +
                    "    Zaczerń niektóre pola z cyframi, tak aby w każdym rzędzie i w każdej kolumnie \n    występowały różne cyfry oraz aby czarne pola nie stykały się bokami,\n" +
                    "    a stykając się rogami, nie dzieliły diagramu na części.";
            information.setText(instructions);
        }
    }

    class PrevMove implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            game1.prevMove();
            upload();
            movesT.setText("ruchów: " + game1.moves);
            information.setText("cofnieto");
        }
    }

    class NextMove implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            game1.nextMove();
            upload();
            movesT.setText("ruchów: " + game1.moves);
            information.setText("nastepny");
        }
    }

    class SaveToFile implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String filename;
            final JFileChooser fc = new JFileChooser();
            int response = fc.showSaveDialog(Board.this);
            if(response == JFileChooser.APPROVE_OPTION){
                filename = fc.getSelectedFile().toString();
            }
            else{
                filename = "";
            }

            try {
                FileOutputStream f = new FileOutputStream(filename);
                ObjectOutputStream os = new ObjectOutputStream(f);
                os.writeObject(game1);
                f.close();
            } catch (IOException ex) {
            }

            information.setText("zapisano plik " + filename);
        }

    }

    class ReadFromFile implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String filename;
            final JFileChooser fc = new JFileChooser();
            int response = fc.showOpenDialog(Board.this);
            if(response == JFileChooser.APPROVE_OPTION){
                filename = fc.getSelectedFile().toString();
            }
            else{
                filename = "";
            }


            try {
                ObjectInputStream is = new ObjectInputStream(new FileInputStream(filename));
                game1 = (Game) is.readObject();
                is.close();
            } catch (IOException ex) {
                System.out.println("wyjatek");
            } catch (ClassNotFoundException exc) {
            }
            upload();
            information.setText("wczytano plik " + filename);
        }
    }
}
