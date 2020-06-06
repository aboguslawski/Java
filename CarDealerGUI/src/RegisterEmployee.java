import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterEmployee extends JFrame implements ActionListener, ItemListener {

    private Connection con;
    private String[] branches = {"Gdansk", "Gdynia"}, genders = {"M", "F", "N"};
    private JLabel  lbBranch, lbFirst, lbSecond, lbBirth, lbGender, lbEmail, lbPhone, lbCity, lbCode,
                    lbStreet, lbBuilding, lbApartment, lbLogin, lbPass, lbSalary;
    private JComboBox<String> cbBranch, cbGender;
    private JTextField  tfFirst, tfSecond, tfBirth, tfEmail, tfPhone, tfCity, tfCode, tfStreet,
                        tfBuilding, tfApartment, tfLogin, tfPass, tfSalary;
    private JButton btGen, btAdd;
    private String valBranch, valGender;

    RegisterEmployee(){
        setSize(650,350);
        setLocation(500,300);
        setTitle("Register new employee");
        setResizable(false);
        setLayout(null);


        //**components
            //1 row
        btGen = add(10,10,"random",this);

        lbFirst = Creator.newLabel(10,90,"first name:", this);
        tfFirst = Creator.newTextField(100,90,this);
        lbSecond = Creator.newLabel(10,130,"second name:", this);
        tfSecond = Creator.newTextField(100,130,this);
        lbBirth = Creator.newLabel(10,170,"birth date:",this);
        tfBirth = Creator.newTextField(100,170,this);
        lbGender = Creator.newLabel(10,210,"gender:",this);
        cbGender = Creator.newComboBox(100,210,genders,this);
        cbGender.addItemListener(this);
        lbEmail = Creator.newLabel(10,250,"email:",this);
        tfEmail = Creator.newTextField(100,250,this);

            //2 row
        lbPhone = Creator.newLabel(210,90,"phone:", this);
        tfPhone = Creator.newTextField(300,90,this);
        lbCity = Creator.newLabel(210,130,"city:",this);
        tfCity = Creator.newTextField(300,130,this);
        lbCode = Creator.newLabel(210,170,"code:", this);
        tfCode = Creator.newTextField(300,170,this);
        lbStreet = Creator.newLabel(210,210,"street:",this);
        tfStreet = Creator.newTextField(300,210,this);
        lbBuilding = Creator.newLabel(210,250,"building:",this);
        tfBuilding = Creator.newTextField(300,250,this);

            //3 row
        lbApartment = Creator.newLabel(410,90,"apartment:", this);
        tfApartment = Creator.newTextField(500,90,this);
        lbLogin = Creator.newLabel(410,130,"login",this);
        tfLogin = Creator.newTextField(500,130,this);
        lbPass = Creator.newLabel(410,170,"password:", this);
        tfPass = Creator.newTextField(500,170,this);
        lbSalary = Creator.newLabel(410,210,"salary:",this);
        tfSalary = Creator.newTextField(500,210,this);
        lbBranch = Creator.newLabel(410,250,"branch",this);
        cbBranch = Creator.newComboBox(500,250,branches, this);
        cbBranch.addItemListener(this);

        btAdd = add(500,10,"add",this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btGen){
            tfFirst.setText("first");
            tfSecond.setText("second");
            tfBirth.setText("1990-10-10");
            cbBranch.setSelectedIndex(1);
            cbGender.setSelectedIndex(1);
            tfEmail.setText("mail");
            tfPhone.setText("100200300");
            tfCity.setText("Gdansk");
            tfCode.setText("00-000");
            tfStreet.setText("street");
            tfBuilding.setText("2");
            tfApartment.setText("3");
            tfLogin.setText(RandomString.getAlphaNumericString(6));
            tfPass.setText("pswd");
            tfSalary.setText("1000");
        }
        if(e.getSource() == btAdd){

            addEmployee(tfFirst.getText(),tfSecond.getText(),tfBirth.getText(),valBranch,valGender,tfEmail.getText(),tfPhone.getText(),
                    tfCity.getText(), tfCode.getText(),tfStreet.getText(),tfBuilding.getText(),tfApartment.getText(),tfLogin.getText(),
                    tfPass.getText(),tfSalary.getText());
        }
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if(itemEvent.getSource() == cbBranch){
            if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                Object item = itemEvent.getItem();
                System.out.println(item.toString());
                if(item.toString().equals("Gdansk")){
                    valBranch = "1";
                }
                else valBranch = "2";
            }
        }
        if(itemEvent.getSource() == cbGender){
            if(itemEvent.getStateChange() == ItemEvent.SELECTED){
                Object item = itemEvent.getItem();
                System.out.println(item.toString());
                valGender = item.toString();
            }
        }
    }

    private JButton add(int x, int y, String content, JFrame frame){
        JButton button = Creator.newButton(x,y,content,frame);
        button.addActionListener(this);
        button.setBackground(Color.orange);

        return button;

    }

    private void addEmployee(String first, String second, String birth, String branch, String gender, String email, String phone,
                             String city, String code, String street, String building, String apartment, String login, String pass, String salary){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            con = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");
            String sql = "INSERT INTO address(city, postal_code, street, building, apartment) VALUES\n" +
                    "(?,?,?,?,?)\n" +
                    "INSERT INTO account(login,password,type) VALUES\n" +
                    "(?,?,?)\n" +
                    "INSERT INTO personal_info(fk_account,fk_address,first_name,second_name,birth_date,gender,email,phone) VALUES\n" +
                    "((SELECT TOP 1 id_account FROM account ORDER BY id_account DESC),\n" +
                    "(SELECT TOP 1 id_address FROM address ORDER BY id_address DESC),\n" +
                    "?,?,?,?,?,?)\n" +
                    "INSERT INTO employee(fk_pinfo, fk_branch, salary) VALUES\n" +
                    "((SELECT TOP 1 id_pinfo FROM personal_info ORDER BY id_pinfo DESC),\n" +
                    "?,?)";
            PreparedStatement statement = con.prepareStatement(sql);
            System.out.println("1");
            statement.setString(1,city);
            statement.setString(2,code);
            statement.setString(3,street);
            statement.setString(4,building);
            statement.setString(5,apartment);
            statement.setString(6,login);
            statement.setString(7,pass);
            statement.setString(8,"employee");
            statement.setString(9,first);
            statement.setString(10,second);
            System.out.println("2");
            statement.setString(11,birth);
            statement.setString(12,gender);
            statement.setString(13,email);
            statement.setString(14,phone);
            statement.setString(15,branch);
            statement.setString(16,salary);
            System.out.println("32");
            statement.executeUpdate();
            System.out.println("43");
            statement.close();

        }catch (SQLException connectionError){
            connectionError.printStackTrace();
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }
    }

}
