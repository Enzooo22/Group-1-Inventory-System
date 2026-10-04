import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.*;

public class Main extends JFrame implements ActionListener {


    private DefaultListModel <String> model;
    private LinkedList <String> linkedList;
    private JList <String> list;
    private JTextField txt;
    private JButton add, del, upd;
    private JScrollPane scrollPane;

    Main() {
        setTitle("Inventory System");
        setSize(300, 350);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        linkedList = new LinkedList<>();
        model = new DefaultListModel<>();
        list = new JList<>(model);

        add = new JButton("Add");
        add.setBounds(180, 200, 100, 30);
        add(add);

        del = new JButton("Remove");
        del.setBounds(180, 240, 100, 30);
        add(del);

        upd = new JButton("Update");
        upd.setBounds(180, 280, 100, 30);
        add(upd);

        scrollPane = new JScrollPane(list);
        scrollPane.setBounds(10, 10, 280, 180);
        add(scrollPane);

        txt = new JTextField();
        txt.setBounds(10, 200, 160, 30);
        add(txt);

        add.addActionListener(this);
        del.addActionListener(this);
        upd.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add) {
            String item = txt.getText();

            if (!item.isEmpty()) {
                linkedList.add(item);
                model.addElement(item);
                txt.setText("");
            }
        }

        if (e.getSource() == del) {
            int selectedIndex = list.getSelectedIndex();

            if (selectedIndex != -1) {
                linkedList.remove(selectedIndex);
                model.remove(selectedIndex);
            }
        }

        if (e.getSource() == upd) {
            int selectedIndex = list.getSelectedIndex();
            String item = txt.getText();

            if (selectedIndex != -1 && !item.isEmpty()) {
                linkedList.set(selectedIndex, item);
                model.set(selectedIndex, item);
                txt.setText("");
            }
        }
    }

}