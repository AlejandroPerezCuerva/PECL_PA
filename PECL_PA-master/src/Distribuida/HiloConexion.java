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
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
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
    private String stringDescanso = "", stringSalidaHospital = ""; //Como solo hay un JTextArea no hace falta array, con un string es suficiente
    private ArrayList<DataOutputStream> arraySalidas;

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
            arraySalidas = new ArrayList<>();
            //Creamos todos los outputs que vamos a necesitar
            for (int i = 0; i < 37; i++) {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                arraySalidas.add(salida);
            }
        } catch (IOException ex) {
            Logger.getLogger(HiloConexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {

        while (conexion.isConnected()) {
            try {
                //Recogemos todos los datos necesarios para enviar al cliente
                arrayRecepcion = recepcion.crearMensajeRecepcion();
                arrayVacunacion = salaVacunacion.crearMensajeVacunacion();
                arrayObservacion = salaObservacion.crearMensajeObservacion();
                stringDescanso = salaDescanso.crearMensajeDescanso();
                stringSalidaHospital = salaObservacion.getSalidaTextField().getText();

                //Enviamos todos los datos al cliente
                for (int i = 0; i < arrayRecepcion.size(); i++) {
                    arraySalidas.get(i).writeUTF(arrayRecepcion.get(i));
                }

                for (int i = 0; i < arrayVacunacion.size(); i++) {
                    arraySalidas.get(i + arrayRecepcion.size()).writeUTF(arrayVacunacion.get(i));
                }

                for (int i = 0; i < arrayObservacion.size(); i++) {
                    arraySalidas.get(i + arrayRecepcion.size() + arrayVacunacion.size()).writeUTF(arrayObservacion.get(i));
                }

                arraySalidas.get(35).writeUTF(stringDescanso);

                arraySalidas.get(36).writeUTF(stringSalidaHospital);

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

        try {
            //Cuando se desconecta es cuando se cierran todos los canales y conexión
            for (int i = 0; i < 37; i++) {
                arraySalidas.get(i).close();
            }
            entradaBooleano.close();
            entradaBoton.close();
            entradaInformar.close();
            conexion.close();
        } catch (IOException ex) {
            Logger.getLogger(HiloConexion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Socket getConexion() {
        return conexion;
    }

    public void setConexion(Socket conexion) {
        this.conexion = conexion;
    }

    public DataInputStream getEntradaBooleano() {
        return entradaBooleano;
    }

    public void setEntradaBooleano(DataInputStream entradaBooleano) {
        this.entradaBooleano = entradaBooleano;
    }

    public DataInputStream getEntradaBoton() {
        return entradaBoton;
    }

    public void setEntradaBoton(DataInputStream entradaBoton) {
        this.entradaBoton = entradaBoton;
    }

    public DataInputStream getEntradaInformar() {
        return entradaInformar;
    }

    public void setEntradaInformar(DataInputStream entradaInformar) {
        this.entradaInformar = entradaInformar;
    }

    public boolean isBoleanoBotones() {
        return boleanoBotones;
    }

    public void setBoleanoBotones(boolean boleanoBotones) {
        this.boleanoBotones = boleanoBotones;
    }

    public boolean isClienteInformar() {
        return clienteInformar;
    }

    public void setClienteInformar(boolean clienteInformar) {
        this.clienteInformar = clienteInformar;
    }

    public int getPuestoBoton() {
        return puestoBoton;
    }

    public void setPuestoBoton(int puestoBoton) {
        this.puestoBoton = puestoBoton;
    }

    public Recepcion getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Recepcion recepcion) {
        this.recepcion = recepcion;
    }

    public SalaVacunacion getSalaVacunacion() {
        return salaVacunacion;
    }

    public void setSalaVacunacion(SalaVacunacion salaVacunacion) {
        this.salaVacunacion = salaVacunacion;
    }

    public SalaObservacion getSalaObservacion() {
        return salaObservacion;
    }

    public void setSalaObservacion(SalaObservacion salaObservacion) {
        this.salaObservacion = salaObservacion;
    }

    public SalaDescanso getSalaDescanso() {
        return salaDescanso;
    }

    public void setSalaDescanso(SalaDescanso salaDescanso) {
        this.salaDescanso = salaDescanso;
    }

    public ArrayList<String> getArrayRecepcion() {
        return arrayRecepcion;
    }

    public void setArrayRecepcion(ArrayList<String> arrayRecepcion) {
        this.arrayRecepcion = arrayRecepcion;
    }

    public ArrayList<String> getArrayVacunacion() {
        return arrayVacunacion;
    }

    public void setArrayVacunacion(ArrayList<String> arrayVacunacion) {
        this.arrayVacunacion = arrayVacunacion;
    }

    public ArrayList<String> getArrayObservacion() {
        return arrayObservacion;
    }

    public void setArrayObservacion(ArrayList<String> arrayObservacion) {
        this.arrayObservacion = arrayObservacion;
    }

    public String getStringDescanso() {
        return stringDescanso;
    }

    public void setStringDescanso(String stringDescanso) {
        this.stringDescanso = stringDescanso;
    }

    public String getStringSalidaHospital() {
        return stringSalidaHospital;
    }

    public void setStringSalidaHospital(String stringSalidaHospital) {
        this.stringSalidaHospital = stringSalidaHospital;
    }

    public ArrayList<DataOutputStream> getArraySalidas() {
        return arraySalidas;
    }

    public void setArraySalidas(ArrayList<DataOutputStream> arraySalidas) {
        this.arraySalidas = arraySalidas;
    }

}
