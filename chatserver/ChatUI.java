package chatserver;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
   
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ChatUI {
    private ChatClient client;
    private static IChatServer server;
   
    public void doConnect() {
        if (connect.getText().equals("Connect")) {
            if (name.getText().length() < 2) {
                JOptionPane
                        .showMessageDialog(frame, "You need to type a name.");
                return;
            }
            if (ip.getText().length() < 2) {
                JOptionPane.showMessageDialog(frame, "You need to type an IP.");
                return;
            }
            try {
                client = new ChatClient(name.getText());
                client.setGUI(this);
                server = (IChatServer) Naming.lookup("rmi://localhost/chatserver");
                server.login(client);
                updateUsers(server.getConnected());
                connect.setText("Disconnect");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "ERROR, we wouldn't connect....");
            }
        } else {
            try {
                server.removeUser(client.getName());
                updateUsers(server.getConnected());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            //updateUsers(null);
            connect.setText("Connect");
        }
    }
   
    public void sendText() {
        if (connect.getText().equals("Connect")) {
            JOptionPane.showMessageDialog(frame, "You need to connect first.");
            return;
        }
        String st = tf.getText();
        st = "[" + name.getText() + "] " + st;
        tf.setText("");
        // Remove if you are going to implement for remote invocation
        try {
            server.publish(st);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    public void writeMsg(String st) {
        tx.setText(tx.getText() + "\n" + st);
    }
   
    public void updateUsers(ArrayList<IChatClient> users) {
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        if (users != null)
            for (int i = 0; i < users.size(); i++) {
                try {
                    String tmp = ((IChatClient) users.get(i)).getName();
                    listModel.addElement(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        list.setModel(listModel);
    }

    public static void main(String[] args) {
        System.out.println("Hello World !");
        ChatUI c = new ChatUI();
    }
   
    // User Interface code.
    public ChatUI() {
        frame = new JFrame("Group Chat");
        JPanel main = new JPanel();
        JPanel top = new JPanel();
        JPanel cn = new JPanel();
        JPanel bottom = new JPanel();
        ip = new JTextField();
        tf = new JTextField();
        name = new JTextField();
        tx = new JTextArea();
        connect = new JButton("Connect");
        JButton bt = new JButton("Send");
        list = new JList();
        main.setLayout(new BorderLayout(5, 5));
        top.setLayout(new GridLayout(1, 0, 5, 5));
        cn.setLayout(new BorderLayout(5, 5));
        bottom.setLayout(new BorderLayout(5, 5));
        top.add(new JLabel("Your name: "));
        top.add(name);
        top.add(new JLabel("Server Address: "));
        top.add(ip);
        top.add(connect);
        cn.add(new JScrollPane(tx), BorderLayout.CENTER);
        cn.add(list, BorderLayout.EAST);
        bottom.add(tf, BorderLayout.CENTER);
        bottom.add(bt, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        main.add(cn, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Events
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doConnect();
            }
        });
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });
        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });
        frame.setContentPane(main);
        frame.setSize(600, 600);
        frame.setVisible(true);
    }
   
    JTextArea tx;
    JTextField tf, ip, name;
    JButton connect;
    JList list;
    JFrame frame;
}
