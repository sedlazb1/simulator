/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zbysulak.semesterproject;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 *
 * @author Uživatel
 */
public class MyLog {

    private static final Level level = Level.FINER;

    public static void setLogging(Logger log) {

        Handler stdout = new StreamHandler(System.out, new SimpleFormatter()) {
            @Override
            public void publish(LogRecord record) {
                super.publish(record);
                flush();
            }
        };
        /*try {
            FileHandler fh = new FileHandler("log.txt", true);
            fh.setLevel(level);
            log.addHandler(fh);
        } catch (IOException ex) {
            Logger.getLogger(MyLog.class.getName()).log(Level.SEVERE, "Security exception while initialising logger: " + ex.getMessage());
        } catch (SecurityException ex) {
            Logger.getLogger(MyLog.class.getName()).log(Level.SEVERE, "IO exception while initialising logger: " + ex.getMessage());
        }*/
        log.addHandler(stdout);
        log.setLevel(level);
        stdout.setLevel(level);
    }
}
