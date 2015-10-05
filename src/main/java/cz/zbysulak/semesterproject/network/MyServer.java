package cz.zbysulak.semesterproject.network;

import cz.zbysulak.semesterproject.MyLog;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 * is thread. it creates server socket at port 27048.
 *
 * @author Zbyšek
 */
public class MyServer extends Thread {

    private static final Logger log = Logger.getLogger(MyServer.class.getName());
    private final int PORT = 27048;
    private ServerSocket server;
    private ArrayList<ClientThread> clients;
    private JTextField tf;
    private boolean waiting;
    private String[] fields;
    private int rows, colls;
    private String initString;

    public MyServer() {
        MyLog.setLogging(log);
        fields = new String[5];
    }

    /**
     * set size of each part of simulation
     *
     * @param rows number of rows
     * @param colls number of collumns
     */
    public void setSize(int rows, int colls) {
        this.rows = rows;
        this.colls = colls;
    }

    /**
     *
     * @param i initial string for start of simulation
     */
    public void setInitString(String i) {
        initString = i;
    }

    /**
     * restart simulation with same initial values
     */
    public void restart() {
        broadcast(initString);
    }

    /**
     *
     * @return all fields from clients.
     */
    public String[] getFields() {
        return fields;
    }

    /**
     *
     * @param waiting if is server waiting for clients to connect.
     */
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /**
     * textfield for info about clients
     *
     * @param tf
     */
    public void addTextField(JTextField tf) {
        this.tf = tf;
        tf.setText("0");
    }

    /**
     *
     * @return true if all clients are done with their work.
     */
    public boolean areReady() {
        boolean ret = true;
        for (ClientThread clt : clients) {
            if (clt.isbusy) {
                ret = false;
            }
        }
        return ret;
    }

    @Override
    public void start() {
        waiting = true;
        clients = new ArrayList<>();
        try {
            server = new ServerSocket(PORT);
            super.start();
            log.log(Level.FINE, "Server is switched on");
        } catch (IOException ex) {
            log.log(Level.WARNING, "Server was not switched on (It can be caused by multiple servers at one computer)");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket s = server.accept(); //přijmout klienta
                if (waiting) {
                    ClientThread newclient = new ClientThread(s); //vytvořit vlákno
                    clients.add(newclient); //přidat klienta do seznamu
                    newclient.start(); //spustit vlákno
                    tf.setText(Integer.toString(clients.size()));
                    if (fields.length < clients.size()) {
                        fields = new String[fields.length + 5];
                    }
                }
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "can't create new thread for client");
        } finally {
            if (server != null) {
                for (ClientThread clt : getClients()) {
                    clt.close();
                }
                clients.clear();
                try {
                    server.close();
                } catch (IOException e) {
                    log.log(Level.INFO, "Can't close server");
                }
                log.log(Level.FINE, "server closed");
            }
        }
    }

    /**
     * send message to all connected clients. then wait for response (done or
     * some request)
     *
     * @param message
     */
    public synchronized void broadcast(String message) {
        for (ClientThread clt : getClients()) {
            String send = message;
            if (message.equals("donextstep")) {
                StringBuilder left = new StringBuilder();
                StringBuilder right = new StringBuilder();
                for (int i = 0; i < rows; i++) {
                    if (getClients().size() == 1) {
                        left.append("T");
                        right.append("T");
                    } else if (getClients().indexOf(clt) == 0) {
                        left.append("T");
                        right.append(fields[getClients().indexOf(clt) + 1].charAt(colls * i));
                    } else if (getClients().indexOf(clt) == (getClients().size() - 1)) {
                        left.append(fields[getClients().indexOf(clt) - 1].charAt((colls * i) + colls - 1));
                        right.append("T");
                    } else {
                        left.append(fields[getClients().indexOf(clt) - 1].charAt((colls * i) + colls - 1));
                        right.append(fields[getClients().indexOf(clt) + 1].charAt(colls * i));
                    }
                }
                send = message + "," + left.toString() + "," + right.toString();
            }
            System.out.println(send);
            clt.out.println(send);
            clt.isbusy = true;
            clt.out.flush();
        }
        try {
            wait(100);
        } catch (InterruptedException ex) {

        }
    }

    public synchronized List<ClientThread> getClients() {
        return clients;
    }

    public void send(ClientThread to, String message) {
        to.out.println(message);
        to.out.flush();
    }

    /**
     * thread for every connected client.
     */
    public class ClientThread extends Thread {

        Socket socket;
        PrintStream out;
        BufferedReader in;
        private boolean isbusy;

        public ClientThread(Socket socket) {
            isbusy = false;
            this.socket = socket;
            try {
                out = new PrintStream(socket.getOutputStream()); //vytvořit PrintStream
                in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //vytvořit BufferedReader
            } catch (IOException e) {
                close();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = in.readLine();
                    log.log(Level.FINE, "got message:'" + message + "'");
                    if (message.startsWith("done")) {
                        log.log(Level.FINEST, "client told he is ready");
                        fields[clients.indexOf(this)] = message.split(",")[1];
                        isbusy = false;
                    } else if (message.startsWith("border")) {
                        if (getClients().size() == 1) {
                            String toSend = "gimmefield";
                            send(this, toSend);
                        } else if (getClients().indexOf(this) == 0) {
                            send(getClients().get(getClients().indexOf(this) + 1), "repairrightonly," + message.split(",")[1]);
                        } else if (getClients().indexOf(this) == getClients().size() - 1) {
                            send(getClients().get(getClients().indexOf(this) - 1), "repairrightonly," + message.split(",")[1]);
                        } else {
                            send(getClients().get(getClients().indexOf(this) + 1), "repairright," + message.split(",")[1]);
                            send(getClients().get(getClients().indexOf(this) - 1), "repairleft," + message.split(",")[2]);
                        }
                    }
                }
            } catch (IOException e) {
                log.log(Level.INFO, "client got disconnected");
                getClients().remove(this);
                tf.setText(Integer.toString(clients.size()));
            } finally {
                close(); //odpojit
                if(!waiting){
                    log.log(Level.SEVERE, "Application had to be closed because one of client has left.");
                    System.exit(0);
                }
            }
        }

        public void close() {
            getClients().remove(this); //vymazat ze seznamu
            try {
                out.close(); //zavřít výstupní proud
                in.close(); //zavřít vstupní proud
                socket.close(); //zavřít soket
            } catch (IOException e) {
            }
        }
    }
}
