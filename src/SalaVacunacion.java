
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaVacunacion {

    private int max; //Número máximo de pacientes en la sala de vacunación
    private ArrayList<Puesto> puestos = new ArrayList<Puesto>();
    private ArrayList<JTextField> puestosVacunacion;
    private JTextField auxiliarVacunacion, numeroVacunas;
    private AtomicInteger contadorVacunas = new AtomicInteger(0);
    private SalaObservacion salaObservacion;    //Sala necesaria para que los pacientes pasen de vacunar a observar
    private BlockingQueue colaEspera = new LinkedBlockingDeque();//Cola de espera hasta que el auxiliar indique a que puesto ir

    public SalaVacunacion(int max, JTextField auxiliarVacunacion, JTextField numeroVacunas, ArrayList<JTextField> puestosVacunacion) {
        this.max = max;
        this.puestosVacunacion = puestosVacunacion;
        for (int i = 0; i < puestosVacunacion.size(); i++) {
            Puesto nuevoPuesto = new Puesto(puestosVacunacion.get(i), true); //NO SE PUEDEN CREAR LOS JTEXTFIELD SE TIENEN QUE PASAR DEL MAIN
            puestos.add(nuevoPuesto);
        }
        this.auxiliarVacunacion = auxiliarVacunacion;
        this.numeroVacunas = numeroVacunas;
    }

    public ArrayList<Puesto> getPuestos() {
        return puestos;
    }

    public boolean puestoLibre() {
        boolean resultado = false;
        for (int i = 0; i < puestos.size(); i++) {
            resultado = resultado || puestos.get(i).isDisponible();//falso o dispoible ->true, falso o no disponible ->false
        }
        return resultado;
    }

    //Método para el auxiliar 2, genera 20 dosis con el periodo indicado. Tiene un contador que llega hasta 20
    /**
     * *
     * En introducirDosis comprobar simultaneamente que haya hueco en Vacunacion
     * y Observacion, cuando haya en ambos toma un permiso de ambos semáforos y
     * ocupa primero el puesto de vacunación y cuando acabe el de observación
     *
     **
     */
    public void introducirDosis(Auxiliar auxiliar2) {
        //Primero ponemos el contador del auxiliar 2 a 0
        auxiliar2.getContadorAux2().set(0);
        while (auxiliar2.getContadorAux2().incrementAndGet() <= 20) { //Comprobamos el contador y lo incrementamos
            try {
                auxiliarVacunacion.setText(auxiliar2.toString());
                numeroVacunas.setText(contadorVacunas.incrementAndGet() + ""); //Incrementamos en 1 el número de vacunas y lo sacamos en el JTextField correspondiente
                auxiliar2.currentThread().sleep((int) (500 * Math.random() + 500)); //Tiempo que tarda en preparar una dosis
            } catch (Exception e) {
            }
        }
        auxiliarVacunacion.setText(""); //Actualizamos el JTextField cuando el auxiliar se va a descansar
    }

    //Los sanitarios se colocan en el puesto que tengan libre
    public void colocarSanitarios(Sanitario sanitario) {
        int i = 0;
        // Hacemos un bucle para que el sanitario elija puesto, una vez elegido el puesto se queda interrumpido para que salga del bucle while y solo ocupe un puesto
        while (!sanitario.isInterrupted() && i < puestos.size()) {
            if (puestos.get(i).isDisponible()) {
                puestos.get(i).entraSanitario();
                puestos.get(i).getJtfPuesto().setText(sanitario.toString());
                sanitario.interrupt(); //Controlamos el comportamiento del sanitario
            }
            i++;
        }
    }

    public void entraPaciente(Paciente paciente) {
        //procedimiento para meter al paciente en la salaVacunacion
        //Aquí hay que meter al paciente en un puesto
        try {
            colaEspera.put(paciente); //Se mete al paciente en la cola
        } catch (InterruptedException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salePaciente(Paciente p) {
        //procedimiento para sacar al paciente de salaVacunacion
        salaObservacion.entraPaciente(p);
    }
}
