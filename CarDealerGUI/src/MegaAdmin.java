import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MegaAdmin extends JFrame implements ActionListener, ItemListener {

    Connection con;
    String[] tables = {"account - login", "address - street", "branch - id_branch", "car - brand", "color - r", "customer - id_customer",
                    "employee - id_employee", "finished_transaction - id_ftransaction", "model - model_name", "personal_info - second_name", "planned_transaction - stance"};
    String[] x;

    JComboBox<String> cb;

    JButton btSel, btIns, btUpd, btDel;

    JTextField tfDel, tfIns, tfUpd;

    //labels
    private JLabel lbInf, lbSel, lbIns, lbUpd ,lbDel;
    //** parametry okna

    MegaAdmin(){
        setSize(300, 400);
        setLocation(500, 300);
        setResizable(false);
        setTitle("Admin interface");
        setLayout(null);

        lbInf = Creator.newLabel(120,110,"",this);
        lbInf.setForeground(Color.red);

        cb = Creator.newComboBox(10,10, 200, tables, this);
        cb.addItemListener(this);

        btSel = Creator.newButton(10,110, "select", this);
        btSel.addActionListener(this);
        btDel = Creator.newButton(10,160,"delete", this);
        btDel.addActionListener(this);
        tfDel = Creator.newTextField(120,160, this);
        btIns = Creator.newButton(10,210,"insert", this);
        btIns.addActionListener(this);
        tfIns = Creator.newTextField(120,210,this);
        btUpd = Creator.newButton(10,260, "update", this);
        btUpd.addActionListener(this);
        tfUpd = Creator.newTextField(120,260,this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String table = x[0];
        String field = x[2];

        if(e.getSource() == btSel){
            selectTable(table);
        }
        if (e.getSource() == btDel){
            deleteFrom(table, field);
        }
        if (e.getSource() == btUpd){
            updateTable(table, field);
        }

    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        x = itemEvent.getItem().toString().split(" ");
    }

    private void selectTable(String table){
        JFrame f=new JFrame();
        List<String[]> lista=new ArrayList<String[]>();
        String tab[][];

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            //Zapytanie SQL
            Statement zapytanie2 = con.createStatement();
            String sql2="select * from " + table;
            ResultSet wynik_sql_2 = zapytanie2.executeQuery(sql2);
            ResultSetMetaData wynik_kol = wynik_sql_2.getMetaData();
            int ile_kolumn = wynik_kol.getColumnCount();
            String[] columns = new String[ile_kolumn];
            //pobranie wybranych kolumn do jednej listy
            while(wynik_sql_2.next()) {
                String[] t = new String[ile_kolumn];
                for (int i = 0 ; i < ile_kolumn; i ++){
                    columns[i] = "" +(i+1);
                    t[i] = wynik_sql_2.getString(i+1);
                }
                lista.add(t);
            }
            //konwersja listy do tablicy na potrzeby JTable
            String array[][]=new String[lista.size()][];
            for (int i=0;i<array.length;i++){
                String[] row=lista.get(i);
                array[i]=row;
            }
            zapytanie2.close();

            //"ręczne" wprowadzenie nazw kolumn
            //wygenerowanie tabeli
            JTable jt1=new JTable(array,columns);
            JScrollPane sp=new JScrollPane(jt1);
            f.add(sp);
            f.setLocation(200,50);
            f.setSize(300,400);
            f.setVisible(true);
            lista.clear();
            System.out.println(lista.toString());

            con.close();

        }
        catch(SQLException error_polaczenie) {
            System.out.println("Błąd połączenia z bazą danych");}
        catch(ClassNotFoundException error_sterownik) {
            System.out.println("Brak sterownika");}
    }

    private void deleteFrom(String table, String field){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "DELETE FROM "+ table +
                    " WHERE "+field+" = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1,tfDel.getText());
            statement.executeUpdate();
            statement.close();
            lbInf.setText("");
        } catch (SQLException connectionError) {
            lbInf.setText("nie mozna usunac powiazania");
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }
    }

    private void updateTable(String table, String field){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "UPDATE "+table+
                    " SET "+field+" = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, tfUpd.getText());
            statement.executeUpdate();
            statement.close();
            lbInf.setText("");
        } catch (SQLException connectionError) {
            lbInf.setText("nie mozna edytowac");
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }
    }

}