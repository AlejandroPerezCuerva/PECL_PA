package Principal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aleja
 */
public class Sanitario extends Thread {

    private String id;
    private SalaDescanso salaDescanso; //Se le pasa como parámetro porque necesita acceder a ella
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private ArrayList<AtomicInteger> contadoresSanitarios;
    private int puesto, numeroSanitario;//****numeroSanitario sobra?*****/

    public Sanitario(int num, SalaDescanso salaDescanso, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, ArrayList<AtomicInteger> contadoresSanitarios) {
        id = "S" + String.format("%02d", num);
        this.salaDescanso = salaDescanso;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.contadoresSanitarios = contadoresSanitarios;
        this.numeroSanitario = num - 1;
        this.puesto = 0;
    }

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
                if (!salaObservacion.getPacientesObservacion().isEmpty()) {
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
