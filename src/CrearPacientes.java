
import static java.lang.Thread.sleep;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Gonzalez Garcia
 */
public class CrearPacientes extends Thread {

    /*
    Se necesita un crear Pacientes porque el sleep se tiene que hacer aqui y no se puede interrumpir al main.
    si se hace el sleep en pacientes el problema es que los 2000 pacientes se crean a la vez, hacen el sleep y los 2000 entran de golpe a la recepcion
    por eso hay que crear una clase para crear los pacientes y que se creen con un sleep para que lleguen de forma escalonada
     */

    private Recepcion recepcion;
    private ExecutorService pool = Executors.newCachedThreadPool(); //Se crean los hilos con un pool para luego poder controlar la vida de ellos 

    public CrearPacientes(Recepcion recepcion) {
        this.recepcion = recepcion;
    }

    public void run() {
        //Se crean los 2000 pacientes con un sleep de 1 a 3 segundos de forma aleatoria para que entren de forma ordenada y escalonada
        for (int i = 0; i < 2000; i++) {
            Paciente pacienteNuevo = new Paciente(i, recepcion);
            try {
                sleep((int) (2000 * Math.random() + 1000)); //Los pacientes esperan entre 1 y 3 segundos para llegar de forma escalonada
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            pool.execute(pacienteNuevo);
        }
        pool.shutdown();
    }

}
