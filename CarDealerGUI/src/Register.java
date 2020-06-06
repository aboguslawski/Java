import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Register extends JFrame implements ActionListener {

    private JLabel      lbLogin, lbPassword, lbFirstName, lbLastName,
                        lbBirth, lbGender, lbEmail, lbPhone, lbCity,
                        lbPostalCode, lbStreet, lbBuilding, lbApartment,
                        lbAccount, lbPInfo, lbAddress, lbError;

    private JTextField  tfUsername, tfPassword, tfFirstName, tfLastName, tfEmail, tfPhone,
                        tfCity, tfPostalCode, tfStreet, tfBuilding, tfApartment, tfBirth;

    private JButton     btRegister, btGenerator;

    private JComboBox<String> cbGender;

    Register(){
        //** parametry okna

        setSize(700,400);
        setLocation(500,300);
        setTitle("Register");
        setResizable(false);
        setLayout(null);

        //** umieszczanie elementow
            //lewa czesc - dane konta
            btGenerator = generatorListener(20,150);

            lbAccount = Creator.newLabel(75,210, "account info", this);
            lbAccount.setForeground(Color.gray);

            tfUsername = Creator.newTextField(100,250, this);
            lbLogin = Creator.newLabel(20,250, "login*:", this);
            tfPassword = Creator.newTextField(100,300, this);
            lbPassword = Creator.newLabel(20,300,"password*:", this);

            //srodkowa czesc - dane personalne
            lbPInfo = Creator.newLabel(275,10,"personal info", this);
            lbPInfo.setForeground(Color.gray);

            tfFirstName = Creator.newTextField(300,50, this);
            lbFirstName = Creator.newLabel(220,50,"first-name*:", this);
            tfLastName = Creator.newTextField(300,100, this);
            lbLastName = Creator.newLabel(220, 100, "last-name*:", this);
            tfBirth = Creator.newTextField(300,150, this);
            tfBirth.setText("2000-01-01");
            lbBirth = Creator.newLabel(220,150,"birth date*:", this);
            cbGender = Creator.newComboBox(300,200, new String[] {"male", "female", "other"}, this);
            lbGender = Creator.newLabel(220,200,"gender:", this);
            tfEmail = Creator.newTextField(300,250, this);
            lbEmail = Creator.newLabel(220,250,"email*:", this);
            tfPhone = Creator.newTextField(300,300, this);
            lbPhone = Creator.newLabel(220,300,"phone*:", this);

            //prawa czesc - dane adresu
            lbAddress = Creator.newLabel(475,10,"address info", this);
            lbAddress.setForeground(Color.gray);

            tfCity = Creator.newTextField(500,50, this);
            lbCity = Creator.newLabel(420,50,"city*:", this);
            tfPostalCode = Creator.newTextField(500,100, this);
            lbPostalCode = Creator.newLabel(420,100,"postal code*:", this);
            tfStreet = Creator.newTextField(500,150, this);
            lbStreet = Creator.newLabel(420,150,"street*:", this);
            tfBuilding = Creator.newTextField(500,200, this);
            lbBuilding = Creator.newLabel(420,200,"building*:", this);
            tfApartment = Creator.newTextField(500,250, this);
            lbApartment = Creator.newLabel(420,250,"apartment:", this);


            btRegister = Creator.newButton(500,300,"create", this);
            btRegister.addActionListener(this);
            lbError = Creator.newLabel(500,330,"", this);
            lbError.setForeground(Color.red);
    }


    private JButton generatorListener(int x, int y){
        JButton button = new JButton("generator");

        button.addActionListener((ActionEvent e) ->{
            tfUsername.setText(RandomString.getAlphaNumericString(6));
            tfPassword.setText("pswd");
            tfFirstName.setText("fname");
            tfLastName.setText("lname");
            tfBirth.setText("2000-01-01");
            cbGender.setSelectedIndex(2);
            tfEmail.setText("e@mail.net");
            tfPhone.setText("000 000 000");
            tfCity.setText("city");
            tfPostalCode.setText("00-000");
            tfStreet.setText("street");
            tfBuilding.setText("0");

        });

        button.setBounds(x,y,100,30);
        button.setVisible(true);
        add(button);

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Connection connection;
        String gender = cbGender.getSelectedItem().toString();
        boolean usernameTaken = false;

        if(gender.equals("other")) gender = "O";
        else if(gender.equals("male")) gender = "M";
        else gender = "F";

        if(tfApartment.getText().equals("")){
            tfApartment.setText("-");
        }
        for(Account a : Login.accounts){
            if(a.getUsername().equals(tfUsername.getText()))
                usernameTaken = true;
        }

        if(usernameTaken){
            lbError.setText("username is already taken");
        }
        else{
            try{
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                connection = DriverManager.getConnection("jdbc:sqlserver://" +
                        "localhost:1433;databaseName=ab_cardealer;" +
                        "user=aboguslawski;password=Adam531;");

                //INSERT
                String sql ="INSERT INTO account(login, password, type) VALUES" +
                        "(?,?,?)\n"+
                        "INSERT INTO address(city,postal_code,street,building,apartment) VALUES" +
                        "(?,?,?,?,?)\n"+
                        "INSERT INTO personal_info(fk_address, fk_account, first_name, second_name, birth_date, gender, email, phone) VALUES" +
                        "((SELECT TOP 1 id_address FROM address ORDER BY id_address DESC)," +
                        "(SELECT TOP 1 id_account FROM account ORDER BY id_account DESC),?,?,?,?,?,?)\n" +
                        "INSERT INTO customer(fk_pinfo) VALUES" +
                        "((SELECT TOP 1 id_pinfo FROM personal_info ORDER BY id_pinfo DESC))";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1,tfUsername.getText());
                statement.setString(2,tfPassword.getText());
                statement.setString(3,"customer");
                statement.setString(4,tfCity.getText());
                statement.setString(5,tfPostalCode.getText());
                statement.setString(6,tfStreet.getText());
                statement.setString(7,tfBuilding.getText());
                statement.setString(8,tfApartment.getText());
                statement.setString(9,tfFirstName.getText());
                statement.setString(10,tfLastName.getText());
                statement.setString(11,tfBirth.getText());
                statement.setString(12,gender);
                statement.setString(13,tfEmail.getText());
                statement.setString(14,tfPhone.getText());
                System.out.println("halo");
                statement.executeUpdate();
                statement.close();
                lbError.setVisible(false);

                Login.accounts.add(new Account(tfUsername.getText(), tfPassword.getText(), "customer"));

            }catch (SQLException connectionError){
                System.out.println("Connection error");
                lbError.setText("invalid data");
                lbError.setVisible(true);
            }catch (ClassNotFoundException driverError){
                System.out.println("Driver error");
            }
        }

    }
}
