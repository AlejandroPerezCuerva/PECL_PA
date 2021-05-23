package Principal;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class Recepcion {

    private JTextField pacienteRecepcion, auxiliarRecepcion; //Recepción tiene todos los JTextField necesarios, los recibe del main
    private JTextArea colaRecepcion; //JTextArea de la cola de recepción
    private BlockingQueue colaEspera; //Elegimos este tipo de cola porque es el más cómodo de utilizar
    private SalaVacunacion salaVacunacion; //Sala necesaria para que los pacientes pasen de recepcion a vacunacion
    private SalaObservacion salaObservacion; //Sala de observación para que el auxiliar mire si está llena la sala
    private Semaphore semEsperaPaciente; //Semaforo que hace que el paciente espere mientras no haya nadie en la cola de espera en recepción
    private Semaphore semSalasOcupadas; //Semaforo que hacer que el paciente espere si la sala de observación y vacunación están ocupadas
    private BufferedWriter fichero; //Fichero donde se guardad toda la información
    private Date objDate; //Fecha en la que ocurren los movimientos
    private DateFormat diaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //Formato de la fecha

    /**
     * Constructor de la recepción del hostpital. Se inicializan todos los
     * atributos necesarios para el correcto funcionamiento
     *
     * @param colaRecepcion JTextArea de la cola de recepción
     * @param pacienteRecepcion JTextField donde se indica el paciente que está
     * siendo registrado
     * @param auxiliarRecepcion JTextField donde se coloca el auxiliar 1 para
     * poder registrar
     * @param salaVacunacion Sala de vacunación para indicar que puesto y que
     * sanitario atiende al paciente
     * @param salaObservacion Sala de observación para saber si está llena y
     * parar a los pacientes hasta que haya hueco
     * @param fichero Fichero donde se guardan los movimientos
     */
    public Recepcion(JTextArea colaRecepcion, JTextField pacienteRecepcion, JTextField auxiliarRecepcion, SalaVacunacion salaVacunacion, SalaObservacion salaObservacion, BufferedWriter fichero) {
        this.colaRecepcion = colaRecepcion;
        this.pacienteRecepcion = pacienteRecepcion;
        this.auxiliarRecepcion = auxiliarRecepcion;
        this.salaVacunacion = salaVacunacion;
        this.salaObservacion = salaObservacion;
        this.colaEspera = new LinkedBlockingQueue();
        this.semEsperaPaciente = new Semaphore(0);
        this.semSalasOcupadas = new Semaphore(0);
        this.fichero = fichero;
    }

    /**
     * Método en el cual el paciente que llega se mete en la cola de la
     * recepción para que luego le registre el auxiliar
     *
     * @param paciente Paciente que llega a la sala de recepción
     * @throws InterruptedException Excepción de interrupción
     */
    public void meterColaEspera(Paciente paciente) throws InterruptedException {
        colaEspera.put(paciente); //Se mete al paciente en la cola
        colaRecepcion.setText(colaEspera.toString()); //Mostramos en la cola de espera los pacientes que tenemos
        semEsperaPaciente.release(); //El release se hace para avisar al auxiliar 1 de que hay alguien en la cola y por lo tanto puede registrar
    }

    /**
     * Método en el cual llega el auxiliar 1 y va registrando a los pacientes,
     * donde un 1% no está registrado, una vez que los registra comprueba que
     * haya hueco en la sala de observación y la de vacunación y si hay hueco
     * mira los puestos disponibles de la sala de vacunación para indicar al
     * paciente donde tiene que ir y que sanitario le va atender. Tarda entre
     * 0,5 y 1 segundos en realizar todo este proceso, una vez que el contadore
     * del auxiliar 1 llega a 10 se va a descansar
     *
     * @param auxiliar1 Auziliar1 que registra a los pacientes que están en
     * recepción
     * @throws InterruptedException Excepción de interrupción
     * @throws IOException Excepción de entrada salida
     */
    public void registrarPacientes(Auxiliar auxiliar1) throws InterruptedException, IOException {

        //El auxiliar se pone en su puesto cuando llega por primera vez o viene de un descanso
        auxiliarRecepcion.setText(auxiliar1.toString());
        //Primero ponemos el contador del auxiliar 1 a 0
        auxiliar1.getContadorAux1().set(0);

        while (auxiliar1.getContadorAux1().get() < 10) {
            //Mientras el contador sea menor que 10, se sigue registrando pacientes

            if (colaEspera.isEmpty()) {
                //Si la cola de espera del registro está vacía, se espera
                semEsperaPaciente.acquire();

            } else if (salaVacunacion.getColaVacunar().size() > 9 || (salaObservacion.getCapacidadObservacion().size() + salaObservacion.getPacientesObservacion().size()) > 19) {
                semSalasOcupadas.acquire();
            } else {
                Paciente paciente = (Paciente) colaEspera.take(); //Con esto lo saca de la cola y lo borra
                pacienteRecepcion.setText(paciente.toString());
                colaRecepcion.setText(colaEspera.toString()); //Cuando se coge a un paciente se actualiza la cola de espera 
                auxiliar1.currentThread().sleep((int) (500 * Math.random() + 500)); //Tarda entre 0,5 y 1s en registrarse
                Date objDate = new Date();//Fecha para el txt
                if ((int) (Math.random() * 100) <= 99) {//Un 1% de los pacientes no tinenen cita previa

                    //Elige el puesto de forma aleatoria y el sanitario que le va a tocar vacunarse
                    boolean puestoObtenido = false;
                    String sanitario = "";
                    int elegirPuestoAleatorio = 0; //Puesto al que irá el paciente
                    while (!puestoObtenido) {
                        elegirPuestoAleatorio = (int) (Math.random() * 10); //Tiene que ser de 0 a 9 ya que son los puestos que hay en el array
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

                    String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " se vacunara en el puesto " + (paciente.getPuesto() + 1) + " por " + sanitario + "\n";
                    System.out.print(mensaje); //El mas 1 es porque empieza en 0 el array de los puestos
                    fichero.write(mensaje);
                    fichero.flush();

                } else {
                    //Caso para el paciente que no está registrado y tiene que salir del hospital
                    paciente.getRegistrado().set(false);
                    auxiliar1.getSemRegistrar().release(); //Una vez que ha terminado el registro se puede ir a la siguiente sala
                    pacienteRecepcion.setText("");
                    String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " ha acudido sin cita\n";
                    System.out.print(mensaje);
                    fichero.write(mensaje);
                    fichero.flush();
                }
                auxiliar1.getContadorAux1().incrementAndGet();
            }
        }
        auxiliarRecepcion.setText(""); //Actualizamos el JTextField para que se aprecie cuando el A1 se va al descanso
    }

    /**
     * Método que se utiliza en la segunda parte de la práctica que es la de
     * concurrencia distribuida, la función que tiene este método es recopilar
     * toda la información que hay en la recepción para luego enviarsela al
     * cliente
     *
     * @return ArrayList con toda la información de la recepción
     */
    public ArrayList<String> crearMensajeRecepcion() {//Se crea un array con el contenido de la recepción para enviarlo al cliente
        ArrayList<String> mensaje = new ArrayList<String>();
        mensaje.add(colaRecepcion.getText());
        mensaje.add(pacienteRecepcion.getText());
        mensaje.add(auxiliarRecepcion.getText());
        return mensaje;
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

    public BufferedWriter getFichero() {
        return fichero;
    }

    public void setFichero(BufferedWriter fichero) {
        this.fichero = fichero;
    }

    public Date getObjDate() {
        return objDate;
    }

    public void setObjDate(Date objDate) {
        this.objDate = objDate;
    }

    public DateFormat getDiaHora() {
        return diaHora;
    }

    public void setDiaHora(DateFormat diaHora) {
        this.diaHora = diaHora;
    }

}
