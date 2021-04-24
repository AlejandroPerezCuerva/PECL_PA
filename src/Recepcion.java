
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
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
    private Queue<Paciente> colaEspera = new ConcurrentLinkedQueue<Paciente>(); //Elegimos este tipo de cola porque es el más cómodo de utilizar

    private SalaVacunacion salaVacunacion; //Sala necesaria para que los pacientes pasen de recepcion a vacunacion
    private SalaObservacion salaObservacion; //Sala necesaria para los pacientes que no están registrados
    private ListaThreads colaEspera2;

    private AtomicInteger contadorRegistrar = new AtomicInteger(0);
    private int numeroGanador, sumador;

    public Recepcion(JTextArea colaRecepcion, JTextField pacienteRecepcion, JTextField auxiliarRecepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion) {
        this.colaRecepcion = colaRecepcion;
        this.pacienteRecepcion = pacienteRecepcion;
        this.auxiliarRecepcion = auxiliarRecepcion;
        this.salaObservacion = salaObservacion;
        this.salaVacunacion = salaVacunacion;
        colaEspera2 = new ListaThreads(colaRecepcion);
        sumador = 100; //utilizamos una variable para que cada vez que se actualice, elija un rango nuevo
        numeroGanador = (int) (sumador * Math.random()) + 1; //Se elige un número aleatorio entre 100 y ese será el paciente que no pase el registro
    }

    //En este metodo se recibe un paciente que va a ser ingresado a la cola de espera de la recepción
    public void meterColaEspera(Paciente paciente) {
        colaEspera.offer(paciente); //Se mete al paciente en la cola
        synchronized (colaEspera) {
            colaEspera.notify();
        }
        colaRecepcion.setText(colaEspera.toString()); //Mostramos en la cola de espera los pacientes que tenemos
    }

    public void meterColaEspera2(Paciente paciente) {       //Prueba con ListaThreads, ignorar ya que seguramente se borre la clase y lo relativo a ella,
        colaEspera2.introducir(paciente); //Se mete al paciente en la cola
        colaEspera2.imprimir(); //Mostramos en la cola de espera los pacientes que tenemos
    }

    //Metodo donde se registran los pacientes y el Aux1 indica si pueden seguir o no
    public void registrarPacientes(Auxiliar auxiliar1) {

        //El auxiliar se pone en su puesto cuando llega por primera vez o viene de un descanso
        auxiliarRecepcion.setText(auxiliar1.toString());
        //Primero ponemos el contador del auxiliar 1 a 0
        auxiliar1.getContadorAux1().set(0);

        System.out.println("Num aleatorio: " + numeroGanador);
        while (!colaEspera.isEmpty() && auxiliar1.getContadorAux1().getAndIncrement() <= 10) {
            //Mientras la cola no esté vacía y el contador sea menor que 10, se sigue registrando pacientes
            try {
                Paciente paciente = (Paciente) colaEspera.poll(); //Con esto lo saca de la cola y lo borra
                pacienteRecepcion.setText(paciente.toString());
                colaRecepcion.setText(colaEspera.toString()); //Cuando se coge a un paciente se actualiza la cola de espera 
                auxiliar1.currentThread().sleep((int) (500 * Math.random() + 500)); //Tarda entre 0,5 y 1s en registrarse

                //Aquí he pensado en generar un número aleatorio entre 100 y el que coincida con el ID del paciente va fuera y cada 100 pacientes se actualiza
                if (paciente.getNumero() != numeroGanador) {
                    paciente.getRegistrado().set(true);
                    System.out.println("Paciente " + paciente.toString() + " vacunado en el puesto " );
                    synchronized (paciente.getRegistrado()) {
                        paciente.getRegistrado().notify();
                    }

                } else {
                    paciente.getRegistrado().set(false);
                    System.out.println("Paciente " + paciente.toString() + " ha acudido sin cita");
                }

                synchronized (colaEspera) {
                    colaEspera.wait();
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Recepcion.class.getName()).log(Level.SEVERE, null, ex);
            }
            elegirPacienteParaEchar();
        }
        auxiliarRecepcion.setText(""); //Actualizamos el JTextField para que se aprecie cuando el A1 se va al descanso
    }

    //Se deja en un método el elegir un número para saber a que paciente no pasa el registro en la recepción
    public void elegirPacienteParaEchar() {
        //Si el contador no llega a 100 se le suma uno, si llega a 100 se pone a 0 y se elige el próximo paciente que no será registrado
        //En un 1% de los casos, el auxiliar tiene que echar a un paciente fuera del hospital
        if (contadorRegistrar.get() < 100) {
            contadorRegistrar.getAndIncrement();
        } else {
            contadorRegistrar.set(0);
            numeroGanador = (int) (100 * Math.random()) + 1; //Elegimos otro número
            numeroGanador = numeroGanador + sumador; //Con esto se eligen numeros en rangos de 100 para tener así un 1% exacto en cada 100 pacientes
        }
    }

}
