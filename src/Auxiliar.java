
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author aleja
 */
public class Auxiliar extends Thread {

    private String id;
    private Recepcion recepcion; //auxiliar 1 necesita tener recepcion porque ahí es donde tiene que estar
    private AtomicInteger contadorAux1; //Contador que llevan los auxiliar1
    private AtomicInteger contadorAux2; //Contador que llevan los auxiliar2
    private SalaVacunacion salaVacunacion; //El auxiliar 2 necesita la sala vacunacion
    private SalaDescanso salaDescanso;
    private Semaphore semRegistrar;

    public Auxiliar(int num, AtomicInteger contadorAux1, Recepcion recepcion, SalaDescanso salaDescanso, Semaphore semRegistrar) {
        id = "A" + num;
        this.contadorAux1 = contadorAux1;
        this.recepcion = recepcion;
        this.salaDescanso = salaDescanso;
        this.semRegistrar = semRegistrar;
    }

    public Auxiliar(int num, AtomicInteger contadorAux2, SalaVacunacion salaVacunacion, SalaDescanso salaDescanso) {
        id = "A" + num;
        this.contadorAux2 = contadorAux2;
        this.salaVacunacion = salaVacunacion;
        this.salaDescanso = salaDescanso;
    }

    @Override
    public void run() {
        while (true) {
            if (this.id.equalsIgnoreCase("A1")) {
                recepcion.registrarPacientes(this);
                salaDescanso.descansoAuxiliares(this, 5000, 3000); //Auxiliar 1 se va a descansar cuando hace 10 registros, le pasamos los tiempos de descanso
            } else {
                salaVacunacion.introducirDosis(this);
                salaDescanso.descansoAuxiliares(this, 4000, 1000); //Auxiliar 2 se va a descansar cuando coge 10 dosis, le pasamos los tiempos de descanso
            }
        }
    }

    public AtomicInteger getContadorAux1() {
        return contadorAux1;
    }

    public void setContadorAux1(AtomicInteger contadorAux) {
        this.contadorAux1 = contadorAux;
    }

    public AtomicInteger getContadorAux2() {
        return contadorAux2;
    }

    public void setContadorAux2(AtomicInteger contadorAux) {
        this.contadorAux2 = contadorAux;
    }

    public void setId(String id) {
        this.id = id;
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

    public SalaDescanso getSalaDescanso() {
        return salaDescanso;
    }

    public void setSalaDescanso(SalaDescanso salaDescanso) {
        this.salaDescanso = salaDescanso;
    }

    public Semaphore getSemRegistrar() {
        return semRegistrar;
    }

    public void setSemRegistrar(Semaphore semRegistrar) {
        this.semRegistrar = semRegistrar;
    }

    @Override
    public String toString() {
        return id;
    }
}
