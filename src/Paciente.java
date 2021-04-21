
/**
 *
 * @author aleja
 */
public class Paciente extends Thread {

    private String id;
    private Recepcion recepcion; //Reciben la recepcion directamente del main para que sea la misma todo el rato

    public Paciente(int num, Recepcion recepcion) {
        id = "P" + String.format("%04d", num);
        this.recepcion = recepcion;
    }

    public void run() {
        try {
            recepcion.meterColaEspera(this);
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return id;
    }
    
    
}
