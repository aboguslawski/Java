import javax.swing.*;

public abstract class Creator {

    public static JTextField newTextField(int x, int y, JFrame frame){
        JTextField textField = new JTextField();
        textField.setBounds(x,y, 100, 30);
        textField.setVisible(true);
        frame.add(textField);

        return textField;
    }

    public static JTextField newTextField(int x, int y, JPanel frame){
        JTextField textField = new JTextField();
        textField.setBounds(x,y, 100, 30);
        textField.setVisible(true);
        frame.add(textField);

        return textField;
    }

    public static JTextField newTextField(int x, int y, int width, JPanel frame){
        JTextField textField = new JTextField();
        textField.setBounds(x,y, width, 30);
        textField.setVisible(true);
        frame.add(textField);

        return textField;
    }

    public static JComboBox<String> newComboBox(int x, int y, String[] content, JFrame frame){
        JComboBox<String> comboBox = new JComboBox<>(content);
        comboBox.setBounds(x,y,100,30);
        comboBox.setVisible(true);
        frame.add(comboBox);

        return comboBox;
    }

    public static JComboBox<String> newComboBox(int x, int y, String[] content, JPanel frame){
        JComboBox<String> comboBox = new JComboBox<>(content);
        comboBox.setBounds(x,y,100,30);
        comboBox.setVisible(true);
        frame.add(comboBox);

        return comboBox;
    }

    public static JComboBox<String> newComboBox(int x, int y, int width, String[] content, JPanel frame){
        JComboBox<String> comboBox = new JComboBox<>(content);
        comboBox.setBounds(x,y,width,30);
        comboBox.setVisible(true);
        frame.add(comboBox);

        return comboBox;
    }
    public static JComboBox<String> newComboBox(int x, int y, int width, String[] content, JFrame frame){
        JComboBox<String> comboBox = new JComboBox<>(content);
        comboBox.setBounds(x,y,width,30);
        comboBox.setVisible(true);
        frame.add(comboBox);

        return comboBox;
    }

    public static JLabel newLabel(int x, int y, String content, JFrame frame){
        JLabel label = new JLabel(content);
        label.setBounds(x,y,200,30);
        label.setVisible(true);
        frame.add(label);

        return label;
    }

    public static JLabel newSmallLabel(int x, int y, String content, JPanel frame){
        JLabel label = new JLabel(content);
        label.setBounds(x,y,100,30);
        label.setVisible(true);
        frame.add(label);

        return label;
    }

    public static JLabel newLabel(int x, int y, String content, JPanel frame){
        JLabel label = new JLabel(content);
        label.setBounds(x,y,200,30);
        label.setVisible(true);
        frame.add(label);

        return label;
    }

    public static JButton newButton(int x, int y, String content, JFrame frame){
        JButton button = new JButton(content);
        button.setBounds(x,y,100,30);
        button.setVisible(true);
        frame.add(button);

        return button;
    }

    public static JButton newButton(int x, int y, String content, JPanel frame){
        JButton button = new JButton(content);
        button.setBounds(x,y,100,30);
        button.setVisible(true);
        frame.add(button);

        return button;
    }

    public static JButton newButton(int x, int y, int width, String content, JPanel frame){
        JButton button = new JButton(content);
        button.setBounds(x,y,width,30);
        button.setVisible(true);
        frame.add(button);

        return button;
    }

    public static JRadioButton newRadioButton(int x, int y, String title, JPanel frame){
        JRadioButton button = new JRadioButton(title);
        button.setBounds(x,y,120,30);
        button.setVisible(true);
        frame.add(button);

        return button;
    }

}
