/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Distribuida;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alvaro Gonzalez Garcia
 */
public class HiloClient extends Thread {

    private ArrayList<DataInputStream> arrayEntradas = new ArrayList<>();
    private ArrayList<JTextField> arrayJTexField;
    private Socket cliente;
    private JTextArea colaRecepcion, colaSalaDescanso;
    private DataOutputStream salida;
    private JButton botonRefrescar;

    public HiloClient(Socket cliente, ArrayList<JTextField> arrayJTexField, JTextArea colaRecepcion, JTextArea colaSalaDescanso, JButton botonRefrescar) {
        this.cliente = cliente;
        this.arrayJTexField = arrayJTexField;
        this.colaRecepcion = colaRecepcion;
        this.colaSalaDescanso = colaSalaDescanso;
        this.botonRefrescar = botonRefrescar;
    }

    public void run() {
        for (int i = 0; i < 36; i++) {
            DataInputStream entrada = null;
            try {
                entrada = new DataInputStream(cliente.getInputStream());
                arrayEntradas.add(entrada);
            } catch (IOException ex) {
                Logger.getLogger(HiloClient.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        while (true) {
            botonRefrescar.setEnabled(true);
            try {
                try {
                    //Lo que primero nos llega son los datos de la recepción
                    colaRecepcion.setText(arrayEntradas.get(0).readUTF());
                    for (int i = 0; i < arrayJTexField.size(); i++) {
                        arrayJTexField.get(i).setText(arrayEntradas.get(i + 1).readUTF());
                    }
                    //Por ultimo llega el string de la sala de descanso
                    colaSalaDescanso.setText(arrayEntradas.get(35).readUTF());
                } catch (IOException ex) {
                    Logger.getLogger(HiloClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(HiloClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
