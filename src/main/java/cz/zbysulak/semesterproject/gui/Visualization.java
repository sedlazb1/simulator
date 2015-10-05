package cz.zbysulak.semesterproject.gui;

import cz.zbysulak.semesterproject.MyLog;
import cz.zbysulak.semesterproject.network.MyServer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

/**
 * frame with settings of simulation and showing whole simulation progress.
 *
 * @author Zbyšek
 */
public class Visualization extends JFrame {

    private static final Logger log = Logger.getLogger(Visualization.class.getName());
    private ActionListener doStep;
    private int zoom;
    private MyServer server;
    private final int rows, colls;
    private final int numberOfClients;
    private String[] fields;
    private int zeroX, zeroY, pressX, pressY;

    private JPanel field;
    private JSlider slider;
    private JToggleButton runBtn;
    private JButton oneStepBtn, resetBtn;
    private Timer timer;
    private JTextField roundNumber;
    private JTextField fps;

    /**
     * creates new instance of Frame.
     *
     * @param server instance of server with all clients
     * @param rows width of each part (of each client)
     * @param colls height of each part
     * @param numberOfClients number of clients
     */
    public Visualization(final MyServer server, final int rows, final int colls, int numberOfClients) {
        super("Simulator");

        MyLog.setLogging(log);
        this.server = server;
        this.rows = rows;
        this.colls = colls;
        this.numberOfClients = numberOfClients;
        zeroX = 0;
        zeroY = 0;
        fields = server.getFields();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                for (int j = 0; j < fields[i].length(); j++) {
                    sb.append(fields[i].charAt(j));
                    if (j % colls == (colls - 1)) {
                        sb.append("\n");
                    }
                }
                sb.append("\n");
            }
        }
        log.log(Level.FINE, "current field:\n" + sb.toString());
        doStep = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (server.areReady()) {
                    log.log(Level.FINE, "I've told clients to do next step");
                    server.broadcast("donextstep");
                    fields = server.getFields();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fields.length; i++) {
                        if (fields[i] != null) {
                            for (int j = 0; j < fields[i].length(); j++) {
                                sb.append(fields[i].charAt(j));
                                if (j % colls == (colls - 1)) {
                                    sb.append("\n");
                                }
                            }
                            sb.append("\n");
                        }
                    }
                    log.log(Level.FINE, "current field:\n" + sb.toString());
                    roundNumber.setText(Integer.toString(Integer.parseInt(roundNumber.getText()) + 1));
                    repaint();  // Refresh the JFrame, callback paintComponent()
                }
            }
        };
        initComponents();
        timer = new Timer(slider.getValue(), doStep);
        zoom = 3;

        log.log(Level.FINER, "Simulator frame has been showed");

        this.setVisible(true);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        field = new AnimalCanvas();
        field.setPreferredSize(new Dimension(500, 500));
        field.setBackground(new Color(0, 153, 0));
        field.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                zoom -= e.getWheelRotation();
                if (zoom > 5) {
                    zoom = 5;
                }
                if (zoom < 1) {
                    zoom = 1;
                }
                log.log(Level.FINEST, "Zoom has been set to " + Integer.toString(zoom));
                repaint();
            }
        });
        field.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                zeroX = me.getX() - pressX;
                zeroY = me.getY() - pressY;
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
            }
        });
        field.addMouseListener(new MouseInputListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
                if (me.getClickCount() == 2 && !me.isConsumed()) {
                    me.consume();
                    zeroX = zeroY = 0;
                    log.log(Level.FINEST, "doubleclick");
                }
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent me) {
                pressX = me.getX() - zeroX;
                pressY = me.getY() - zeroY;
                log.log(Level.FINEST, this.getClass().getName() + "mouse pressed");
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }

            @Override
            public void mouseDragged(MouseEvent me) {
                log.log(Level.FINEST, "mouse dragging");
                zeroX = me.getX();
                zeroY = me.getY();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
            }
        });
        field.setFocusable(true);

        runBtn = new JToggleButton("Run");
        runBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (runBtn.isSelected()) {
                    timer.start();
                    runBtn.setText("Stop");
                    oneStepBtn.setEnabled(false);
                    log.log(Level.FINEST, "simulation started");
                } else {
                    timer.stop();
                    runBtn.setText("Run");
                    oneStepBtn.setEnabled(true);
                    log.log(Level.FINEST, "simulation stopped");
                }
            }
        });
        runBtn.setPreferredSize(new Dimension(150, 30));
        runBtn.setMaximumSize(runBtn.getPreferredSize());
        runBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        oneStepBtn = new JButton("Do one step");
        oneStepBtn.addActionListener(doStep);
        oneStepBtn.setPreferredSize(new Dimension(150, 30));
        oneStepBtn.setMaximumSize(oneStepBtn.getPreferredSize());
        oneStepBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetBtn = new JButton("Restart");
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                server.restart();
                fields = server.getFields();
                roundNumber.setText("0");
                repaint();
            }
        });
        resetBtn.setPreferredSize(new Dimension(150, 30));
        resetBtn.setMaximumSize(resetBtn.getPreferredSize());
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        slider = new JSlider();
        slider = new JSlider(100, 500, 150);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                timer.setDelay(600 - slider.getValue());
                fps.setText((1000 / (600 - slider.getValue())) + "fps");
            }
        });
        slider.setPreferredSize(new Dimension(150, 30));
        slider.setMaximumSize(slider.getPreferredSize());

        fps = new JTextField();
        fps.setPreferredSize(new Dimension(50, 20));
        fps.setMaximumSize(fps.getPreferredSize());
        fps.setEditable(false);
        fps.setText((1000 / (600 - slider.getValue())) + "fps");
        JPanel fpsPanel = new JPanel();
        fpsPanel.add(new JLabel("Frequency:  "));
        fpsPanel.add(fps);
        fpsPanel.setPreferredSize(new Dimension(150, 30));
        fpsPanel.setMaximumSize(fpsPanel.getPreferredSize());

        roundNumber = new JTextField("0");
        roundNumber.setPreferredSize(new Dimension(50, 20));
        roundNumber.setMaximumSize(roundNumber.getPreferredSize());
        roundNumber.setEditable(false);
        JPanel roundNoPanel = new JPanel();
        roundNoPanel.add(new JLabel("Round number:"));
        roundNoPanel.add(roundNumber);
        roundNoPanel.setPreferredSize(new Dimension(150, 30));
        roundNoPanel.setMaximumSize(roundNoPanel.getPreferredSize());

        JPanel setPanel = new JPanel();
        setPanel.setLayout(new BoxLayout(setPanel, BoxLayout.Y_AXIS));
        setPanel.add(runBtn);
        setPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        setPanel.add(oneStepBtn);
        setPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        setPanel.add(resetBtn);
        setPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        setPanel.add(slider);
        setPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        setPanel.add(fpsPanel);
        setPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        setPanel.add(roundNoPanel);

        Container cp = getContentPane();
        cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
        cp.add(Box.createRigidArea(new Dimension(5, 5)));
        cp.add(setPanel);
        cp.add(Box.createRigidArea(new Dimension(5, 5)));
        cp.add(field);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    private class AnimalCanvas extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setFont(new Font("TimesRoman", Font.PLAIN, zoom * 10));
            for (int h = 0; h < numberOfClients; h++) {//prochazi postupne stringy od klientu
                for (int i = 0; i < (rows * colls); i++) {//prochazi stringy
                    int x = (i % colls) * 10 * zoom + (h * colls) * zoom * 10 + zeroX;
                    int y = (i / colls) * zoom * 10 + zeroY;
                    log.log(Level.FINEST, "kreslim na souradnice x=" + x + ", y=" + y + "\nz " + h + "tyho klienta, " + i + "tej znak :" + fields[h].charAt(i));
                    switch (fields[h].charAt(i)) {
                        case 'F':
                            g.setColor(Color.orange);
                            g.drawString("F", x, y + zoom * 10);
                            break;
                        case 'R':
                            g.setColor(new Color(102, 51, 0));
                            g.drawString("R", x, y + zoom * 10);
                            break;
                        case 'H':
                            g.setColor(Color.RED);
                            g.drawString("H", x, y + zoom * 10);
                            break;
                        case 'G':
                            g.setColor(new Color(0, 204, 0));
                            g.drawString("G", x, y + zoom * 10);
                            break;
                        case 'T':
                            g.setColor(new Color(0, 102, 0));
                            g.drawString("T", x, y + zoom * 10);
                            break;
                        case 'C':
                            g.setColor(new Color(62, 62, 62));
                            g.drawString("C", x, y + zoom * 10);
                            break;
                        case '.':
                            g.setColor(new Color(0, 153, 0));
                            break;
                        case 'B':
                            g.setColor(new Color(255, 255, 102));
                            g.drawString("B", x, y + zoom * 10);
                            break;
                        default:
                            g.setColor(new Color(153, 76, 0));
                            g.drawString("W", x, y + zoom * 10);
                            break;
                    }
                    g.drawRect(x, y, zoom * 10 - 1, zoom * 10 - 1);
                }
            }
        }
    }
}
