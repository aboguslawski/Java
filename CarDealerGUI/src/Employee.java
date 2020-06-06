import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Employee extends JFrame implements ActionListener, ItemListener {
    Connection con;
    private JOptionPane byeMessage;
    private Account account;
    private JComboBox<String> cbTransaction;
    private List<String[]> list = new ArrayList<String[]>();
    private String[] transactions = loadTransactions(),
            columns = {"id", "branch", "car", "price", "seller", "buyer"};
    private String sql = "SELECT planned_transaction.id_ptransaction, address.city, " +
            "CONCAT(car.brand,' ',model.model_name,' ',model.production_year), " +
            "car.price, sa.login as 'seller', ba.login as 'buyer'  " +
            "FROM car\n" +
            "INNER JOIN planned_transaction ON car.id_car = planned_transaction.fk_car " +
            "INNER JOIN customer as sCust ON sCust.id_customer = car.fk_seller " +
            "INNER JOIN customer as bCust ON bCust.id_customer = planned_transaction.fk_buyer " +
            "INNER JOIN personal_info AS sPI ON sPI.id_pinfo = sCust.fk_pinfo " +
            "INNER JOIN personal_info AS bPI ON bPI.id_pinfo = bCust.fk_pinfo " +
            "INNER JOIN account AS sa ON sa.id_account = sPI.fk_account " +
            "INNER JOIN account AS ba ON ba.id_account = bPI.fk_account " +
            "INNER JOIN model ON model.id_model = car.fk_model  " +
            "INNER JOIN branch ON branch.id_branch = car.fk_branch  " +
            "INNER JOIN address ON address.id_address = branch.fk_address " +
            "WHERE stance != 'finished' " +
            "ORDER BY planned_transaction.id_ptransaction";
    private String id, first, second, brand, price;

    private JButton btQuit, btLogout, btTransaction;
    private JLabel lbQuit, lbWork, lbInfo;
    private JPanel panel, up, mid, down;
    private JSplitPane sp1, sp2;

    Employee(Account acc) {
        this.account = acc;
        //** parametry okna

        setSize(300, 500);
        setLocation(800, 300);
        setResizable(false);
        setTitle(acc.getUsername());

        //** panele

        up = new JPanel();
        up.setLayout(null);
        up.setPreferredSize(new Dimension(300, 90));
        up.setBackground(new Color(120, 160, 145));
        //podzial
        mid = new JPanel();
        mid.setPreferredSize(new Dimension(300, 315));

        down = new JPanel();
        down.setLayout(null);
        down.setBackground(new Color(120, 160, 145));

        sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, up, mid);
        sp1.setDividerSize(0);
        sp2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sp1, down);
        sp2.setDividerSize(0);

        add(sp2);
        transactions = loadTransactions();
        loadTransactionTable();
        //** komponenty

        btLogout = Creator.newButton(10, 10, "logout", down);
        btLogout.addActionListener(this);
        btQuit = Creator.newButton(175, 10, "quit job", down);
        btQuit.addActionListener(this);
        cbTransaction = Creator.newComboBox(10, 10, 260, transactions, up);
        cbTransaction.addItemListener(this);

        btTransaction = Creator.newButton(40, 50, 200, "finish transaction", up);
        btTransaction.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btLogout) {
            Login login = new Login();
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            login.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
        if (e.getSource() == btQuit) {
            deleteEmployee();
            JOptionPane.showMessageDialog(this,"bye","bye",JOptionPane.PLAIN_MESSAGE);
            Login login = new Login();
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            login.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
        if (e.getSource() == btTransaction) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection("jdbc:sqlserver://" +
                        "localhost:1433;databaseName=ab_cardealer;" +
                        "user=aboguslawski;password=Adam531;");
                String sql = "INSERT INTO finished_transaction (fk_transaction) VALUES\n" +
                        "(( SELECT id_ptransaction  \n" +
                        "FROM planned_transaction \n" +
                        "INNER JOIN car ON id_car = fk_car \n" +
                        "INNER JOIN customer ON id_customer = fk_seller \n" +
                        "INNER JOIN personal_info ON id_pinfo = fk_pinfo\n" +
                        "WHERE stance != 'finished' AND price = "+price+" AND brand = '"+brand+"'\n" +
                        "AND first_name = '"+first+"' AND second_name = '"+second+"'))";
                PreparedStatement statement = con.prepareStatement(sql);
                statement.executeUpdate();
                statement.close();

                sql = "UPDATE planned_transaction\n" +
                        "SET stance = 'finished' " +
                        "WHERE id_ptransaction IN " +
                        "(SELECT id_ptransaction  \n" +
                        "FROM planned_transaction \n" +
                        "INNER JOIN car ON id_car = fk_car \n" +
                        "INNER JOIN customer ON id_customer = fk_seller \n" +
                        "INNER JOIN personal_info ON id_pinfo = fk_pinfo\n" +
                        "WHERE stance != 'finished' AND price = "+price+" AND brand = '"+brand+"'\n" +
                        "AND first_name = '"+first+"' AND second_name = '"+second+"')";

                PreparedStatement statement2 = con.prepareStatement(sql);
                statement2.executeUpdate();
                statement2.close();

                sql = "UPDATE car " +
                        "SET availability = 'sold' " +
                        "WHERE id_car IN " +
                        "(SELECT id_car FROM car " +
                        "INNER JOIN  planned_transaction ON id_car = fk_car " +
                        "WHERE stance = 'finished' )";

                PreparedStatement statement3 = con.prepareStatement(sql);
                statement3.executeUpdate();
                statement3.close();
                System.out.println("halo");

            } catch (SQLException connectionError) {
                System.out.println("Connection error");
            } catch (ClassNotFoundException driverError) {
                System.out.println("Driver error");
            }

            loadTransactionTable();
//            cbTransaction.setVisible(false);
//            cbTransaction = Creator.newComboBox(10,10, 260,loadTransactions(),up);
        }
    }

    private void loadTransactionTable() {
        mid.removeAll();
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                String[] t = new String[countColumns];
                for (int i = 0; i < countColumns; i++) {
                    t[i] = resultSet.getString(i + 1);
                }
                list.add(t);
            }
            String array[][] = new String[list.size()][];
            for (int i = 0; i < array.length; i++) {
                String[] row = list.get(i);
                array[i] = row;
            }
            statement.close();
            JTable jt = new JTable(array, columns);
            jt.setRowHeight(30);
            JScrollPane sp = new JScrollPane(jt);
            sp.setPreferredSize(new Dimension(275, 310));
            sp.setVisible(true);
            mid.add(sp);
            mid.doLayout();
            list.clear();
            con.close();
        } catch (SQLException connectionError) {
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }
    }

    private String[] loadTransactions() {
        Connection connection;

        ArrayList<String> trans = new ArrayList<>();
        String[] output;
        String car, seller;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = connection.createStatement();
            String sql = "SELECT car.brand, personal_info.first_name, personal_info.second_name, car.price " +
                    "FROM planned_transaction " +
                    "INNER JOIN car ON id_car = fk_car " +
                    "INNER JOIN customer ON id_customer = fk_seller " +
                    "INNER JOIN personal_info ON id_pinfo = fk_pinfo " +
                    "WHERE stance != 'finished'";

            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();

            while (result.next()) {
                car = result.getString(1) + " " + result.getString(4);
                seller = result.getString(2) + " " + result.getString(3);
                trans.add("s: " + seller + " c: " + car);
            }

            output = new String[trans.size()];

            for (int i = 0; i < output.length; i++) {
                output[i] = trans.get(i);
            }
            return output;

        } catch (SQLException connectionError) {
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }
        return new String[1];
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Object item = e.getItem();
            String[] split = item.toString().split(" ");
            first = split[1];
            second = split[2];
            brand = split[4];
            price = split[5];
            System.out.println("event : " + first + second + brand + price);
        }
    }

    private void deleteEmployee() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "DELETE FROM finished_transaction\n" +
                    "WHERE finished_transaction.fk_employee = (select id_employee from employee\n" +
                    "inner join personal_info on personal_info.id_pinfo = employee.fk_pinfo\n" +
                    "inner join account on account.id_account = personal_info.fk_account\n" +
                    "where account.login = '" + account.getUsername() + "')\n" +
                    "DELETE FROM employee \n" +
                    "WHERE id_employee = (select id_employee from employee\n" +
                    "inner join personal_info on personal_info.id_pinfo = employee.fk_pinfo\n" +
                    "inner join account on account.id_account = personal_info.fk_account\n" +
                    "where account.login = '"+ account.getUsername() +"')\n" +
                    "DELETE FROM personal_info\n" +
                    "where fk_account = (select id_account from account where login = '"+ account.getUsername() +"')\n" +
                    "DELETE FROM account\n" +
                    "WHERE login = '"+ account.getUsername() +"'";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

        } catch (SQLException connectionError) {
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }

    }
}
