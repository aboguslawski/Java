import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class Login extends JFrame implements ActionListener{

    private JButton btLogin, btRegister;
    private JTextField tfLogin, tfPassword;
    private JPasswordField pfPassword;
    private JLabel lbLogin, lbPassword, lbRegister, lbInvalid, lbImage;
    private ImageIcon img;

    public static ArrayList<Account> accounts = new ArrayList<>();

    public Login (){

        //** parametry okna

        setSize(300,500);
        setLocation(800,300);
        setResizable(false);
        setTitle("Login");
        setLayout(null);

        //** wstawianie obrazka

        img = new ImageIcon(getClass().getResource("monstertruck.png"));
        lbImage = new JLabel(img);
        lbImage.setIcon(img);
        lbImage.setBounds(10,10,268,170);
        add(lbImage);
        //** umieszczanie elementów

            // przyciski
                //logowania

        btLogin = new JButton("login");
        btLogin.setBounds(90,300,100,30);
        btLogin.addActionListener(this);
        add(btLogin);
                //rejestracji

        btRegister = new JButton("sign up");
        btRegister.setBounds(90,400,100,30);
        btRegister.addActionListener(this);
        add(btRegister);

            // pola tekstowe
                //login

        tfLogin = new JTextField("megaadmin");
        tfLogin.setBounds(90,200,100,30);
        add(tfLogin);

                //haslo

        pfPassword = new JPasswordField("pswd");
        pfPassword.setBounds(90,250,100,30);
        add(pfPassword);
//        tfPassword = new JTextField("admin");
//        tfPassword.setBounds(90,250,100,30);
//        add(tfPassword);

            // oznaczenia
                //login

        lbLogin = new JLabel("username:");
        lbLogin.setBounds(20,200,70,30);
        add(lbLogin);
                //haslo

        lbPassword = new JLabel("password:");
        lbPassword.setBounds(20,250,70,30);
        add(lbPassword);
                //rejestracja

        lbRegister = new JLabel("Don't have an account?");
        lbRegister.setBounds(75,370,200,30);
        add(lbRegister);
                //zle dane logowania

        lbInvalid = new JLabel("username or password is invalid.");
        lbInvalid.setForeground(Color.red);
        lbInvalid.setBounds(50,330,200,30);
        add(lbInvalid);
        lbInvalid.setVisible(false);

                //inicjacja bazy danych

        accounts = loadAccounts();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == btLogin){
            //** data

            String login, password;
            login = tfLogin.getText();
            password = new String(pfPassword.getPassword());
            boolean valid = false;
            Account account = null;

            //** czy admin

            if(login.equals("admin") && password.equals("admin")){
                Admin admin = new Admin();
                admin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                admin.setVisible(true);
                this.setVisible(false);
                this.dispose();
            }

            if(login.equals("megaadmin") && password.equals("pswd")){
                MegaAdmin megaAdmin = new MegaAdmin();
                megaAdmin.setVisible(true);
            }

            //** walidacja danych logowania

            for(Account a : accounts){
                if(a.compare(login,password)){
                    valid = true;
                    account = a;
                }
            }

            //** logowanie - ladowanie odpowiedniego interfejsu
            if(valid){
                if(account.getType().equals("employee")){
                    System.out.println("employee account login");
                    // logowanie jako pracownik

                    Employee employee = new Employee(account);
                    employee.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    employee.setVisible(true);
                    this.setVisible(false);
                    this.dispose();

                }
                else if(account.getType().equals("customer")){
                    System.out.println("customer account login");
                    // logowanie jako klient

                    Customer customer = new Customer(account);
                    customer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    customer.setVisible(true);
                    this.setVisible(false);
                    this.dispose();
                }
            }
            else lbInvalid.setVisible(true);








        }
        else if(source == btRegister){
            System.out.println("przycisk rejestruj");

            Register register = new Register();
            register.setVisible(true);


        }
    }

    public ArrayList<Account> loadAccounts(){
        Connection connection;
        int i = 0;
        String username, password, type;

        ArrayList<Account> accounts = new ArrayList<>();

        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection("jdbc:sqlserver://" +
                    "localhost:1433;databaseName=ab_cardealer;" +
                    "user=aboguslawski;password=Adam531;");

            Statement statement = connection.createStatement();
            String sql = "SELECT login, password, type FROM account";
            ResultSet result = statement.executeQuery(sql);

            while(result.next()){
                username = result.getString(1);
                password = result.getString(2);
                type = result.getString(3);
                accounts.add(new Account(username,password,type));
            }

        }catch (SQLException connectionError){
            System.out.println("Connection error");
        }catch (ClassNotFoundException driverError){
            System.out.println("Driver error");
        }

        return accounts;
    }
}
