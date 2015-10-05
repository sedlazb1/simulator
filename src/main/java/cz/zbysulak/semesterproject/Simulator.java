package cz.zbysulak.semesterproject;

import cz.zbysulak.semesterproject.gui.ServerOrClient;

/**
 * main class of project
 *
 * @author ZbyĹˇek
 */
public class Simulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ServerOrClient();
            }
        });
    }
}
