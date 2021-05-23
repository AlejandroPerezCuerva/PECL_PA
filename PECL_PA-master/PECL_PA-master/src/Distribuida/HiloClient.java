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
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class HiloClient extends Thread {

    private ArrayList<DataInputStream> arrayEntradas; //Array de todas las entradas que tiene el cliente
    private ArrayList<JTextField> arrayJTexField;//ArrayList de todos los JTextField de la interfaz cliente para poder modificarlos
    private Socket cliente;//Socket que tiene el cliente con el servidor
    private JTextArea colaRecepcion, colaSalaDescanso;//JTextArea para poder modificarlo
    private DataOutputStream salida, mensaje; //Salidas de mensajes del cliente hacia el servidor
    private JTextField salidaTextField;//JTextField de la salida del hospital

    /**
     * Constructor de la clase Hilo Cliente
     *
     * @param cliente conexión con el cliente
     * @param arrayJTexField ArrayList de todos los JtextField
     * @param colaRecepcion JTextArea de la cola de la recepción
     * @param colaSalaDescanso JTextArea de la cola de la sala de descanso
     * @param salidaTextField JTextField de la salida del hospital
     */
    public HiloClient(Socket cliente, ArrayList<JTextField> arrayJTexField, JTextArea colaRecepcion, JTextArea colaSalaDescanso, JTextField salidaTextField) {
        this.arrayEntradas = new ArrayList<>();
        this.cliente = cliente;
        this.arrayJTexField = arrayJTexField;
        this.colaRecepcion = colaRecepcion;
        this.colaSalaDescanso = colaSalaDescanso;
        this.salidaTextField = salidaTextField;
    }

    /**
     * Método principal de la clase hilo cliente, como es un hilo, toda su
     * función está en un run. Aquí se inicializan todos los Inputs que tiene y
     * los guarda en un arrayList Tiene un bucle que es verdadero mientras
     * exista la conexión con el servidor y en el bucle lo que se hace es
     * recibir todos los mensajes que envia el servidor Es muy importante que la
     * cantidad de mensajes que se envían y se reciben sean los mismos en el
     * cliente que en el servidor porque si hay algún mensaje que se envía
     * demás, el programa se queda totalmente bloqueado, ya que al ser sockets
     * TCP se necesita una conexión sincrona
     */
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
