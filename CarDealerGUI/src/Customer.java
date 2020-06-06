import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Customer extends JFrame implements ActionListener, ItemListener {

    private Connection con;
    private Account account;

    //** glowny podzial
    private JButton btLogout;
    private JSplitPane splitPane1, splitPane2;

    //** zakladki
    private JPanel pnTabs, pnLeft, pnRight;
    private JButton btCars, btSales, btTransactions, btMessages;

    private List<String[]> list = new ArrayList<String[]>();

    //** opcje

        //cars
    private JLabel lbCarsBranch, lbCarsPriceFrom, lbCarsPriceTo, lbPrice, lbYear, lbCarsYearFrom, lbCarsYearTo;
    private JButton btCarsPrint;
    private JTextField tfCarsPriceFrom, tfCarsPriceTo, tfCarsYearFrom, tfCarsYearTo;
    private JRadioButton rbCarsGdansk, rbCarsGdynia, rbCarsAvailbale;
    private int valCarsGdansk = 0, valCarsGdynia = 0, valCarsAvailable = 0;

        //sales
    private JLabel  lbError, lbSalesBranch, lbSalesBrand, lbSalesModel, lbSalesGen, lbSalesYear,
                    lbSalesColor, lbSalesR, lbSalesG, lbSalesB, lbSalesPrice, lbSalesCondition;
    private JTextField tfSalesBrand, tfSalesModel, tfSalesGen, tfSalesYear, tfSalesR, tfSalesG, tfSalesB, tfSalesPrice, tfSalesCondition;
    private JComboBox<String> cbSalesBranch;
    private JButton btLabelAdd;

        //transactions
    private JButton btTranBuyer, btTranSeller, btTranBuy, btTranCars;
    private JComboBox<String> cbTranCarList;
    private JLabel  lbTranBrand, lbTranModel, lbTranYear, lbTranGen, lbTranPrice, lbTranCond,
                    lbTranAvlb, lbTranCity, lbTranColor, lbTranSeller, lbTranInfo, lbTranError;
    String valTransCarId;

    Customer(Account acc){
        this.account = acc;
        //** parametry okna

        setSize(800,600);
        setLocation(500,300);
        setResizable(false);
        setTitle(acc.getUsername());

        //**panele
            // zakladki

        pnTabs = new JPanel();
        pnTabs.setLayout(null);
        pnTabs.setPreferredSize(new Dimension(230,90));

            // tworzenie podzialu

        pnLeft = new JPanel();
        pnLeft.setLayout(null);
        pnLeft.setBackground(Color.yellow);

        pnRight = new JPanel(new FlowLayout(FlowLayout.LEFT));

        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTabs, pnLeft);
        splitPane1.setDividerSize(1);
        splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitPane1, pnRight);
        splitPane2.setDividerSize(1);

        add(splitPane2);

                // przyciski

        btCars = Creator.newButton(10,10,"cars",pnTabs);
        btCars.addActionListener(this);
        btCars.setBackground(Color.lightGray);
        btSales = Creator.newButton(120,10,"sales",pnTabs);
        btSales.addActionListener(this);
        btSales.setBackground(Color.lightGray);
        btTransactions = Creator.newButton(10, 50, "transactions", pnTabs);
        btTransactions.addActionListener(this);
        btTransactions.setBackground(Color.lightGray);
        btMessages = Creator.newButton(120,50,"messages", pnTabs);
        btMessages.addActionListener(this);
        btMessages.setBackground(Color.lightGray);
        btLogout = Creator.newButton(10,430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btLogout){
            Login login = new Login();
            login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            login.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }

        // ** zakladka CARS ---------------------------------------------------------------------------------------------

        if(e.getSource() == btCars){
            setCarsLayout();
        }

        if(e.getSource() == btCarsPrint){
            // nazwy naglowkow w tabeli
            String[] t = {"brand", "model", "year", "gen", "price", "cond", "availability", "city", "color rgb", "seller"};
            String city = "address.city = 'Gdynia' or address.city = 'Gdansk'", availability;

            // odczytywanie wartosci z pol
            switch (Integer.toString(valCarsGdansk) + Integer.toString(valCarsGdynia)){
                case "11":
                    city = "address.city = 'Gdynia' or address.city = 'Gdansk'";
                    break;
                case "10":
                    city = "address.city = 'Gdansk'";
                    break;
                case "01":
                    city = "address.city = 'Gdynia'";
                    break;
                case "00":
                    city = "address.city = 'Ostroda'";
            }
            if(valCarsAvailable == 1)
                availability = " AND car.availability = 'available'";
            else availability = " AND car.availability != 'sold'";

            String sql = "SELECT car.brand as 'brand', model.model_name as 'model', model.production_year as 'year', model.generation as 'gen',\n" +
                    "car.price as 'price', car.condition as 'cond.', car.availability as 'availability', address.city as 'city',\n" +
                    "CONCAT(color.r, ' ', color.g, ' ', color.b) as 'color rgb', account.login as 'seller'\n" +
                    "FROM car INNER JOIN model ON car.fk_model = model.id_model\n" +
                    "INNER JOIN branch ON car.fk_branch = branch.id_branch\n" +
                    "INNER JOIN address ON branch.fk_address = address.id_address\n" +
                    "INNER JOIN color ON car.fk_color = color.id_color\n" +
                    "INNER JOIN customer ON car.fk_seller = customer.id_customer\n" +
                    "INNER JOIN personal_info ON customer.fk_pinfo = personal_info.id_pinfo\n" +
                    "INNER JOIN account ON personal_info.fk_account = account.id_account\n" +
                    "WHERE " + city + availability +
                    " AND car.price >= " + tfCarsPriceFrom.getText() +
                    " AND car.price <= " + tfCarsPriceTo.getText() +
                    " AND model.production_year >= " + tfCarsYearFrom.getText() +
                    " AND model.production_year <= " + tfCarsYearTo.getText();
            printTable(sql,t);
        }

        // ** zakladka SALES --------------------------------------------------------------------------------------------

        if(e.getSource() == btSales){
            setSalesLayout();
        }
        if(e.getSource() == btLabelAdd){
            String branch;
            if(cbSalesBranch.getSelectedItem().toString().equals("Gdansk")) branch = "1";
            else branch = "2";
            try{
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                con = DriverManager.getConnection("jdbc:sqlserver://" +
                        "localhost:1433;databaseName=ab_cardealer;" +
                        "user=aboguslawski;password=Adam531;");

                //INSERT
                String sql ="INSERT INTO model(model_name, generation, production_year) VALUES " +
                        "(?,?,?)\n" +
                        "INSERT INTO color(r,g,b) VALUES " +
                        "(?,?,?)\n" +
                        "INSERT INTO car(fk_branch, fk_model, fk_color, fk_seller, brand, price, condition, availability) VALUES\n" +
                        "(?, " +
                        "(SELECT TOP 1 id_model FROM model ORDER BY id_model DESC)," +
                        "(SELECT TOP 1 id_color FROM color ORDER BY id_color DESC)," +
                        "(SELECT customer.id_customer FROM customer " +
                        "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo " +
                        "INNER JOIN account ON personal_info.fk_account = account.id_account " +
                        "WHERE account.login = ?)," +
                        "?, ?, ?, 'available')";
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setString(1,tfSalesModel.getText());
                statement.setString(2,tfSalesGen.getText());
                statement.setString(3,tfSalesYear.getText());
                statement.setString(4,tfSalesR.getText());
                statement.setString(5,tfSalesG.getText());
                statement.setString(6,tfSalesB.getText());
                statement.setString(7,branch);
                statement.setString(8,account.getUsername());
                statement.setString(9,tfSalesBrand.getText());
                statement.setString(10,tfSalesPrice.getText());
                statement.setString(11,tfSalesCondition.getText());
                statement.executeUpdate();
                statement.close();
//                lbError.setVisible(false);

            }catch (SQLException connectionError){
                System.out.println("Connection error");
//                lbError.setText("invalid data");
//                lbError.setVisible(true);
            }catch (ClassNotFoundException driverError){
                System.out.println("Driver error");
            }
            setSalesLayout();
        }

        // ** zakladka TRANSACTIONS -------------------------------------------------------------------------------------
        if(e.getSource() == btTransactions){
            setTransactionsLayout();
        }

        if(e.getSource() == btTranBuy){
            if(lbTranAvlb.getText().equals("available")){
                lbTranError.setText("transaction initiated");
                lbTranError.setForeground(Color.green);

                try{
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    con = DriverManager.getConnection("jdbc:sqlserver://" +
                            "localhost:1433;databaseName=ab_cardealer;" +
                            "user=aboguslawski;password=Adam531;");
                    String sql = "UPDATE car\n" +
                            "SET availability = 'processing'\n" +
                            "WHERE id_car = " + valTransCarId;

                    PreparedStatement statement = con.prepareStatement(sql);
                    statement.executeUpdate();
                    statement.close();

                    sql = "INSERT INTO planned_transaction(fk_car, fk_buyer) VALUES\n" +
                            "(" + valTransCarId + ", (SELECT customer.id_customer FROM customer\n" +
                            "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo\n" +
                            "INNER JOIN account ON account.id_account = personal_info.fk_account\n" +
                            "WHERE account.login = '" + account.getUsername() + "'))";
                    PreparedStatement statement2 = con.prepareStatement(sql);
                    statement2.executeUpdate();
                    statement2.close();


                }catch (SQLException connectionError){
                    System.out.println("Connection error");
                }catch (ClassNotFoundException driverError){
                    System.out.println("Driver error");
                }
            }
            else{
                lbTranError.setText("this car is not available");
                lbTranError.setForeground(Color.red);
            }
        }
        if(e.getSource() == btTranBuyer){
            btTranBuyer.setBackground(Color.green);
            btTranSeller.setBackground(Color.lightGray);
            lbTranError.setText("");

            String[] t = {"car", "year", "price", "city", "seller"};
            String sql = "SELECT " +
                    "CONCAT(car.brand,' ',model.model_name) as 'car', model.production_year, " +
                    "car.price, address.city, account.login as 'seller' FROM planned_transaction " +
                    "INNER JOIN car ON car.id_car = planned_transaction.fk_car " +
                    "INNER JOIN model ON model.id_model = car.fk_model " +
                    "INNER JOIN branch ON branch.id_branch = car.fk_branch " +
                    "INNER JOIN address ON address.id_address = branch.fk_address " +
                    "INNER JOIN customer ON customer.id_customer = car.fk_seller " +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo " +
                    "INNER JOIN account ON account.id_account = personal_info.fk_account " +
                    "WHERE planned_transaction.stance != 'finished' AND fk_buyer IN  " +
                    "( " +
                    "SELECT customer.id_customer FROM customer " +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo " +
                    "INNER JOIN account ON account.id_account = personal_info.fk_account " +
                    "WHERE account.login = '" + account.getUsername() +
                    "')";

            printTable(sql, t);

        }

        if(e.getSource() == btTranSeller){
            btTranSeller.setBackground(Color.green);
            btTranBuyer.setBackground(Color.lightGray);
            lbTranError.setText("");

            String[] t = {"car", "year", "price", "city", "buyer"};
            String sql = "SELECT " +
                    "CONCAT(car.brand,' ',model.model_name) as 'car', model.production_year, " +
                    "car.price, address.city, account.login as 'buyer' FROM car " +
                    "INNER JOIN planned_transaction ON planned_transaction.fk_car = car.id_car " +
                    "INNER JOIN model ON model.id_model = car.fk_model " +
                    "INNER JOIN branch ON branch.id_branch = car.fk_branch " +
                    "INNER JOIN address ON address.id_address = branch.fk_address " +
                    "INNER JOIN customer ON customer.id_customer = planned_transaction.fk_buyer " +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo " +
                    "INNER JOIN account ON account.id_account = personal_info.fk_account " +
                    "WHERE planned_transaction.stance != 'finished' AND fk_seller IN  " +
                    "( " +
                    "SELECT customer.id_customer FROM customer " +
                    "INNER JOIN personal_info ON personal_info.id_pinfo = customer.fk_pinfo " +
                    "INNER JOIN account ON account.id_account = personal_info.fk_account " +
                    "WHERE account.login = '" + account.getUsername() +
                    "')";
            System.out.println(account.getUsername());

            printTable(sql, t);
        }


    }


























    // ***************** FUNKCJE ***************************

    String readFile(String path){
        StringBuilder contentBuilder = new StringBuilder();
        try(BufferedReader b = new BufferedReader(new FileReader(path))){
            String currentLine;
            while((currentLine = b.readLine()) != null){
                contentBuilder.append(currentLine).append("\n");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    void printTable(String sql, String[] columns){
        pnRight.removeAll();
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();
            while(resultSet.next()){
                String[] t = new String[countColumns];
                for(int i = 0; i < countColumns; i++){
                    t[i] = resultSet.getString(i+1);
                }
                list.add(t);
            }
            String array[][] = new String [list.size()][];
            for(int i = 0; i < array.length; i++){
                String [] row = list.get(i);
                array[i] = row;
            }
            statement.close();
            JTable jt = new JTable(array,columns);
            jt.setRowHeight(30);
            JScrollPane sp = new JScrollPane(jt);
            sp.setPreferredSize(new Dimension(540,550));
            pnRight.add(sp);
            pnRight.doLayout();
            list.clear();
            con.close();
        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }


    // ********** LAYOUTS *************************

    void setCarsLayout(){
        btCars.setBackground(Color.yellow);
        btMessages.setBackground(Color.lightGray);
        btTransactions.setBackground(Color.lightGray);
        btSales.setBackground(Color.lightGray);

        pnLeft.setBackground(Color.white);
        pnLeft.removeAll();
        pnLeft.setBackground(Color.yellow);

        rbCarsGdansk = Creator.newRadioButton(20,10,"Gdansk",pnLeft);
        rbCarsGdansk.setBackground(Color.yellow);
        rbCarsGdynia = Creator.newRadioButton(20,50,"Gdynia",pnLeft);
        rbCarsGdynia.setBackground(Color.yellow);
        rbCarsGdansk.setSelected(true);
        rbCarsGdynia.setSelected(true);
        valCarsGdansk = valCarsGdynia = 1;
        rbCarsGdansk.addActionListener((ActionEvent e) ->{
            if(valCarsGdansk == 1) valCarsGdansk = 0;
            else valCarsGdansk = 1;
        });
        rbCarsGdynia.addActionListener((ActionEvent e) ->{
            if(valCarsGdynia == 1) valCarsGdynia = 0;
            else valCarsGdynia = 1;
        });

        lbPrice = Creator.newSmallLabel(100,100,"price",pnLeft);
        lbCarsPriceFrom = Creator.newSmallLabel(20,130,"from", pnLeft);
        lbCarsPriceTo = Creator.newSmallLabel(20,160,"to",pnLeft);
        tfCarsPriceFrom = Creator.newTextField(100,130, pnLeft);
        tfCarsPriceFrom.setText("0");
        tfCarsPriceTo = Creator.newTextField(100,160,pnLeft);
        tfCarsPriceTo.setText("999999");

        lbYear = Creator.newSmallLabel(100, 200, "year", pnLeft);
        lbCarsYearFrom = Creator.newSmallLabel(20, 230, "from",pnLeft);
        lbCarsYearTo = Creator.newSmallLabel(20,260,"to",pnLeft);
        tfCarsYearFrom = Creator.newTextField(100,230,pnLeft);
        tfCarsYearFrom.setText("0");
        tfCarsYearTo = Creator.newTextField(100,260,pnLeft);
        tfCarsYearTo.setText("2020");

        rbCarsAvailbale = Creator.newRadioButton(20,300, "only available",pnLeft);
        rbCarsAvailbale.setBackground(Color.yellow);
        rbCarsAvailbale.addActionListener((ActionEvent e) ->{
            if(valCarsAvailable == 1) valCarsAvailable = 0;
            else valCarsAvailable = 1;
        });

        btCarsPrint = Creator.newButton(100,350,"find", pnLeft);
        btCarsPrint.addActionListener(this);
        btCarsPrint.setBackground(Color.orange);

        btLogout = Creator.newButton(10,430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);

        pnLeft.doLayout();

    }

    void setSalesLayout(){
        btSales.setBackground(Color.yellow);
        btCars.setBackground(Color.lightGray);
        btTransactions.setBackground(Color.lightGray);
        btMessages.setBackground(Color.lightGray);

        pnLeft.setBackground(Color.white);
        pnLeft.removeAll();
        pnLeft.setBackground(Color.yellow);
        String[] branches = {"Gdansk", "Gdynia"};

        lbSalesBranch = Creator.newLabel(10,10,"branch:", pnLeft);
        cbSalesBranch = Creator.newComboBox(100,10,branches,pnLeft);

        lbSalesBrand = Creator.newLabel(10, 50, "brand:",pnLeft);
        tfSalesBrand = Creator.newTextField(100,50, pnLeft);

        lbSalesModel = Creator.newLabel(10, 90, "model:", pnLeft);
        tfSalesModel = Creator.newTextField(100,90, pnLeft);

        lbSalesGen = Creator.newLabel(10,130,"gen:",pnLeft);
        tfSalesGen = Creator.newTextField(100,130,pnLeft);

        lbSalesYear = Creator.newLabel(10,170,"year:",pnLeft);
        tfSalesYear = Creator.newTextField(100,170, pnLeft);

        lbSalesPrice = Creator.newLabel(10,210,"price:",pnLeft);
        tfSalesPrice = Creator.newTextField(100,210,pnLeft);

        lbSalesCondition = Creator.newLabel(10,250,"condition:", pnLeft);
        tfSalesCondition = Creator.newTextField(100,250,pnLeft);

        lbSalesColor = Creator.newLabel(10,310,"color",pnLeft);
        lbSalesR = Creator.newLabel(100,285,"r",pnLeft);
        tfSalesR = Creator.newTextField(80,310, 45, pnLeft);
        lbSalesG = Creator.newLabel(150,285,"g",pnLeft);
        tfSalesG = Creator.newTextField(130,310,45,pnLeft);
        lbSalesB= Creator.newLabel(200,285,"b",pnLeft);
        tfSalesB = Creator.newTextField(180,310,45,pnLeft);

        btLabelAdd = Creator.newButton(100,360, "add", pnLeft);
        btLabelAdd.addActionListener(this);
        btLabelAdd.setBackground(Color.orange);

        btLogout = Creator.newButton(10,430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);

        String[] t = {"brand", "model", "year", "gen", "price", "cond", "availability", "city", "color rgb"};
        String sql = "SELECT car.brand as 'brand', model.model_name as 'model', model.production_year as 'year', model.generation as 'gen',\n" +
                "car.price as 'price', car.condition as 'cond.', car.availability as 'availability', address.city as 'city',\n" +
                "CONCAT(color.r, ' ', color.g, ' ', color.b) as 'color rgb'\n" +
                "FROM car INNER JOIN model ON car.fk_model = model.id_model\n" +
                "INNER JOIN branch ON car.fk_branch = branch.id_branch\n" +
                "INNER JOIN address ON branch.fk_address = address.id_address\n" +
                "INNER JOIN color ON car.fk_color = color.id_color\n" +
                "INNER JOIN customer ON car.fk_seller = customer.id_customer\n" +
                "INNER JOIN personal_info ON customer.fk_pinfo = personal_info.id_pinfo\n" +
                "INNER JOIN account ON personal_info.fk_account = account.id_account\n" +
                "WHERE login = '" + account.getUsername() + "'";

        printTable(sql,t);



        pnLeft.doLayout();
    }

    private void setTransactionsLayout(){
        btSales.setBackground(Color.lightGray);
        btCars.setBackground(Color.lightGray);
        btTransactions.setBackground(Color.yellow);
        btMessages.setBackground(Color.lightGray);

        pnLeft.setBackground(Color.white);
        pnLeft.removeAll();
        pnLeft.setBackground(Color.yellow);

        String[] list = loadCar();

        cbTranCarList = Creator.newComboBox(10,10,200,list,pnLeft);
        cbTranCarList.addItemListener(this);

        lbTranBrand = Creator.newLabel(10,40,"brand", pnLeft);
        lbTranModel = Creator.newLabel(110,40,"model",pnLeft);
        lbTranYear = Creator.newLabel(10,70,"year", pnLeft);
        lbTranGen = Creator.newLabel(110,70,"gen", pnLeft);
        lbTranPrice = Creator.newLabel(10,100,"price", pnLeft);
        lbTranCond = Creator.newLabel(110,100,"10/10", pnLeft);
        lbTranAvlb = Creator.newLabel(10, 130, "available", pnLeft);
        lbTranCity = Creator.newLabel(110,130, "city", pnLeft);
        lbTranColor = Creator.newLabel(10,160, "255/255/255", pnLeft);
        lbTranSeller = Creator.newLabel(110, 160, "seller", pnLeft);

        btTranBuy = Creator.newButton(60,200, "buy", pnLeft);
        btTranBuy.setBackground(Color.orange);
        btTranBuy.addActionListener(this);
        lbTranError = Creator.newLabel(40,230,"", pnLeft);

        lbTranInfo = Creator.newLabel(25,270,"transactions where you are a:", pnLeft);
        btTranBuyer = Creator.newButton(10,300,"buyer", pnLeft);
        btTranBuyer.setBackground(Color.lightGray);
        btTranBuyer.addActionListener(this);
        btTranSeller = Creator.newButton(110,300,"seller",pnLeft);
        btTranSeller.setBackground(Color.lightGray);
        btTranSeller.addActionListener(this);

        btLogout = Creator.newButton(10,430, "logout", pnLeft);
        btLogout.addActionListener(this);
        btLogout.setBackground(Color.darkGray);
        btLogout.setForeground(Color.red);

        pnLeft.doLayout();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED){
            Object item = e.getItem();
            valTransCarId = item.toString().split(" ")[0];

            ArrayList<String> carInfo = loadCar(valTransCarId);

            lbTranBrand.setText(carInfo.get(1));
            lbTranModel.setText(carInfo.get(2));
            lbTranYear.setText(carInfo.get(3));
            lbTranGen.setText(carInfo.get(4));
            lbTranPrice.setText(carInfo.get(5));
            lbTranCond.setText(carInfo.get(6));
            lbTranAvlb.setText(carInfo.get(7));
            lbTranCity.setText(carInfo.get(8));
            lbTranColor.setText(carInfo.get(9));
            lbTranSeller.setText(carInfo.get(10));

        }
    }

    private ArrayList<String> loadCar(String id){
        Connection connection;

        ArrayList<String> carInfo = new ArrayList<>();

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = connection.createStatement();
            String sql = "SELECT car.id_car, car.brand, model.model_name, model.production_year, \n" +
                    "model.generation, car.price, car.condition, car.availability, address.city, \n" +
                    "CONCAT(color.r,'/',color.g,'/',color.b) as 'color', account.login\n" +
                    "FROM car\n" +
                    "INNER JOIN model ON model.id_model = car.fk_model\n" +
                    "INNER JOIN customer ON car.fk_seller = customer.id_customer\n" +
                    "INNER JOIN personal_info ON customer.fk_pinfo = personal_info.id_pinfo\n" +
                    "INNER JOIN account ON personal_info.fk_account = account.id_account\n" +
                    "INNER JOIN color ON car.fk_color = color.id_color\n" +
                    "INNER JOIN branch ON car.fk_branch = branch.id_branch\n" +
                    "INNER JOIN address ON branch.fk_address = address.id_address " +
                    "WHERE car.availability != 'sold'";
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();

            while(result.next()){
                if(result.getString(1).equals(id)){
                    for(int i = 0; i < countColumns; i++){
                        carInfo.add(result.getString(i+1));
                    }
                }
            }

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }

        return carInfo;
    }

    private String[] loadCar(){
        Connection connection;

        ArrayList<String> carInfo = new ArrayList<>();
        String[] output;
        String id, brand, model, year, price;

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = connection.createStatement();
            String sql = "SELECT car.id_car, car.brand, model.model_name, model.production_year, \n" +
                    "model.generation, car.price, car.condition, car.availability, address.city, \n" +
                    "CONCAT(color.r,'/',color.g,'/',color.b) as 'color', account.login\n" +
                    "FROM car\n" +
                    "INNER JOIN model ON model.id_model = car.fk_model\n" +
                    "INNER JOIN customer ON car.fk_seller = customer.id_customer\n" +
                    "INNER JOIN personal_info ON customer.fk_pinfo = personal_info.id_pinfo\n" +
                    "INNER JOIN account ON personal_info.fk_account = account.id_account\n" +
                    "INNER JOIN color ON car.fk_color = color.id_color\n" +
                    "INNER JOIN branch ON car.fk_branch = branch.id_branch\n" +
                    "INNER JOIN address ON branch.fk_address = address.id_address " +
                    "WHERE car.availability != 'sold' ";
            ResultSet result = statement.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = result.getMetaData();
            int countColumns = resultSetMetaData.getColumnCount();

            while(result.next()){
                id = result.getString(1);
                brand = result.getString(2);
                model = result.getString(3);
                year = result.getString(4);
                price = result.getString(6);
                carInfo.add(id + " " + brand + " " + model + " " + price + "PLN " + year + "y");
            }

            output = new String[carInfo.size()];

            for(int i = 0; i < output.length; i++){
                output[i] = carInfo.get(i);
            }
            return output;

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }

        return new String[1];
    }
}
