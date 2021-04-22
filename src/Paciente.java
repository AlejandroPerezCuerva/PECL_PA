
/**
 *
 * @author aleja
 */
public class Paciente extends Thread {

    private String id;
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato
    private int numero;

    public Paciente(int numero, Recepcion recepcion) {
        this.numero = numero;
        id = "P" + String.format("%04d", numero);
        this.recepcion = recepcion;
    }

    public void run() {
        try {//sleep antes de entrar a recepcion y evitar CrearPacientes? //Si se hace un sleep aqui el problema es que se crean los 2000 pacientes de golpe y 
            //todos hacen un sleep y entran a la vez, por eso hay que crear una clase de CrearPacientes
            recepcion.meterColaEspera(this);
        } catch (Exception e) {
        }
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
