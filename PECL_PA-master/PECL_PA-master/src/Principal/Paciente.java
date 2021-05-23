package Principal;

import java.io.IOException;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class Paciente extends Thread {

    private String id; //Identificador que tiene el paciente
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato
    private int numero; //Número del identificador
    private AtomicBoolean registrado, reaccionVacuna; //Booleanos para saber si han sido registrados con éxito y si les ha dado reacción la vacuna
    private int puesto;  //Número donde se le indica el puesto al que tienen que ir
    private SalaVacunacion salaVacunacion; //Sala donde se tienen que vacunar
    private SalaObservacion salaObservacion; //Sala donde están una vez que se han vacunado
    private Semaphore semRegistrar; //Objeto compartido con el auxiliar 1 para que espere mientra se hace el registro de la vacuna
    private Semaphore pacienteVacunado; //Semáforo para saber si el paciente ha sido vacunado y espere mientras le vacunen
    private Semaphore semObservar; //Semáforo para saber si ha ido todo bien en la observación o ha tenido reacción a la vacuna

    /**
     * Constructor de los paciente. También se inicializan los atributos
     * necesarios para el correcto funcionamiento del programa
     *
     * @param numero Número del paciente que se le asigna al identificador
     * @param recepcion Recepción donde se registra el paciente
     * @param salaVacunacion Sala de vacunación donde se vacuna el paciente
     * @param salaObservacion Sala de observación donde el paciente espera por
     * si le da reacción la vacuna
     * @param semRegistrar Semáforo que comparte con el auxiliar para que se
     * registren de 1 en 1 y se indique si ha sido con éxito o no
     */
    public Paciente(int numero, Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, Semaphore semRegistrar) {
        this.numero = numero;
        id = "P" + String.format("%04d", numero);
        this.recepcion = recepcion;
        this.registrado = new AtomicBoolean(false);
        this.reaccionVacuna = new AtomicBoolean(false);
        this.puesto = 0;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.semRegistrar = semRegistrar;
        this.pacienteVacunado = new Semaphore(0);
        this.semObservar = new Semaphore(0);
    }

    /**
     * Método run donde se indican los pasos que tiene que hacer el paciente
     * durante su estancia en el hospital. Primero se va a la cola de la
     * recepción para saber si está registrado o no, luego si ha sido registrado
     * pasa a la sala de vacunación donde un sanitario le vacuna. Espera
     * mientras le vacunan y después pasa a la sala de observación, aquí también
     * tiene que esperar por si le da reacción la vacuna o ha ido todo bien.
     * Después de todo el proceso el paciente abandona el hospital con su
     * dosis de la vacuna
     */
    public void run() {
        try {
            recepcion.meterColaEspera(this); //Cuando un paciente llega se mete en la cola de recepción

            //El paciente esperará hasta que el auxiliar 1 verifique que tiene cita para la vacuna, gracias al semaforo compartido entre el paciente y el auxiliar
            this.semRegistrar.acquire(); //Condición de esepera

            //Una vez registrados pasamos al puesto de vacunación o a la calle porque no ha acudido sin cita
            if (registrado.get()) {
                salaVacunacion.entraPaciente(this);

                pacienteVacunado.acquire(); //Hasta que el paciente no ha terminado de vacunarse no estra a la sala de observación

                salaObservacion.entraPaciente(this);
                salaObservacion.pacienteEnObservacion(this);
                semObservar.acquire();//Espera mientras es observado
                salaObservacion.salirHospital(this); //Una vez que el paciente ha sido observado sale del hospital

            } else {
                //Si no ha sido registrado directamente sale del hospital 
                salaObservacion.salirHospital(this);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Paciente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Paciente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setSemObservar(Semaphore semObservar) {
        this.semObservar = semObservar;
    }

    public Semaphore getPacienteVacunado() {
        return pacienteVacunado;
    }

    public void setPacienteVacunado(Semaphore pacienteVacunado) {
        this.pacienteVacunado = pacienteVacunado;
    }

    public AtomicBoolean getRegistrado() {
        return registrado;
    }

    public void setRegistrado(AtomicBoolean registrado) {
        this.registrado = registrado;
    }

    public AtomicBoolean getReaccionVacuna() {
        return reaccionVacuna;
    }

    public void setReaccionVacuna(AtomicBoolean reaccionVacuna) {
        this.reaccionVacuna = reaccionVacuna;
    }

    public int getPuesto() {
        return puesto;
    }

    public void setPuesto(int puesto) {
        this.puesto = puesto;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
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

    public Semaphore getSemObservar() {
        return semObservar;
    }

    @Override
    public String toString() {
        return id;
    }

}
