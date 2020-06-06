import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Admin extends JFrame implements ActionListener, ItemListener {
    private Connection con;

    //** glowny podzial
    private JButton btLogout, btReset;
    private JSplitPane sp1, sp2;
    private Color color = new Color(193, 111, 103);

    //** zakladki
    private JPanel pnTabs, pnLeft, pnRight;
    private JButton btEmp, btCus;
    private List<String[]> list = new ArrayList<String[]>();

    //** opcje

    //emp
    private JComboBox<String> cbEmp;
    private JButton btEmpDelete, btEmpRaise, btAdd;
    private JTextField tfEmpRaise;
    private int idEmp;
    private String firstEmp, secondEmp, cityEmp;

    //cus
    private JComboBox<String> cbCus;
    private JButton btCusDelete;
    private int idCus;

    Admin() {
        //** parametry okna

        setSize(800, 600);
        setLocation(500, 300);
        setResizable(false);
        setTitle("Admin interface");

        //** panele
        //zakladki

        pnTabs = new JPanel();
        pnTabs.setLayout(null);
        pnTabs.setPreferredSize(new Dimension(230, 50));

        //tworzenie podzialu

        pnLeft = new JPanel();
        pnLeft.setLayout(null);
        pnLeft.setBackground(color);

        pnRight = new JPanel(new FlowLayout(FlowLayout.LEFT));

        sp1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTabs, pnLeft);
        sp1.setDividerSize(0);
        sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp1, pnRight);
        sp2.setDividerSize(0);

        add(sp2);

        //przyciski
        btCus = Creator.newButton(110, 10, "customers", pnTabs);
        btCus.addActionListener(this);
        btCus.setBackground(Color.lightGray);
        btEmp = Creator.newButton(10, 10, "employees", pnTabs);
        btEmp.addActionListener(this);
        btEmp.setBackground(Color.lightGray);
        btLogout = Creator.newButton(10, 430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);
        btReset = Creator.newButton(110,430,"reset", pnLeft);
        btReset.addActionListener(this);
        btReset.setBackground(Color.darkGray);
        btReset.setForeground(Color.green);

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
        if(e.getSource() == btReset){
            reset();
        }
        if(e.getSource() == btAdd){
            RegisterEmployee add = new RegisterEmployee();
            add.setVisible(true);
        }

        //** zakladka employee -------------------------------------
        if (e.getSource() == btEmp) {
            setEmployeeLayout();
        }
        if(e.getSource() == btEmpDelete){
            deleteEmployee(idEmp);
            setEmployeeLayout();
        }
        if(e.getSource() == btEmpRaise){
            raiseEmployee(tfEmpRaise.getText(), idEmp);
            setEmployeeLayout();
        }

        //** zakladka customer---------------------------------------
        if (e.getSource() == btCus) {
            setCustomerLayout();
        }
        if(e.getSource() == btCusDelete){
            deleteCustomer(idCus);
            setCustomerLayout();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == cbEmp){
            if(e.getStateChange() == ItemEvent.SELECTED){
                Object item = e.getItem();
                String[] content = item.toString().split(" ");
                secondEmp = content[0];
                firstEmp = content[1];
                cityEmp = content[2];
                idEmp = getIdEmp(firstEmp, secondEmp, cityEmp);
            }
        }
        if(e.getSource() == cbCus){
            if(e.getStateChange() == ItemEvent.SELECTED){
                Object item = e.getItem();
                String[] content = item.toString().split(" ");
                idCus = getIdCus(content[1], content[0], content[2]);
            }
        }
    }

    // ************************* LAYOUTS ***********************

    private void setEmployeeLayout() {
        btEmp.setBackground(color);
        btCus.setBackground(Color.lightGray);

        pnLeft.setBackground(Color.white);
        pnLeft.removeAll();
        pnLeft.setBackground(color);

        String[] content = loadEmployee();

        cbEmp = Creator.newComboBox(20, 10, 200, content, pnLeft);
        cbEmp.addItemListener(this);

        btEmpDelete = Creator.newButton(20, 50, "delete", pnLeft);
        btEmpDelete.addActionListener(this);
        btEmpDelete.setBackground(Color.white);

        tfEmpRaise = Creator.newTextField(20, 130, pnLeft);

        btEmpRaise = Creator.newButton(20, 170, "raise", pnLeft);
        btEmpRaise.addActionListener(this);
        btEmpRaise.setBackground(Color.white);

        btAdd = Creator.newButton(20,230,"add",pnLeft);
        btAdd.addActionListener(this);
        btAdd.setBackground(Color.orange);

        String[] t = {"name", "branch", "salary"};
        String sql = "SELECT CONCAT(personal_info.second_name,' ',personal_info.first_name) as 'name',\n" +
                "address.city, employee.salary FROM employee\n" +
                "INNER JOIN personal_info ON personal_info.id_pinfo = employee.fk_pinfo\n" +
                "INNER JOIN branch ON employee.fk_branch = branch.id_branch\n" +
                "INNER JOIN address ON address.id_address = branch.fk_address";

        printTable(sql, t);

        btLogout = Creator.newButton(10, 430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);
        btReset = Creator.newButton(110,430,"reset", pnLeft);
        btReset.addActionListener(this);
        btReset.setBackground(Color.darkGray);
        btReset.setForeground(Color.green);

        pnLeft.doLayout();

    }

    private void setCustomerLayout() {
        btCus.setBackground(color);
        btEmp.setBackground(Color.lightGray);

        pnLeft.setBackground(Color.white);
        pnLeft.removeAll();
        pnLeft.setBackground(color);

        String[] content = loadCustomers();

        cbCus = Creator.newComboBox(20, 10, 200, content, pnLeft);
        cbCus.addItemListener(this);

        btCusDelete = Creator.newButton(20, 50, "delete", pnLeft);
        btCusDelete.addActionListener(this);
        btCusDelete.setBackground(Color.white);

        String[] t = {"name", "city"};
        String sql = "SELECT CONCAT(personal_info.second_name,' ',personal_info.first_name) as 'name',\n" +
                "address.city FROM customer\n" +
                "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo\n" +
                "INNER JOIN address ON address.id_address = personal_info.fk_address";

        printTable(sql, t);
        btLogout = Creator.newButton(10, 430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);
        btReset = Creator.newButton(110,430,"reset", pnLeft);
        btReset.addActionListener(this);
        btReset.setBackground(Color.darkGray);
        btReset.setForeground(Color.green);

        pnLeft.doLayout();

    }

    void printTable(String sql, String[] columns) {
        pnRight.removeAll();
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
            sp.setPreferredSize(new Dimension(540, 550));
            pnRight.add(sp);
            pnRight.doLayout();
            list.clear();
            con.close();
        } catch (SQLException connectionError) {
            System.out.println("Connection error");
        } catch (ClassNotFoundException driverError) {
            System.out.println("Driver error");
        }
    }

    void deleteEmployee(int id){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "DELETE FROM employee\n" +
                    "WHERE id_employee = " + id;
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }

    void deleteCustomer(int id){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "DELETE FROM planned_transaction\n" +
                    "WHERE planned_transaction.fk_car IN (SELECT car.id_car FROM car WHERE car.fk_seller = "+ id +")\n" +
                    "DELETE FROM CAR\n" +
                    "WHERE car.id_car IN (SELECT car.id_car FROM car WHERE car.fk_seller = "+ id +")\n" +
                    "DELETE FROM planned_transaction\n" +
                    "WHERE planned_transaction.fk_buyer = " + id +"\n" +
                    "DELETE FROM customer\n" +
                    "WHERE id_customer = " + id;
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }

    void raiseEmployee(String raise, int id){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "UPDATE employee\n" +
                    "SET salary = salary + " + raise + "\n" +
                    "WHERE id_employee = "+ id;
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }

    private String[] loadCustomers(){
        String[] output;
        ArrayList<String> info = new ArrayList<>();
        String id, name, city;

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = con.createStatement();
            String sql = "SELECT CONCAT(personal_info.second_name,' ',personal_info.first_name) as 'name',\n" +
                    "address.city FROM customer\n" +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo\n" +
                    "INNER JOIN address ON address.id_address = personal_info.fk_address";
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();

            while(result.next()){
                name = result.getString(1);
                city = result.getString(2);
                info.add(name + " " + city);
            }


            output = new String[info.size()];

            for(int i = 0; i < output.length; i++){
                output[i] = info.get(i);
            }
            return output;

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }

        return new String[1];

    }

    private String[] loadEmployee(){
        String[] output;
        ArrayList<String> info = new ArrayList<>();
        String id;
        String name, city;
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = con.createStatement();
            String sql = "SELECT CONCAT(personal_info.second_name,' ',personal_info.first_name) as 'name',\n" +
                    "address.city FROM employee\n" +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = employee.fk_pinfo\n" +
                    "INNER JOIN address ON address.id_address = personal_info.fk_address";
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();

            while(result.next()){
                name = result.getString(1);
                city = result.getString(2);
                info.add(name + " " + city);
            }

            output = new String[info.size()];

            for(int i = 0; i < output.length; i++){
                output[i] = info.get(i);
            }
            return output;

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }

        return new String[1];

    }

    private void reset(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = resetSql();
            PreparedStatement statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }

    private int getIdEmp(String fst, String snd, String ct){
        int id = 0;
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            Statement statement = con.createStatement();
            String sql = "SELECT id_employee FROM employee \n" +
                    "INNER JOIN branch on id_branch = fk_branch\n" +
                    "INNER JOIN address on fk_address = id_address\n" +
                    "INNER JOIN personal_info on fk_pinfo = id_pinfo\n" +
                    "WHERE first_name = '"+fst+"' AND  second_name = '"+snd+"' AND city = '"+ct+"'";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                id = Integer.parseInt(result.getString(1));
            }
        }catch (SQLException c){
            System.out.println("con err");
        }catch (ClassNotFoundException c){
            System.out.println("Driver error");
        }
        return id;
    }

    private int getIdCus(String fst, String snd, String ct){
        int id = 0;
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            Statement statement = con.createStatement();
            String sql = "SELECT id_customer FROM customer \n" +
                    "INNER JOIN personal_info on fk_pinfo = id_pinfo\n" +
                    "INNER JOIN address on fk_address = id_address\n" +
                    "WHERE first_name = '"+fst+"' AND  second_name = '"+snd+"' AND city = '"+ct+"'";
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){
                id = Integer.parseInt(result.getString(1));
            }
        }catch (SQLException c){
            System.out.println("con err");
        }catch (ClassNotFoundException c){
            System.out.println("Driver error");
        }
        return id;
    }

    private String resetSql(){
        return "USE ab_cardealer;\n" +
                "\n" +
                "DROP TABLE IF EXISTS finished_transaction;\n" +
                "DROP TABLE IF EXISTS planned_transaction;\n" +
                "DROP TABLE IF EXISTS car;\n" +
                "DROP TABLE IF EXISTS model;\n" +
                "DROP TABLE IF EXISTS color;\n" +
                "DROP TABLE IF EXISTS employee;\n" +
                "DROP TABLE IF EXISTS customer;\n" +
                "DROP TABLE IF EXISTS branch;\n" +
                "DROP TABLE IF EXISTS personal_info;\n" +
                "DROP TABLE IF EXISTS address;\n" +
                "DROP TABLE IF EXISTS account;\n" +
                "\n" +
                "CREATE TABLE account\n" +
                "(\n" +
                "\tid_account\tINT PRIMARY KEY IDENTITY,\n" +
                "\tlogin\t\tVARCHAR(25) NOT NULL UNIQUE,\n" +
                "\tpassword\tVARCHAR(25) NOT NULL,\n" +
                "\ttype\t\tVARCHAR(25) NOT NULL\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE address\n" +
                "(\n" +
                "\tid_address\tINT PRIMARY KEY IDENTITY,\n" +
                "\tcity\t\tVARCHAR(25) NOT NULL,\n" +
                "\tpostal_code\tVARCHAR(6) NOT NULL,\n" +
                "\tstreet\t\tVARCHAR(25) NOT NULL,\n" +
                "\tbuilding\tVARCHAR(25) NOT NULL,\n" +
                "\tapartment\tVARCHAR(25)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE personal_info\n" +
                "(\n" +
                "\tid_pinfo\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_address\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES address(id_address),\n" +
                "\tfk_account\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES account(id_account),\n" +
                "\tfirst_name\tVARCHAR(25) NOT NULL,\n" +
                "\tsecond_name\tVARCHAR(25) NOT NULL,\n" +
                "\tbirth_date\tDATE NOT NULL,\n" +
                "\tgender\t\tVARCHAR(1) DEFAULT 'N' NOT NULL,\n" +
                "\temail\t\tVARCHAR(25),\n" +
                "\tphone\t\tVARCHAR(25) NOT NULL\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE branch\n" +
                "(\n" +
                "\tid_branch\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_address\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES address(id_address)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE customer\n" +
                "(\n" +
                "\tid_customer\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_pinfo\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES personal_info(id_pinfo)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE employee\n" +
                "(\n" +
                "\tid_employee\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_branch\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES branch(id_branch),\n" +
                "\tfk_pinfo\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES personal_info(id_pinfo),\n" +
                "\tsalary\t\tFLOAT NOT NULL DEFAULT 2000,\n" +
                "\tCHECK (salary > 0)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE color\n" +
                "(\n" +
                "\tid_color\tINT PRIMARY KEY IDENTITY,\n" +
                "\tr\t\t\tINT NOT NULL,\n" +
                "\tg\t\t\tINT NOT NULL,\n" +
                "\tb\t\t\tINT NOT NULL,\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE model\n" +
                "(\n" +
                "\tid_model\t\tINT PRIMARY KEY IDENTITY,\n" +
                "\tmodel_name\t\tVARCHAR(25) NOT NULL,\n" +
                "\tgeneration\t\tINT DEFAULT 0,\n" +
                "\tproduction_year\tINT NOT NULL,\n" +
                "\tCHECK (generation >= 0),\n" +
                "\tCHECK (production_year >= 1800)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE car\n" +
                "(\n" +
                "\tid_car\t\t\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_model\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES model(id_model),\n" +
                "\tfk_branch\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES branch(id_branch),\n" +
                "\tfk_color\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES color(id_color),\n" +
                "\tfk_seller\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES customer(id_customer),\n" +
                "\tbrand\t\t\tVARCHAR(25) NOT NULL,\n" +
                "\tprice\t\t\tFLOAT NOT NULL,\n" +
                "\tcondition\t\tINT NOT NULL,\n" +
                "\tavailability\tVARCHAR(25) DEFAULT 'available' NOT NULL,\n" +
                "\tCHECK (price > 0),\n" +
                "\tCHECK (condition > 0),\n" +
                "\tCHECK (condition < 11)\n" +
                ")\n" +
                "\n" +
                "CREATE TABLE planned_transaction\n" +
                "(\n" +
                "\tid_ptransaction\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_car\t\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES car(id_car),\n" +
                "\tfk_buyer\t\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES customer(id_customer),\n" +
                "\tstance\t\t\tVARCHAR(25) DEFAULT 'processing' NOT NULL,\n" +
                ")\n" +
                "\n" +
                "\n" +
                "\n" +
                "CREATE TABLE finished_transaction\n" +
                "(\n" +
                "\tid_ftransaction\tINT PRIMARY KEY IDENTITY,\n" +
                "\tfk_transaction\tINT FOREIGN KEY\n" +
                "\t\t\tREFERENCES planned_transaction(id_ptransaction)\n" +
                ")\n" +
                "\n" +
                "USE ab_cardealer;\n" +
                "\n" +
                "INSERT INTO account(login, password, type) VALUES\n" +
                "\t('adam1', 'hadam1', 'employee'),\n" +
                "\t('daniel1', 'hdaniel1', 'employee'),\n" +
                "\t('laura1', 'hlaura1', 'employee'),\n" +
                "\t('robert1', 'hrobert1', 'customer'),\n" +
                "\t('mikolaj1', 'hmikolaj1', 'customer'),\n" +
                "\t('renata1', 'hrenata1', 'customer'),\n" +
                "\t('vladimir1', 'hvladimir1', 'customer'),\n" +
                "\t('miroslawa1', 'hmiroslawa1', 'customer')\n" +
                "\n" +
                "INSERT INTO address(city, postal_code, street, building, apartment) VALUES\n" +
                "\t('Gdansk', '80-257', 'Mickiewicza', '5', '-'),\n" +
                "\t('Gdynia', '87-301', 'Asfaltowa', '13', '-'),\n" +
                "\t('Gdansk', '40-102', 'Majowa', '4', '13'),\n" +
                "\t('Gdansk', '99-199', 'Targowa', '3b', '2'),\n" +
                "\t('Gdynia', '70-700', 'Morska', '17', '29'),\n" +
                "\t('Gdansk', '17-102', 'Portowa', '7', '4'),\n" +
                "\t('Sopot', '30-400', 'Molowa', '14', '14'),\n" +
                "\t('Gdansk', '40-102', 'Majowa', '4', '14'),\n" +
                "\t('Gdynia', '17-700', 'Perlowa', '6', '6'),\n" +
                "\t('Gdynia', '70-700', 'Kolejowa', '4a', '5')\n" +
                "\n" +
                "INSERT INTO personal_info(fk_address, fk_account, first_name, second_name, birth_date, gender, email, phone) VALUES\n" +
                "\t(3, 1,'Adam','Adamiak','1994-10-30','M', 'adam@mail.com', '101 101 101'),\n" +
                "\t(4, 2,'Daniel','Kowalski','1995-05-12','M', 'daniel@mail.com', '102 102 102'),\n" +
                "\t(5, 3,'Laura','Sobocinska','1991-03-02','K', 'laura@mail.com', '103 103 103'),\n" +
                "\t(6, 4,'Robert','Gracinski','1984-12-24','M', 'robert@mail.com', '201 201 201'),\n" +
                "\t(7, 5,'Mikolaj','Salut','1998-06-01','M', 'mikolaj@mail.com', '202 202 202'),\n" +
                "\t(8, 6,'Renata','Nowy','1989-08-14','K', 'renata@mail.com', '203 203 203'),\n" +
                "\t(9, 7,'Vladimir','Dentsky','1998-06-17','M', NULL, '204 204 204'),\n" +
                "\t(10, 8,'Miroslawa','Kowalska','1978-08-20','K', 'miroslawa@mail.com', '205 205 205')\n" +
                "\n" +
                "INSERT INTO branch(fk_address) VALUES\n" +
                "\t(1),\n" +
                "\t(2)\n" +
                "\n" +
                "INSERT INTO customer(fk_pinfo) VALUES\n" +
                "\t(4),\n" +
                "\t(5),\n" +
                "\t(6),\n" +
                "\t(7),\n" +
                "\t(8)\n" +
                "\n" +
                "INSERT INTO employee(fk_branch, fk_pinfo, salary) VALUES\n" +
                "\t(1, 1, 2500),\n" +
                "\t(1, 2, 2000),\n" +
                "\t(2, 3, 2300)\n" +
                "\n" +
                "INSERT INTO color(r,g,b) VALUES\n" +
                "\t(100, 100, 100),\n" +
                "\t(255, 200, 110),\n" +
                "\t(30, 100, 180)\n" +
                "\n" +
                "INSERT INTO model(model_name, generation, production_year) VALUES\n" +
                "\t('impreza', 2, 2008),\n" +
                "\t('vectra c', 0, 2006),\n" +
                "\t('v60', 3, 2014)\n" +
                "\n" +
                "INSERT INTO car(fk_branch, fk_model, fk_color, fk_seller, brand, price, condition, availability) VALUES\n" +
                "\t(1, 1, 1, 1, 'subaru', 31000, 10, 'available'),\n" +
                "\t(1, 2, 2, 3, 'opel', 12500, 8, 'processing'),\n" +
                "\t(2, 3, 3, 5, 'volvo', 24200, 9, 'processing')\n" +
                "\n" +
                "\n" +
                "INSERT INTO planned_transaction(fk_car, fk_buyer) VALUES\n" +
                "(2, (SELECT customer.id_customer FROM customer\n" +
                "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo\n" +
                "INNER JOIN account ON account.id_account = personal_info.fk_account\n" +
                "WHERE account.login = 'miroslawa1')),\n" +
                "(3, (SELECT customer.id_customer FROM customer\n" +
                "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo\n" +
                "INNER JOIN account ON account.id_account = personal_info.fk_account\n" +
                "WHERE account.login = 'miroslawa1'))";
    }
}