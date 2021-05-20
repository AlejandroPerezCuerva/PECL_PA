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

    /*
    Se necesita un crear Pacientes porque el sleep se tiene que hacer aqui y no se puede interrumpir al main.
    si se hace el sleep en pacientes el problema es que los 2000 pacientes se crean a la vez, hacen el sleep y los 2000 entran de golpe a la recepcion
    por eso hay que crear una clase para crear los pacientes y que se creen con un sleep para que lleguen de forma escalonada
     */
    private Recepcion recepcion;
    private ExecutorService pool = Executors.newCachedThreadPool(); //Se crean los hilos con un pool para luego poder controlar la vida de ellos 
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private Semaphore semRegistrar;

    public CrearPacientes(Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, Semaphore semRegistrar) {
        this.recepcion = recepcion;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.semRegistrar = semRegistrar;
    }

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
