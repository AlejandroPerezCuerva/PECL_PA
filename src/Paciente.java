
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aleja
 */
public class Paciente extends Thread {

    private String id;
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato
    private int numero;
    private AtomicBoolean registrado, reaccionVacuna;
    private int puesto;
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;
    private Semaphore semRegistrar; //Objeto compartido con el auxiliar 1 para que espere mientra se hace el registro de la vacuna
    private Semaphore pacienteVacunado;
    private Semaphore semObservar=new Semaphore(0);

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
    }

    public void run() {
        try {
            recepcion.meterColaEspera(this); //Cuando un paciente llega se mete en la cola de

            //El paciente esperará hasta que el auxiliar 1 verifique que tiene cita para la vacuna, gracias al semaforo compartido entre el paciente y el auxiliar
            this.semRegistrar.acquire(); //Condición de esepera

            //Una vez registrados pasamos al puesto de vacunación o a la calle porque no ha acudido sin cita
            if (registrado.get()) {
                salaVacunacion.entraPaciente(this);

                pacienteVacunado.acquire(); //Hasta que el paciente no ha terminado de vacunarse no estra a la sala de observación

                salaObservacion.entraPaciente(this);
                salaObservacion.pacienteEnObservacion(this);
                semObservar.acquire();//Espera mientras es observado
                System.out.println("me voy");
                salaObservacion.salirHospital(this); //Una vez que el paciente ha sido observado sale del hospital

            } else {
                //Si no ha sido registrado directamente sale del hospital 
                salaObservacion.salirHospital(this);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Paciente.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public Semaphore getSemObservar(){
        return semObservar;
    }
    @Override
    public String toString() {
        return id;
    }

}
