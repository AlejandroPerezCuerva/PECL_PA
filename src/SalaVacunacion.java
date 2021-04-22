
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
    private ArrayList<Puesto> puestos= new ArrayList<Puesto>();
    private JTextField auxiliarVacunacion, numeroVacunas;
    private AtomicInteger contadorVacunas = new AtomicInteger(0);
    private SalaObservacion salaObservacion;    //Sala necesaria para que los pacientes pasen de vacunar a observar
    private BlockingQueue colaEspera = new LinkedBlockingDeque();//Cola de espera hasta que el auxiliar indique a que puesto ir
    
    public SalaVacunacion(int max, JTextField auxiliarVacunacion, JTextField numeroVacunas) {
        this.max = max;
        for (int i = 0; i < max; i++) {
            Puesto nuevoPuesto=  new Puesto(new JTextField(), true);
            puestos.add(nuevoPuesto);
        }       
        this.auxiliarVacunacion = auxiliarVacunacion;
        this.numeroVacunas = numeroVacunas;
    }

    //Método para el auxiliar 2, genera 20 dosis con el periodo indicado. Tiene un contador que llega hasta 20
    public void introducirDosis(Auxiliar auxiliar2) {   /****Hacer de introducirDosis el run de A2?          *****/
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
    
    public void entraPaciente(Paciente paciente){
        //procedimiento para meter al paciente en la salaVacunacion
        try {
            colaEspera.put(paciente); //Se mete al paciente en la cola
        } catch (InterruptedException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void salePaciente(Paciente p){
        //procedimiento para sacar al paciente de salaVacunacion
        salaObservacion.entraPaciente(p);
    }
}
