package Principal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class Sanitario extends Thread {

    private String id; //Identificador del sanitario
    private SalaDescanso salaDescanso; //Se le pasa como parámetro porque necesita acceder a ella
    private SalaVacunacion salaVacunacion; //Se le pasa como parámetro porque necesita acceder a ella
    private SalaObservacion salaObservacion; //Se le pasa como parámetro porque necesita acceder a ella
    private ArrayList<AtomicInteger> contadoresSanitarios; //Cada sanitario tiene un contador para llevar el control de los pacientes que atiende
    private int puesto, numeroSanitario; //Puesto en el que están en la sala de observación y número de sanitario
    private AtomicBoolean sanitarioDescansaDistribuida; //Es un booleano para que el sanitario cuando esté descansado porque se lo ha dicho el cliente, 
    //vaya directamente a colocarse otra vez en el puesto y no vaya a atender a alguien que le ha dado reaccion

    /**
     * Constructor del sanitario
     *
     * @param num Número del identificador
     * @param salaDescanso Clase donde descansan los sanitarios
     * @param salaVacunacion Clase donde realizan la acción de vacunar
     * @param salaObservacion Clase donde atienden a un paciente que tiene
     * reacción
     * @param contadoresSanitarios Contador que tiene cada sanitario para saber
     * cuantos pacientes ha vacunado
     */
    public Sanitario(int num, SalaDescanso salaDescanso, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, ArrayList<AtomicInteger> contadoresSanitarios) {
        id = "S" + String.format("%02d", num);
        this.salaDescanso = salaDescanso;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.contadoresSanitarios = contadoresSanitarios;
        this.numeroSanitario = num - 1;
        this.puesto = 0;
        this.sanitarioDescansaDistribuida = new AtomicBoolean(false);
    }

    /**
     * En el método run la función que tiene el sanitario lo primero es
     * cambiarse, y luego tiene un bucle infinito donde su misión es colocarse
     * en un puesto de vacunación, vacunar a 15 pacientes a menos que se limpie
     * la sala de descanso. Después de vacunar, se van a descansar y después de
     * descansar miran a ver si algún paciente tiene reacción a la vacuna para
     * poder curarle y que se vaya bien a casa. En este último paso hay que
     * tener en cuenta que si el sanitario sale de la sala de descanso porque lo
     * ha dicho el cliente, tiene que volver a vacunar a 15 pacientes y no tiene
     * que atender a los pacientes con reacción
     */
    public void run() {
        try {
            //Lo primero que hacen es ir a la sala de descanso donde tardan en cambiarse entre 1 y 3 segundos
            salaDescanso.cambiarseSanitario(this);
            //Ahora los sanitarios se tienen que ir a la sala de vacunación para meterse cada uno en su puesto
            while (true) {
                salaVacunacion.colocarSanitarios(this);
                salaVacunacion.vacunarPaciente(this);
                salaDescanso.descansoSanitarios(this, 8000, 5000); //El sanitario descansa entre 5 y 8 segundos

                //Si la cola de pacientes de reaccion no está vacía significa que hay un paciente que requiere de un sanitario
                //El sanitario solo puede estar cuando el booleano este a false porque sino tiene que colocarse en el puesto despues de limpiar el puesto en distribuida
                if (!salaObservacion.getPacientesObservacion().isEmpty() && !sanitarioDescansaDistribuida.get()) {
                    salaObservacion.atenderPaciente(this);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Sanitario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sanitario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<AtomicInteger> getContadoresSanitarios() {
        return contadoresSanitarios;
    }

    public void setContadoresSanitarios(ArrayList<AtomicInteger> contadoresSanitarios) {
        this.contadoresSanitarios = contadoresSanitarios;
    }

    @Override
    public String toString() {
        return id;
    }

    public int getNumeroSanitario() {
        return numeroSanitario;
    }

    public void setNumeroSanitario(int numeroSanitario) {
        this.numeroSanitario = numeroSanitario;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AtomicBoolean getSanitarioDescansaDistribuida() {
        return sanitarioDescansaDistribuida;
    }

    public void setSanitarioDescansaDistribuida(AtomicBoolean sanitarioDescansaDistribuida) {
        this.sanitarioDescansaDistribuida = sanitarioDescansaDistribuida;
    }

    public SalaDescanso getSalaDescanso() {
        return salaDescanso;
    }

    public void setSalaDescanso(SalaDescanso salaDescanso) {
        this.salaDescanso = salaDescanso;
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

    public int getPuesto() {
        return puesto;
    }

    public void setPuesto(int puesto) {
        this.puesto = puesto;
    }

}
