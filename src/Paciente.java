
import java.util.concurrent.atomic.AtomicBoolean;

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

    public Paciente(int numero, Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion) {
        this.numero = numero;
        id = "P" + String.format("%04d", numero);
        this.recepcion = recepcion;
        this.registrado = new AtomicBoolean(false);
        this.reaccionVacuna = new AtomicBoolean(false);
        this.puesto = 0;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
    }

    public void run() {
        try {//sleep antes de entrar a recepcion y evitar CrearPacientes? //Si se hace un sleep aqui el problema es que se crean los 2000 pacientes de golpe y 
            //todos hacen un sleep y entran a la vez, por eso hay que crear una clase de CrearPacientes
            recepcion.meterColaEspera(this);

            //Esperamos mientras el auxliar nos indica al puesto que tenemos que ir o si no tenemos cita
            synchronized (this.registrado) {
                this.registrado.wait();
            }

            //Una vez registrados pasamos al puesto de vacunación o a la calle porque no ha acudido sin cita
            if (registrado.get()) {
                salaVacunacion.entraPaciente(this);
                //Aquí viene la sala de observación para después de la vacuna
                synchronized (this.registrado) {
                    this.registrado.wait();
                }
                salaObservacion.entraPaciente(this);
            }
        } catch (Exception e) {
            System.out.println("La has liado");
        }
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

    @Override
    public String toString() {
        return id;
    }

}
