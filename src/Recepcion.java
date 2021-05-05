
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class Recepcion {

    private JTextField pacienteRecepcion, auxiliarRecepcion; //Recepción tiene todos los JTextField necesarios, los recibe del main
    private JTextArea colaRecepcion; 
    
    private BlockingQueue colaEspera = new LinkedBlockingQueue(); //Elegimos este tipo de cola porque es el más cómodo de utilizar

    private SalaVacunacion salaVacunacion; //Sala necesaria para que los pacientes pasen de recepcion a vacunacion
    private SalaObservacion salaObservacion;

    private AtomicInteger contadorRegistrar = new AtomicInteger(0);
    private int numeroGanador, sumador;

    private Semaphore semEsperaPaciente = new Semaphore(0); //Semaforo que hace que el paciente espere mientras no haya nadie en la cola de espera en recepción
    private Semaphore semSalasOcupadas = new Semaphore(0); //Semaforo que hacer que el paciente espere si la sala de observación y vacunación están ocupadas

    public Recepcion(JTextArea colaRecepcion, JTextField pacienteRecepcion, JTextField auxiliarRecepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion) {
        this.colaRecepcion = colaRecepcion;
        this.pacienteRecepcion = pacienteRecepcion;
        this.auxiliarRecepcion = auxiliarRecepcion;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        sumador = 100; //utilizamos una variable para que cada vez que se actualice, elija un rango nuevo
        numeroGanador = (int) (sumador * Math.random()) + 1; //Se elige un número aleatorio entre 100 y ese será el paciente que no pase el registro
    }

    //En este metodo se recibe un paciente que va a ser ingresado a la cola de espera de la recepción
    public void meterColaEspera(Paciente paciente) throws InterruptedException {
        colaRecepcion.setText(colaEspera.toString()); //Mostramos en la cola de espera los pacientes que tenemos
        colaEspera.put(paciente); //Se mete al paciente en la cola
        semEsperaPaciente.release();
    }

    //Metodo donde se registran los pacientes y el Aux1 indica si pueden seguir o no
    public void registrarPacientes(Auxiliar auxiliar1) throws InterruptedException {

        //El auxiliar se pone en su puesto cuando llega por primera vez o viene de un descanso
        auxiliarRecepcion.setText(auxiliar1.toString());
        //Primero ponemos el contador del auxiliar 1 a 0
        auxiliar1.getContadorAux1().set(0);

        while (auxiliar1.getContadorAux1().get() < 10) {
            //Mientras el contador sea menor que 10, se sigue registrando pacientes

            if (colaEspera.isEmpty()) {
                //Si la cola de espera del registro está vacía, se espera
                semEsperaPaciente.acquire();

            } else {
                Paciente paciente = (Paciente) colaEspera.take(); //Con esto lo saca de la cola y lo borra
                pacienteRecepcion.setText(paciente.toString());
                colaRecepcion.setText(colaEspera.toString()); //Cuando se coge a un paciente se actualiza la cola de espera 
                auxiliar1.currentThread().sleep((int) (500 * Math.random() + 500)); //Tarda entre 0,5 y 1s en registrarse

                //Aquí he pensado en generar un número aleatorio entre 100 y el que coincida con el ID del paciente va fuera y cada 100 pacientes se actualiza
                if (paciente.getNumero() != numeroGanador) {

                    while (salaVacunacion.getColaVacunar().size() >= 10 && salaObservacion.getCapacidadObservacion().size() >= 20) {
                        //Si están llenas las salas se espera
                        semSalasOcupadas.acquire();
                    }

                    //Elige el puesto de forma aleatoria y el sanitario que le va a tocar vacunarse
                    boolean puestoObtenido = false;
                    String sanitario = "";
                    while (!puestoObtenido) {
                        int elegirPuestoAleatorio = (int) (Math.random() * 10); //Tiene que ser de 0 a 9 ya que son los puestos que hay en el array
                        if (salaVacunacion.getPuestos().get(elegirPuestoAleatorio).isDisponiblePaciente()) {
                            salaVacunacion.getPuestos().get(elegirPuestoAleatorio).setDisponiblePaciente(false);
                            paciente.setPuesto(elegirPuestoAleatorio); //El puesto será el número del Puesto que coge
                            sanitario = salaVacunacion.getPuestos().get(elegirPuestoAleatorio).getJtfPuesto().getText(); //Obtenemos el sanitario que le va a vacunar
                            puestoObtenido = true;
                        }
                    }

                    paciente.getRegistrado().set(true); //Se confirma que se puede vacunar
                    auxiliar1.getSemRegistrar().release(); //Una vez que ha terminado el registro, el Auxiliar le da permiso para que avance a la siguiente sala
                    pacienteRecepcion.setText(""); //una vez que sabe a que sala va y a que medico le toca ya se limpia el Jtextfield para el siguiente
                    System.out.println("Paciente " + paciente.toString() + " vacunado en el puesto " + (paciente.getPuesto() + 1) + " por " + sanitario); //El mas 1 es porque empieza en 0 el array de los puestos

                } else {
                    paciente.getRegistrado().set(false);
                    auxiliar1.getSemRegistrar().release(); //Una vez que ha terminado el registro se puede ir a la siguiente sala
                    pacienteRecepcion.setText("");
                    System.out.println("Paciente " + paciente.toString() + " ha acudido sin cita");
                }

                elegirPacienteParaEchar();
                auxiliar1.getContadorAux1().incrementAndGet();
            }

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

    public JTextField getPacienteRecepcion() {
        return pacienteRecepcion;
    }

    public void setPacienteRecepcion(JTextField pacienteRecepcion) {
        this.pacienteRecepcion = pacienteRecepcion;
    }

    public JTextField getAuxiliarRecepcion() {
        return auxiliarRecepcion;
    }

    public void setAuxiliarRecepcion(JTextField auxiliarRecepcion) {
        this.auxiliarRecepcion = auxiliarRecepcion;
    }

    public JTextArea getColaRecepcion() {
        return colaRecepcion;
    }

    public void setColaRecepcion(JTextArea colaRecepcion) {
        this.colaRecepcion = colaRecepcion;
    }

    public SalaVacunacion getSalaVacunacion() {
        return salaVacunacion;
    }

    public void setSalaVacunacion(SalaVacunacion salaVacunacion) {
        this.salaVacunacion = salaVacunacion;
    }

    public AtomicInteger getContadorRegistrar() {
        return contadorRegistrar;
    }

    public void setContadorRegistrar(AtomicInteger contadorRegistrar) {
        this.contadorRegistrar = contadorRegistrar;
    }

    public int getNumeroGanador() {
        return numeroGanador;
    }

    public void setNumeroGanador(int numeroGanador) {
        this.numeroGanador = numeroGanador;
    }

    public int getSumador() {
        return sumador;
    }

    public void setSumador(int sumador) {
        this.sumador = sumador;
    }

    public Semaphore getSemEsperaPaciente() {
        return semEsperaPaciente;
    }

    public void setSemEsperaPaciente(Semaphore semEsperaPaciente) {
        this.semEsperaPaciente = semEsperaPaciente;
    }

    public SalaObservacion getSalaObservacion() {
        return salaObservacion;
    }

    public void setSalaObservacion(SalaObservacion salaObservacion) {
        this.salaObservacion = salaObservacion;
    }

    public Semaphore getSemSalasOcupadas() {
        return semSalasOcupadas;
    }

    public void setSemSalasOcupadas(Semaphore semSalasOcupadas) {
        this.semSalasOcupadas = semSalasOcupadas;
    }

    public BlockingQueue getColaEspera() {
        return colaEspera;
    }

    public void setColaEspera(BlockingQueue colaEspera) {
        this.colaEspera = colaEspera;
    }

}
