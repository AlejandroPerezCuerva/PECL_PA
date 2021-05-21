package Principal;

import static java.lang.Thread.sleep;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class CrearPacientes extends Thread {

    private Recepcion recepcion; //Recepción que luego se le pasa como parámetro a los pacientes
    private ExecutorService pool; //Se crean los hilos con un pool para luego poder controlar la vida de ellos 
    private SalaVacunacion salaVacunacion; //Sala de vacunación para pasarsela a los pacientes
    private SalaObservacion salaObservacion; //Sala de observación para pasarsela a los pacientes
    private Semaphore semRegistrar; //Semáforo que comparten los pacientes con el auxiliar 1 para que pasen de 1 en 1

    /**
     * Constructor de la clase que crea los pacientes de 1 a 3 segundos
     *
     * @param recepcion Recepción para pasarsela a los pacientes
     * @param salaVacunacion Sala de vacunación para pasarsela a los pacientes
     * @param salaObservacion Sala de observación para pasarsela a los pacientes
     * @param semRegistrar Semáforo para los pacientes que comparten con el
     * auxiliar 1
     */
    public CrearPacientes(Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, Semaphore semRegistrar) {
        this.recepcion = recepcion;
        this.pool = Executors.newCachedThreadPool();
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.semRegistrar = semRegistrar;
    }

    /**
     * Método run de la clase crear pacientes que su función es ir creando los
     * pacientes con un periodo de 1 a 3 segundos. Se crean con un pool para
     * luego poder controlar la vida de los pacientes
     */
    public void run() {
        //Se crean los 2000 pacientes con un sleep de 1 a 3 segundos de forma aleatoria para que entren de forma ordenada y escalonada
        for (int i = 0; i < 2000; i++) {
            Paciente pacienteNuevo = new Paciente(i, recepcion, salaVacunacion, salaObservacion, semRegistrar);
            try {
                sleep((int) (2000 * Math.random() + 1000)); //Los pacientes esperan entre 1 y 3 segundos para llegar de forma escalonada
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            pool.execute(pacienteNuevo);
        }
        pool.shutdown();
    }

    public Recepcion getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Recepcion recepcion) {
        this.recepcion = recepcion;
    }

    public ExecutorService getPool() {
        return pool;
    }

    public void setPool(ExecutorService pool) {
        this.pool = pool;
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

    public Semaphore getSemRegistrar() {
        return semRegistrar;
    }

    public void setSemRegistrar(Semaphore semRegistrar) {
        this.semRegistrar = semRegistrar;
    }

}
