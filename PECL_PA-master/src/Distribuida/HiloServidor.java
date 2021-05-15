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
 * @author aleja
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
}
