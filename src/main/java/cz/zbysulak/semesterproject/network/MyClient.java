package cz.zbysulak.semesterproject.network;

import cz.zbysulak.semesterproject.MyLog;
import cz.zbysulak.semesterproject.simulation.World;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MyClient is client for socket comunication. it connect to localhost on port
 * 27048. after connecting it listens for server. and controls instance of
 * world.
 *
 * @author Zbyšek
 */
public class MyClient {

    private static final Logger log = Logger.getLogger(MyClient.class.getName());
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private World world;
    private String currentField;
    private String left, right;
    private String[] Nleft;
    private String[] Nright;

    public MyClient() {
        MyLog.setLogging(log);
        this.Connect("localhost", 27048);
        this.start();
    }

    /**
     *
     * @return current gamefield from World.
     */
    public String getCurrentField() {
        return currentField;
    }

    public String talkToServer(String message, boolean needAnswer) {
        out.println(message);
        out.flush();
        log.log(Level.FINE, "message sent");
        String resp = null;
        if (needAnswer) {
            try {
                resp = in.readLine();
                log.log(Level.FINE, "got response");
            } catch (IOException ex) {
                log.log(Level.INFO, "can't get response");
            }
        }
        return resp;
    }

    private void Connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            log.log(Level.FINE, " connected successfully");
        } catch (IOException ex) {
            log.log(Level.WARNING, " did not connect!");
        }
    }

    public char getLeft(int line) {
        return left.charAt(line);
    }

    public char getRight(int line) {
        return right.charAt(line);
    }

    public void setLeft(int line, String c) {
        Nleft[line] = c;
    }

    public void setRight(int line, String c) {
        Nright[line] = c;
    }

    private void start() {
        try {
            String fromServer;
            while (!(fromServer = in.readLine()).equals("end")) {
                log.log(Level.FINE, "got message:'" + fromServer + "'");
                if (fromServer.startsWith("donextstep")) {
                    left = fromServer.split(",")[1];
                    right = fromServer.split(",")[2];
                    world.nextRound();
                    StringBuilder Sleft = new StringBuilder();
                    StringBuilder Sright = new StringBuilder();
                    for (int i = 0; i < Nleft.length; i++) {
                        Sleft.append(Nleft[i] == null ? "." : Nleft[i]);
                        Sright.append(Nright[i] == null ? "." : Nright[i]);
                    }
                    String msg = "border," + Sleft.toString() + "," + Sright.toString();
                    log.log(Level.FINER, "sending message:'" + msg + "'");
                    this.out.println(msg);
                    this.out.flush();
                } else if (fromServer.startsWith("init")) {
                    String[] init = fromServer.split(",");
                    int[] initINT = new int[init.length];
                    try {
                        for (int i = 1; i < init.length; i++) {
                            initINT[i] = Integer.parseInt(init[i]);
                        }
                        Nleft = new String[initINT[1]];
                        Nright = new String[initINT[1]];
                        world = new World(initINT[1], initINT[2], initINT[3], initINT[4], initINT[5], initINT[6], initINT[7]);
                        currentField = world.print();
                        log.log(Level.FINEST, " world initialized, new field:\n" + currentField);
                        log.log(Level.FINER, "sending message:'done, " + currentField + "'");
                        this.out.println("done," + currentField);
                        this.out.flush();
                        world.setClient(this);
                    } catch (NumberFormatException e) {
                        log.log(Level.FINE, " did not get correct data");
                    }
                } else if (fromServer.equals("gimmefield")) {
                    currentField = world.print();
                    String msg = "done," + currentField;
                    log.log(Level.FINER, "sending message:'done," + currentField + "'");
                    this.out.println(msg);
                    this.out.flush();
                    log.log(Level.FINE, "sent server gamefield");
                } else if (fromServer.startsWith("repair")) {
                    if (fromServer.startsWith("repairrightonly")) {
                        world.repairBorders(null, fromServer.split(",")[1]);
                        currentField = world.print();
                        String msg = "done," + currentField;
                        log.log(Level.FINER, "sending message:'" + msg + "'");
                        this.out.println(msg);
                        this.out.flush();
                    } else if (fromServer.startsWith("repairleftonly")) {
                        world.repairBorders(fromServer.split(",")[1], null);
                        currentField = world.print();
                        String msg = "done," + currentField;
                        log.log(Level.FINER, "sending message:'" + msg + "'");
                        this.out.println(msg);
                        this.out.flush();
                    } else if (fromServer.startsWith("repairright")) {
                        right = fromServer.split(",")[1];
                        if (left != null) {
                            world.repairBorders(left, right);
                            currentField = world.print();
                            String msg = "done," + currentField;
                            log.log(Level.FINER, "sending message:'" + msg + "'");
                            this.out.println(msg);
                            this.out.flush();
                        }
                    } else if (fromServer.startsWith("repairleft")) {
                        left = fromServer.split(",")[1];
                        if (right != null) {
                            world.repairBorders(left, right);
                            currentField = world.print();
                            String msg = "done," + currentField;
                            log.log(Level.FINER, "sending message:'" + msg + "'");
                            this.out.println(msg);
                            this.out.flush();
                        }
                    }
                }
            }
            log.log(Level.FINE, " client end, server command");
        } catch (UnknownHostException e) {
            log.log(Level.WARNING, " Don't know about host " + socket.getInetAddress());
            System.exit(0);
        } catch (IOException e) {
            log.log(Level.WARNING, " Couldn't get I/O for the connection to " + socket.getInetAddress());
            System.exit(0);
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "Did not find server");
            e.printStackTrace();
            System.exit(0);
        }
    }

}
