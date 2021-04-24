
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author aleja
 */
public class Paciente extends Thread {

    private String id;
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato
    private int numero;
    private AtomicBoolean registrado;
    private int puesto;

    public Paciente(int numero, Recepcion recepcion) {
        this.numero = numero;
        id = "P" + String.format("%04d", numero);
        this.recepcion = recepcion;
        this.registrado = new AtomicBoolean(false);
        this.puesto = 0;
    }

    public void run() {
        try {//sleep antes de entrar a recepcion y evitar CrearPacientes? //Si se hace un sleep aqui el problema es que se crean los 2000 pacientes de golpe y 
            //todos hacen un sleep y entran a la vez, por eso hay que crear una clase de CrearPacientes
            recepcion.meterColaEspera(this);
            
            //Esperamos mientras el auxliar nos indica al puesto que tenemos que ir o si no tenemos cita
            synchronized (this.registrado) {
                this.registrado.wait();
            }

            if (registrado.get()) {
                
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
