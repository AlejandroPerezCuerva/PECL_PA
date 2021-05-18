package Distribuida;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private DataOutputStream salida, mensaje;
    private JTextField salidaTextField;

    public HiloClient(Socket cliente, ArrayList<JTextField> arrayJTexField, JTextArea colaRecepcion, JTextArea colaSalaDescanso, JTextField salidaTextField) {
        this.cliente = cliente;
        this.arrayJTexField = arrayJTexField;
        this.colaRecepcion = colaRecepcion;
        this.colaSalaDescanso = colaSalaDescanso;
        this.salidaTextField = salidaTextField;
    }

    public void run() {
        try {
            for (int i = 0; i < 37; i++) {
                DataInputStream entrada = null;
                entrada = new DataInputStream(cliente.getInputStream());
                arrayEntradas.add(entrada);
            }
            salida = new DataOutputStream(cliente.getOutputStream());//Se crea el canal de salida
            mensaje = new DataOutputStream(cliente.getOutputStream());
            while (cliente.isConnected()) {
                //Se envia un booleano de false porque todavía no se ha cerrado la conexión
                //Lo que primero nos llega son los datos de la recepción
                colaRecepcion.setText(arrayEntradas.get(0).readUTF());
                for (int i = 0; i < arrayJTexField.size(); i++) {
                    arrayJTexField.get(i).setText(arrayEntradas.get(i + 1).readUTF());
                }
                //Por ultimo llega el string de la sala de descanso
                colaSalaDescanso.setText(arrayEntradas.get(35).readUTF());
                salidaTextField.setText(arrayEntradas.get(36).readUTF());

                salida.writeBoolean(false);
                mensaje.writeInt(-1);

                sleep(1000);
            }
            for (int i = 0; i < arrayEntradas.size(); i++) {
                arrayEntradas.get(i).close();
            }
            salida.close();
            mensaje.close();
            cliente.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HiloClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<DataInputStream> getArrayEntradas() {
        return arrayEntradas;
    }

    public void setArrayEntradas(ArrayList<DataInputStream> arrayEntradas) {
        this.arrayEntradas = arrayEntradas;
    }

    public ArrayList<JTextField> getArrayJTexField() {
        return arrayJTexField;
    }

    public void setArrayJTexField(ArrayList<JTextField> arrayJTexField) {
        this.arrayJTexField = arrayJTexField;
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public JTextArea getColaRecepcion() {
        return colaRecepcion;
    }

    public void setColaRecepcion(JTextArea colaRecepcion) {
        this.colaRecepcion = colaRecepcion;
    }

    public JTextArea getColaSalaDescanso() {
        return colaSalaDescanso;
    }

    public void setColaSalaDescanso(JTextArea colaSalaDescanso) {
        this.colaSalaDescanso = colaSalaDescanso;
    }

    public DataOutputStream getSalida() {
        return salida;
    }

    public void setSalida(DataOutputStream salida) {
        this.salida = salida;
    }

    public DataOutputStream getMensaje() {
        return mensaje;
    }

    public void setMensaje(DataOutputStream mensaje) {
        this.mensaje = mensaje;
    }

    public JTextField getSalidaTextField() {
        return salidaTextField;
    }

    public void setSalidaTextField(JTextField salidaTextField) {
        this.salidaTextField = salidaTextField;
    }

}
