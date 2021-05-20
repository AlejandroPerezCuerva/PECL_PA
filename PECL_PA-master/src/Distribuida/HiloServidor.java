package Distribuida;

import Principal.Recepcion;
import Principal.SalaObservacion;
import Principal.SalaDescanso;
import Principal.SalaVacunacion;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class HiloServidor extends Thread {

    private ServerSocket server;
    private Socket conexion;
    private Recepcion recepcion;
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private SalaDescanso salaDescanso;

    public HiloServidor(Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, SalaDescanso salaDescanso) {
        this.recepcion = recepcion;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.salaDescanso = salaDescanso;
    }

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
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
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
