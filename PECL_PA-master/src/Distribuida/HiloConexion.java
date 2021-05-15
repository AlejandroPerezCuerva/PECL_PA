package Distribuida;

import Principal.Recepcion;
import Principal.SalaObservacion;
import Principal.SalaDescanso;
import Principal.SalaVacunacion;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aleja
 */
public class HiloConexion extends Thread {

    private Socket conexion;
    private DataInputStream entradaBooleano, entradaBoton, entradaInformar;
    private boolean boleanoBotones = false, clienteInformar = false;
    private int puestoBoton = 0;
    private Recepcion recepcion;
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private SalaDescanso salaDescanso;
    private ArrayList<String> arrayRecepcion, arrayVacunacion, arrayObservacion;
    private String stringDescanso; //Como solo hay un JTextArea no hace falta array, con un string es suficiente

    public HiloConexion(Socket conexion, Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, SalaDescanso salaDescanso) {
        try {
            this.conexion = conexion;
            this.recepcion = recepcion;
            this.salaVacunacion = salaVacunacion;
            this.salaObservacion = salaObservacion;
            this.salaDescanso = salaDescanso;
            entradaBooleano = new DataInputStream(conexion.getInputStream());
            entradaInformar = new DataInputStream(conexion.getInputStream());
            entradaBoton = new DataInputStream(conexion.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(HiloConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        while (true) {
            try {
                char clienteInformar = entradaInformar.readChar();

                //Recogemos todos los datos necesarios para enviar al cliente
                arrayRecepcion = recepcion.crearMensajeRecepcion();
                arrayVacunacion = salaVacunacion.crearMensajeVacunacion();
                arrayObservacion = salaObservacion.crearMensajeObservacion();
                stringDescanso = salaDescanso.crearMensajeDescanso();

                //Enviamos todos los datos al cliente
                for (int i = 0; i < arrayRecepcion.size(); i++) {
                    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                    salida.writeUTF(arrayRecepcion.get(i));
                }

                for (int i = 0; i < arrayVacunacion.size(); i++) {
                    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                    salida.writeUTF(arrayVacunacion.get(i));
                }

                for (int i = 0; i < arrayObservacion.size(); i++) {
                    DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                    salida.writeUTF(arrayObservacion.get(i));
                }

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                salida.writeUTF(stringDescanso);

                boleanoBotones = entradaBooleano.readBoolean();
                puestoBoton = entradaBoton.readInt();
                //Si está a true es porque se ha pulsado un botón
                if (boleanoBotones) {
                    boleanoBotones = false;
                    for (int i = 0; i < 10; i++) {
                        if (puestoBoton == i + 1) {
                            //Si esto ocurre hay que cerrar la sala del sanitario que está en el puesto i
                            salaVacunacion.cerrarPuesto(i);
                        }
                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(HiloConexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
