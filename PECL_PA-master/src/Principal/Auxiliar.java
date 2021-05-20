package Principal;


import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Esta clase crea un Auxiliar que tendrá un id el cual determinará el tipo de auxiliar y por tanto lo que hará,
 * un contador en función del tipo de auxiliar y los atributos necesarios para realizar sus funciones.
 * Un auxiliar de tipo 1 registrará pacientes y comprobará si tienen cita o no, mientras que uno de tipo 2
 * creará vacunas
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class Auxiliar extends Thread {

    private String id;//ID que indicará el tipo de auxiliar y las funciones que realizará
    private Recepcion recepcion; //auxiliar 1 necesita tener recepcion porque ahí es donde tiene que estar
    private AtomicInteger contadorAux1; //Contador que llevan los auxiliar1
    private AtomicInteger contadorAux2; //Contador que llevan los auxiliar2
    private SalaVacunacion salaVacunacion; //El auxiliar 2 necesita la sala vacunacion
    private SalaDescanso salaDescanso;//Ambos auxiliares necesitan la sala descanso para descansar
    private Semaphore semRegistrar;//Semáforo  para asegurar el registro de pacientes en exclusión mútua

 /**
 * Constructor del Auxiliar de tipo 1
 * @param num El parámetro num define que número se usará para inicializar el atributo id
 * @param contadorAux1 El parámetro contadorAux1 define el número de pacientes que ha registrado desde el último descanso
 * @param recepcion El parámetro recepcion corresponde con la recepción en donde trabaja
 * @param salaDescanso El parámetro salaDescanso corresponde con la sala de descanso en la que descansa
 * @param semRegistrar El parámetro semRegistrar se usa para asegurar la exclusión mútua y que solo atienda a un paciente a la vez
 */
    public Auxiliar(int num, AtomicInteger contadorAux1, Recepcion recepcion, SalaDescanso salaDescanso, Semaphore semRegistrar) {
        this.id = "A" + num;
        this.contadorAux1 = contadorAux1;
        this.recepcion = recepcion;
        this.salaDescanso = salaDescanso;
        this.semRegistrar = semRegistrar;
    }
/**
 * Constructor del Auxiliar de tipo 2
 * @param num El parámetro num define que número se usará para inicializar el atributo id
 * @param contadorAux2 El parámetro contadorAux2 define el número de vacunas que ha creado desde el último descanso
 * @param recepcion El parámetro recepcion corresponde con la recepción en donde trabaja
 * @param salaDescanso El parámetro salaDescanso corresponde con la sala de descanso en la que descansa
 */
    public Auxiliar(int num, AtomicInteger contadorAux2, SalaVacunacion salaVacunacion, SalaDescanso salaDescanso) {
        id = "A" + num;
        this.contadorAux2 = contadorAux2;
        this.salaVacunacion = salaVacunacion;
        this.salaDescanso = salaDescanso;
    }

    @Override
    /**
     * 
     */
    public void run() {
        while (true) {
            try {
                if (this.id.equalsIgnoreCase("A1")) {
                    recepcion.registrarPacientes(this);
                    salaDescanso.descansoAuxiliares(this, 5000, 3000); //Auxiliar 1 se va a descansar cuando hace 10 registros, le pasamos los tiempos de descanso
                } else {
                    salaVacunacion.introducirDosis(this);
                    salaDescanso.descansoAuxiliares(this, 4000, 1000); //Auxiliar 2 se va a descansar cuando coge 10 dosis, le pasamos los tiempos de descanso
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Auxiliar.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Auxiliar.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
  
    @Override
    /**
     * Función que devuelve el id del auxiliar
     */
    public String toString() {
        return id;
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
    
}
