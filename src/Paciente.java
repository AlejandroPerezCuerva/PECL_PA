
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author aleja
 */
public class Paciente extends Thread {

    private String id;
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato
    private int numero;
    private AtomicBoolean registrado, reaccionVacuna, vacunado;
    private int puesto;
    private SalaVacunacion salaVacunacion;
    private SalaObservacion salaObservacion;

    public Paciente(int numero, Recepcion recepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion) {
        this.numero = numero;
        id = "P" + String.format("%04d", numero);
        this.recepcion = recepcion;
        this.registrado = new AtomicBoolean(false);
        this.reaccionVacuna = new AtomicBoolean(false);
        this.vacunado = new AtomicBoolean(false);
        this.puesto = 0;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
    }

    public void run() {
        try {
            recepcion.meterColaEspera(this); //Cuando un paciente llega se mete en la cola de 

            //Esperamos mientras el auxliar nos indica al puesto que tenemos que ir o si no tenemos cita
            synchronized (this.registrado) {
                this.registrado.wait();
            }

            //Una vez registrados pasamos al puesto de vacunación o a la calle porque no ha acudido sin cita
            if (registrado.get()) {
                salaVacunacion.entraPaciente(this);
                //Hay que hacer que el paciente espere
                
                salaObservacion.entraPaciente(this);
                salaObservacion.pacienteEnObservacion(this);
                salaObservacion.salirHospital(this); //Una vez que el paciente ha sido observado sale del hospital

            } else {
                //Si no ha sido registrado directamente sale del hospital 
                salaObservacion.salirHospital(this);
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

    public void setId(String id) {
        this.id = id;
    }

    public Recepcion getRecepcion() {
        return recepcion;
    }

    public void setRecepcion(Recepcion recepcion) {
        this.recepcion = recepcion;
    }

    public AtomicBoolean getVacunado() {
        return vacunado;
    }

    public void setVacunado(AtomicBoolean vacunado) {
        this.vacunado = vacunado;
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

    @Override
    public String toString() {
        return id;
    }

}
