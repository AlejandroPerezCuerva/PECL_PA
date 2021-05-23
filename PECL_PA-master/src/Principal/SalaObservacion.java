package Principal;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JTextField;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class SalaObservacion {

    private int aforoObservacion, puestoLibre; //Aforo máximo de la sala de observación y número del puesto que está libre
    private ArrayList<Puesto> puestos; //ArrayList de puestos donde están los JTextField y dos booleanos para indicar si hay sanitario y paciente
    private ArrayList<JTextField> puestosObservacion; //ArrayList de los JTextField de los puestos de la sala de observación
    private JTextField salidaTextField; //JTextField de la salida del hospital
    private BlockingQueue capacidadObservacion; //Cola para saber cuantos pacientes hay en la sala de observación
    private BlockingQueue pacientesObservacion; //Cola para saber los pacientes que tienen reacción a la vacuna
    private BufferedWriter fichero; //Fichero donde se guardan los datos
    private Date objDate; //Fecha para indicar cuando se realiza cada movimiento
    private DateFormat diaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //Formato de la fecha con la que aparece

    /**
     * Constructor de la Sala de observación, aquí se inicializan todos los
     * atributos necesarios para el uso de la sala
     *
     * @param aforoObservacion Número del aforo de la sala
     * @param puestosObservacion Todos los JTextField de la interfaz main que
     * son los puestos
     * @param salidaTextField JTextField de la salida
     * @param pacientesObservacion Cola donde están los pacientes que tienen que
     * ir a la sala de observación
     * @param fichero Fichero donde se escriben los moviemientos
     */
    public SalaObservacion(int aforoObservacion, ArrayList<JTextField> puestosObservacion, JTextField salidaTextField, BlockingQueue pacientesObservacion, BufferedWriter fichero) {
        this.aforoObservacion = aforoObservacion;
        this.puestosObservacion = puestosObservacion;
        this.puestos = new ArrayList<Puesto>();
        //Añadimos los puestos de observación a un array de la clase puestos para tener un mayor control sobre ellos
        for (int i = 0; i < aforoObservacion; i++) {
            Puesto nuevoPuesto = new Puesto(puestosObservacion.get(i), true, true);
            puestos.add(nuevoPuesto);
        }
        this.capacidadObservacion = new LinkedBlockingQueue(aforoObservacion);
        this.salidaTextField = salidaTextField;
        this.pacientesObservacion = pacientesObservacion;
        this.fichero = fichero;
    }

    /**
     * Método por donde entra el paciente que tiene que ir a la sala de
     * observación. Los pacientes miran donde hay puesto libre y se meten
     * indicandolo para que ningún otro paciente se meta en el mismo puesto que
     * el y sale su identificador en el JTextField del puesto
     *
     * @param paciente Paciente que llega a la sala de observación
     * @throws InterruptedException Excepciones de interrupción
     */
    public void entraPaciente(Paciente paciente) throws InterruptedException {
        paciente.getSalaVacunacion().getPuestos().get(paciente.getPuesto()).setDisponiblePaciente(true); //Hasta que el paciente no entra en la sala de observación no deja libre su puesto en la sala de vacunación
        capacidadObservacion.put(paciente);//Entra el paciente a la cola que es bloqueante segun el aforo que haya
        int i = 0;
        boolean puestoObtenido = false;
        while (!puestoObtenido && i < puestos.size()) {
            if (puestos.get(i).isDisponiblePaciente()) {
                puestos.get(i).setDisponiblePaciente(false);
                paciente.setPuesto(i);
                puestoObtenido = true;
                puestos.get(i).getJtfPuesto().setText(paciente.toString());
            }
            i++;
        }
    }

    /**
     * Método que hace el paciente que ya ha elegido su puesto esté durante 10
     * segundos para ver si le da reacción la vacuna. A un 5% de los pacientes
     * le da reacción y se tienen que meter en la cola de pacientes con reacción
     * para que luego un sanitario les pueda curar.
     *
     * @param paciente Paciente que espera para ver si le da reacción la vacuna
     * @throws InterruptedException Excepción de interrupción
     * @throws IOException Excepción de entrada salida
     */
    public void pacienteEnObservacion(Paciente paciente) throws InterruptedException, IOException {
        boolean reaccion = (int) (100 * Math.random()) <= 5; //En el 5% de los casos el paciente sufre efectos adversos

        sleep(10000); //El paciente está 10 segundos es la observación 
        if (reaccion) {
            puestos.get(paciente.getPuesto()).getJtfPuesto().setForeground(Color.RED);//Se pone en rojo para resaltar que hay un problema
            capacidadObservacion.take(); //También hay que sacar al paciente de esta lista
            objDate = new Date();
            String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " muestra sintomas\n";
            System.out.print(mensaje);
            fichero.write(mensaje);
            fichero.flush();
            pacientesObservacion.put(paciente);
            paciente.getReaccionVacuna().set(true);//El paciente tiene una reaccion a la vacuna                
        } else {
            paciente.getSemObservar().release();
            puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
            capacidadObservacion.take();
            puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
        }
    }

    /**
     * Método en el cual el sanitario después de hacer su descanso se mete si ve
     * que hay algún paciente con reacción a la vacuna. Si es así se mete en el
     * método, saca al paciente enfermo de la cola, le atiende y luego le da el
     * alta para que se pueda ir
     *
     * @param sanitario Sanitario que cura al paciente con reacción
     * @throws InterruptedException Excepción de interrupción
     * @throws IOException Excepción de entrada salida
     */
    public void atenderPaciente(Sanitario sanitario) throws InterruptedException, IOException {
        //Cuando llega el sanitario, saca al paciente que va a atender de la cola        
        Paciente paciente = (Paciente) pacientesObservacion.take();
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tPaciente " + paciente.toString() + " sufre una reacción y es atendido por " + sanitario.toString() + "\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();

        //Cogemos lo que hay en el JTextField y le añadimos el sanitario para que le cure
        String puestoCurar = "";
        puestoCurar = puestos.get(paciente.getPuesto()).getJtfPuesto().getText() + "," + sanitario.toString();
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText(puestoCurar);

        //El sanitario y el pacienten hacen el sleep para ver la reacción del paciente a la vacuna
        puestos.get(paciente.getPuesto()).getJtfPuesto().setForeground(Color.BLACK);//Se vuelve a poner el color normal para indicar que ya está siendo atendido
        sleep((int) (3000 * Math.random() + 2000));//Tarda entre 2 y 5 segundos
        paciente.getReaccionVacuna().set(false);
        paciente.getSemObservar().release();
        objDate = new Date();
        mensaje = diaHora.format(objDate) + "\t\tAl paciente " + paciente.toString() + " se le da el alta\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();

        puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Ponemos que está libre el puesto del paciente porque ya ha terminado
        puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
    }

    /**
     * Método el cual utiliza el paciente cuando ha terminado su ejecución y
     * quiere salir de hospital, lo único que hace este método es indicar el
     * paciente que ha salido y lo muestra en la interfaz
     *
     * @param paciente Paciente que abandona el hospital
     * @throws IOException Excepción de entrada salida
     */
    public void salirHospital(Paciente paciente) throws IOException {
        objDate = new Date();
        String mensaje = diaHora.format(objDate) + "\t\tEl paciente " + paciente.toString() + " se va del Hospital\n";
        System.out.print(mensaje);
        fichero.write(mensaje);
        fichero.flush();
        salidaTextField.setText(paciente.toString());
    }

    /**
     * Método para la segunda parte de la práctica (Concurrencia distribuida) en el cual se recoge toda la información de todos los puestos de la sala de observación y de la 
     * salida del hospital para poder enviarselo en un ArrayList al cliente
     * 
     * @return ArrayList con toda la información de la sala de observación
     */
    public ArrayList<String> crearMensajeObservacion() {//Se crea un array con el contenido de los 20 puestos para enviarlo al cliente
        ArrayList<String> mensaje = new ArrayList<String>();
        for (int i = 0; i < puestos.size(); i++) {
            mensaje.add(puestos.get(i).getJtfPuesto().getText());
        }
        return mensaje;
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

    public void setPacientesObservacion(BlockingQueue pacientesObservacion) {
        this.pacientesObservacion = pacientesObservacion;
    }

    public int getPuestoLibre() {
        return puestoLibre;
    }

    public void setPuestoLibre(int puestoLibre) {
        this.puestoLibre = puestoLibre;
    }

    public ArrayList<Puesto> getPuestos() {
        return puestos;
    }

    public void setPuestos(ArrayList<Puesto> puestos) {
        this.puestos = puestos;
    }

    public ArrayList<JTextField> getPuestosObservacion() {
        return puestosObservacion;
    }

    public void setPuestosObservacion(ArrayList<JTextField> puestosObservacion) {
        this.puestosObservacion = puestosObservacion;
    }

    public int getAforoObservacion() {
        return aforoObservacion;
    }

    public void setAforoObservacion(int aforoObservacion) {
        this.aforoObservacion = aforoObservacion;
    }

    public JTextField getSalidaTextField() {
        return salidaTextField;
    }

    public void setSalidaTextField(JTextField salidaTextField) {
        this.salidaTextField = salidaTextField;
    }

    public BlockingQueue getCapacidadObservacion() {
        return capacidadObservacion;
    }

    public void setCapacidadObservacion(BlockingQueue capacidadObservacion) {
        this.capacidadObservacion = capacidadObservacion;
    }

    public BlockingQueue getPacientesObservacion() {
        return pacientesObservacion;
    }

}
