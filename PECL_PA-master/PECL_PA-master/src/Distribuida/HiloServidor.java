package Distribuida;

import Principal.Recepcion;
import Principal.SalaObservacion;
import Principal.SalaDescanso;
import Principal.SalaVacunacion;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class HiloServidor extends Thread {

    private ServerSocket server; //Servidor que se especifica el puerto para la conexión con el cliente
    private Socket conexion;  //socket de la conexión con el cliente
    private Recepcion recepcion; //Recepción del hospital
    private SalaVacunacion salaVacunacion; //Sala de vacunación del hospital
    private SalaObservacion salaObservacion; //Sala de observación del hospital
    private SalaDescanso salaDescanso; //Sala de descanso del hospital

    /**
     * Constructor de la clase Hilo Servidor
     *
     * @param recepcion Recepción del hospital
     * @param salaVacunacion Sala de vacunación del hospital
     * @param salaObservacion Sala de observación del hospital
     * @param salaDescanso Sala de descanso del hospital
     */
    public HiloServidor(Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, SalaDescanso salaDescanso) {
        this.recepcion = recepcion;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.salaDescanso = salaDescanso;
    }

    /**
     * Método run del hilo servidor. Aquí se especifica el puerto que está
     * disponible el servidor para que el cliente se pueda conectar. Y en un
     * bucle infinito se van aceptando todas las conexiones con los diferentes
     * clientes que estén
     */
    public void run() {
        try {
            server = new ServerSocket(5002);
            //El hilo servidor va aceptando todas las peticiones que le entran de los clientes
            while (true) {
                conexion = server.accept();
                HiloConexion connect = new HiloConexion(conexion, recepcion, salaVacunacion, salaObservacion, salaDescanso);
                connect.start();
            }
        } catch (IOException ex) {
        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public Socket getConexion() {
        return conexion;
    }

    public void setConexion(Socket conexion) {
        this.conexion = conexion;
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

}
