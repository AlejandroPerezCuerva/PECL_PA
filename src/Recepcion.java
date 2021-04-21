
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class Recepcion {

    private JTextField pacienteRecepcion, auxiliarRecepcion; //Recepción tiene todos los JTextField necesarios, los recibe del main
    private JTextArea colaRecepcion;
    private BlockingQueue colaEspera = new LinkedBlockingDeque(); //Elegimos este tipo de cola porque es el más cómodo de utilizar
    
    private SalaVacunacion salaVacunacion; //Sala necesaria para que los pacientes pasen de recepcion a vacunacion
    private ListaThreads colaEspera2;   
    
    public Recepcion(JTextArea colaRecepcion, JTextField pacienteRecepcion, JTextField auxiliarRecepcion) {
        this.colaRecepcion = colaRecepcion;
        this.pacienteRecepcion = pacienteRecepcion;
        this.auxiliarRecepcion = auxiliarRecepcion;
        colaEspera2= new ListaThreads(colaRecepcion);  
    }

    //En este metodo se recibe un paciente que va a ser ingresado a la cola de espera de la recepción
    public void meterColaEspera(Paciente paciente) {
        try {
            colaEspera.put(paciente); //Se mete al paciente en la cola
            colaRecepcion.setText(colaEspera.toString()); //Mostramos en la cola de espera los pacientes que tenemos
        } catch (InterruptedException ex) {
            Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void meterColaEspera2(Paciente paciente) {       //Prueba con ListaThreads, ignorar ya que seguramente se borre la clase y lo relativo a ella,
            colaEspera2.introducir(paciente); //Se mete al paciente en la cola
            colaEspera2.imprimir(); //Mostramos en la cola de espera los pacientes que tenemos
    }
    
    //Metodo donde se registran los pacientes y el Aux1 indica si pueden seguir o no
    public void registrarPacientes(Auxiliar auxiliar1) {
        //Primero ponemos el contador del auxiliar 1 a 0
        auxiliar1.getContadorAux1().set(0);
        //Mientras el contador del auxiliar 1 sea menor que 10, sigue registrando pacientes
        while (auxiliar1.getContadorAux1().getAndIncrement() <= 10) { //Comprobamos e incrementamos a la vez
            //En un 1% de los casos, el auxiliar tiene que echar a un paciente fuera del hospital
            auxiliarRecepcion.setText(auxiliar1.toString());
            try {
                Paciente paciente=(Paciente) colaEspera.take();
                pacienteRecepcion.setText(paciente.toString());
                colaRecepcion.setText(colaEspera.toString()); //Cuando se coge a un paciente se actualiza la cola de espera 
                auxiliar1.currentThread().sleep((int) (500 * Math.random() + 500)); //Tarda entre 0,5 y 1s en registrarse
                if ((int) (100 * Math.random())<99) { //Se hace una comprobación para ver si el paciente esta citado o no, el 99% de los pacientes estarán citados
                    salaVacunacion.entraPaciente(paciente);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        auxiliarRecepcion.setText(""); //Actualizamos el JTextField para que se aprecie cuando el A1 se va al descanso
    }

}
