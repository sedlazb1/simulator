package cz.zbysulak.semesterproject.gui;

import cz.zbysulak.semesterproject.MyLog;
import cz.zbysulak.semesterproject.network.MyServer;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Enables settings of simulation
 *
 * @author Zbyšek
 */
public class Launcher extends JFrame {

    private static final Logger log = Logger.getLogger(Launcher.class.getName());
    private int ent1, ent2, ent3;
    private JSlider slider1, slider2, slider3;
    private JSlider sliderRows, sliderColls;
    private JTextField textClient;
    private JTextField textEnt1, textEnt2, textEnt3, textRows, textColls;
    private JButton runBtn;
    private JButton exitBtn;
    private JCheckBox square;
    private MyServer server;
    private int rows, colls;

    /**
     * Creates new instance of launcher (settings for simulation, loading
     * clients etc.)
     */
    public Launcher() {
        super("Simulator Launcher");
        MyLog.setLogging(log);
        initComponents();
        initServer();
        this.setVisible(true);
    }

    private void initServer() {
        server = new MyServer();
        server.addTextField(textClient);
        server.start();
        log.log(Level.FINE, "Server was switched on");
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JPanel sliderPanel = new JPanel(new GridLayout(0, 1));

        ent1 = 10;
        JPanel entPan1 = new JPanel();
        textEnt1 = new JTextField();
        textEnt1.setColumns(3);
        textEnt1.setEditable(false);
        textEnt1.setText(Integer.toString(ent1));
        slider1 = new JSlider(0, 40, ent1);
        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                ent1 = slider1.getValue();
                textEnt1.setText(Integer.toString(ent1));
            }
        });
        entPan1.add(new JLabel("Foxes:   "));
        entPan1.add(slider1);
        entPan1.add(textEnt1);
        entPan1.add(new JLabel("%"));

        ent2 = 20;
        JPanel entPan2 = new JPanel();
        textEnt2 = new JTextField();
        textEnt2.setColumns(3);
        textEnt2.setEditable(false);
        textEnt2.setText(Integer.toString(ent2));
        slider2 = new JSlider(0, 40, ent2);
        slider2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                ent2 = slider2.getValue();
                textEnt2.setText(Integer.toString(ent2));
            }
        });
        entPan2.add(new JLabel("Rabbits:"));
        entPan2.add(slider2);
        entPan2.add(textEnt2);
        entPan2.add(new JLabel("%"));

        ent3 = 5;
        JPanel entPan3 = new JPanel();
        textEnt3 = new JTextField();
        textEnt3.setColumns(3);
        textEnt3.setEditable(false);
        textEnt3.setText(Integer.toString(ent3));
        slider3 = new JSlider(0, 10, ent3);
        slider3.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                ent3 = slider3.getValue();
                textEnt3.setText(Integer.toString(ent3));
            }
        });
        entPan3.add(new JLabel("Hunters:"));
        entPan3.add(slider3);
        entPan3.add(textEnt3);
        entPan3.add(new JLabel("%"));

        sliderPanel.add(entPan1);
        sliderPanel.add(entPan2);
        sliderPanel.add(entPan3);
        sliderPanel.add(new JLabel("      Grass will be at 1/3 of remaining space"));
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Entities"));

        square = new JCheckBox("Square", true);
        square.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                if (square.isSelected()) {
                    sliderColls.setEnabled(false);
                } else {
                    sliderColls.setEnabled(true);
                }
            }
        });

        colls = 30;
        sliderColls = new JSlider(1, 50, colls / 10);
        sliderColls.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                colls = sliderColls.getValue() * 10;
                textColls.setText(Integer.toString(colls));
            }
        });
        sliderColls.setEnabled(false);
        textColls = new JTextField();
        textColls.setText(Integer.toString(colls));
        textColls.setEditable(false);
        textColls.setColumns(5);

        JPanel sizeYPanel = new JPanel();
        sizeYPanel.add(new JLabel("Size of field (collumns):"));
        sizeYPanel.add(sliderColls);
        sizeYPanel.add(textColls);

        rows = colls;
        sliderRows = new JSlider(1, 50, rows / 10);
        sliderRows.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                rows = sliderRows.getValue() * 10; //change to 100 later
                textRows.setText(Integer.toString(rows));
                if (square.isSelected()) {
                    sliderColls.setValue(sliderRows.getValue());
                }
            }
        });
        textRows = new JTextField();
        textRows.setText(Integer.toString(rows));
        textRows.setEditable(false);
        textRows.setColumns(5);

        JPanel sizeXPanel = new JPanel();
        sizeXPanel.add(new JLabel("Size of field (rows):"));
        sizeXPanel.add(sliderRows);
        sizeXPanel.add(textRows);

        JPanel sizePanel = new JPanel();
        sizePanel.setLayout(new BoxLayout(sizePanel, BoxLayout.Y_AXIS));
        sizePanel.add(sizeXPanel);
        sizePanel.add(square);
        sizePanel.add(sizeYPanel);
        sizePanel.setBorder(BorderFactory.createTitledBorder("Size of simulation field"));

        JPanel btnPanel = new JPanel();
        runBtn = new JButton("Run");
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (Integer.parseInt(textClient.getText()) != 0) {
                    int grass = (100 - ent1 - ent2 - ent3) / 3;
                    String message = "init," + (square.isSelected() ? rows : colls)
                            + "," + (rows / (Integer.parseInt(textClient.getText()))) + "," + ent1 + "," + ent2 + "," + ent3 + "," + grass + "," + (grass / 3);
                    log.log(Level.FINE, "Send this to clients:" + message);
                    server.broadcast(message);
                    server.setSize(rows, colls / (Integer.parseInt(textClient.getText())));
                    server.setWaiting(false);
                    server.setInitString(message);
                    new Visualization(server, rows, colls / (Integer.parseInt(textClient.getText())), Integer.parseInt(textClient.getText()));
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "At least one client have to be connected.");
                }
            }
        });

        exitBtn = new JButton("Exit");
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        btnPanel.add(runBtn);
        btnPanel.add(exitBtn);
        btnPanel.setBorder(BorderFactory.createTitledBorder("Buttons"));

        textClient = new JTextField();
        textClient.setEditable(false);
        textClient.setBorder(null);
        textClient.setText("0");

        JPanel clientPanel = new JPanel();
        clientPanel.add(new JLabel("Number of connected clients: "));
        clientPanel.add(textClient);
        clientPanel.setBorder(BorderFactory.createTitledBorder("Server info"));

        JPanel allComponents = new JPanel();
        allComponents.setLayout(new BoxLayout(allComponents, BoxLayout.Y_AXIS));
        allComponents.add(sliderPanel);
        allComponents.add(sizePanel);
        allComponents.add(btnPanel);
        allComponents.add(clientPanel);
        this.add(allComponents);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
}
