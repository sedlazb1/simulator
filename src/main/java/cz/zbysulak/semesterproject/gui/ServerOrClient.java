package cz.zbysulak.semesterproject.gui;

import cz.zbysulak.semesterproject.network.MyClient;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * you have to choose if this program will be server or client.
 * @author Zbyšek
 */
public class ServerOrClient extends JFrame {
    
    private JButton runBtn;
    private JRadioButton server;
    private JRadioButton client;

    public ServerOrClient() {
        super("Choose server or client");
        initComponents();
        this.setVisible(true);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        server = new JRadioButton("Server");
        server.setSelected(true);
        client = new JRadioButton("Client");
        ButtonGroup group = new ButtonGroup();
        group.add(server);
        group.add(client);
        runBtn = new JButton("Run!");
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(server.isSelected()){
                    new Launcher();
                }else{
                    new MyClient();
                    //run client - to bude asi nejakej upravenej World();
                }
                setVisible(false);
            }
        });
        JPanel radio = new JPanel();
        radio.add(server);
        radio.add(client);
        this.setLayout(new BorderLayout());
        this.add(radio, BorderLayout.NORTH);
        this.add(runBtn, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

}
