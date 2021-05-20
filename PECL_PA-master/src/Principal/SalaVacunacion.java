package Principal;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JTextField;

/**
 *
 * @author Alvaro Gonzalez Garcia y Alejandro Pérez Cuerva
 */
public class SalaVacunacion {

    private int aforoVacunacion, puestoPaciente = 0; //Número máximo de pacientes en la sala de vacunación
    private ArrayList<Puesto> puestos; //ArrayList de puestos de la sala de vacunación
    private ArrayList<JTextField> puestosVacunacion; //ArrayList de todos los JTextField de los puestos de vacuanción
    private JTextField auxiliarVacunacion, numeroVacunas; //JTextField para el auxiliar y el número de vacunas
    private AtomicInteger contadorVacunas; //Contador de las vacunas
    private BlockingQueue colaVacunar; //Cola donde se meten los pacientes para vacunarse
    private Semaphore sinVacunas; //Semáforo que avisa si no hay vacunas
    private AtomicInteger puestoCerrado; //Puesto que se está limpiando

    /**
     * Constructor de la clase sala de vacunación. También se inicializan todos
     * recursos de concurrencia aquí, para que quede más limpio
     *
     * @param aforoVacunacion Número máximo que tiene la sala como aforo
     * @param auxiliarVacunacion JTextField donde se indica al auxiliar 2 que es
     * quien hace las vacunas
     * @param numeroVacunas JTextField donde se indican las vacunas disponibles
     * @param puestosVacunacion Todos los JTextField donde están los sanitarios
     * vacunando
     */
    public SalaVacunacion(int aforoVacunacion, JTextField auxiliarVacunacion, JTextField numeroVacunas, ArrayList<JTextField> puestosVacunacion) {
        this.aforoVacunacion = aforoVacunacion;
        this.puestosVacunacion = puestosVacunacion;
        this.puestos = new ArrayList<Puesto>();
        for (int i = 0; i < puestosVacunacion.size(); i++) {
            Puesto nuevoPuesto = new Puesto(puestosVacunacion.get(i), true, true); //Creamos los puestos con los JTextField del main y con dos variables para controlar si están vacios o llenos
            puestos.add(nuevoPuesto);
        }
        this.auxiliarVacunacion = auxiliarVacunacion;
        this.numeroVacunas = numeroVacunas;
        this.colaVacunar = new LinkedBlockingQueue(aforoVacunacion);
        this.contadorVacunas = new AtomicInteger(0); //Inicializamos el contador de las vacunas
        this.sinVacunas = new Semaphore(0);
        this.puestoCerrado = new AtomicInteger(-1); //Se inicializa a -1 para que no afecte a ningun sanitario
    }

    /**
     * En este método la misión del auxiliar 2 es ir incrementando el número de
     * vacunas para que los sanitarios puedan utilizarlas, se utiliza
     * contadorVacunas que es un AtomicInteger ya que es un método de
     * concurrencia de comunicación y también un semáforo que indica que hay
     * vacunas, por si hay algún sanitario esperando por ellas
     *
     * @param auxiliar2 Auxiliar encargado de introducir las dosis
     * @throws InterruptedException Excepciones de interrupciones
     */
    public void introducirDosis(Auxiliar auxiliar2) throws InterruptedException {
        //Primero ponemos el contador del auxiliar 2 a 0
        auxiliar2.getContadorAux2().set(0);
        auxiliarVacunacion.setText(auxiliar2.toString());
        while (auxiliar2.getContadorAux2().incrementAndGet() <= 20) { //Comprobamos el contador y lo incrementamos
            numeroVacunas.setText(contadorVacunas.incrementAndGet() + ""); //Incrementamos en 1 el número de vacunas y lo sacamos en el JTextField correspondiente
            sinVacunas.release(); //Cada vez que se mete una vacuna se hace un release por si hay algún sanitario esperando, esto le avisa
            auxiliar2.currentThread().sleep((int) (500 * Math.random() + 500)); //Tiempo que tarda en preparar una dosis
        }
        auxiliarVacunacion.setText(""); //Actualizamos el JTextField cuando el auxiliar se va a descansar
    }

    /**
     * Método para que los sanitarios se coloquen en un puesto de vacunación,
     * para hacer esto, se elige un puesto que esté libre y cuando se lo asignan
     * lo pone a false para que esté ocupado y el siguiente sanitario no pueda
     * meterse en el mismo puesto
     *
     * @param sanitario Sanitario que busca hueco en los puestos
     */
    public void colocarSanitarios(Sanitario sanitario) {
        int i = 0;
        // Hacemos un bucle para que el sanitario elija puesto, una vez elegido el puesto se queda interrumpido para que salga del bucle while y solo ocupe un puesto
        boolean puestoSanitarioObtenido = false;
        while (!puestoSanitarioObtenido && i < puestos.size() && !puestos.get(i).isLimpiando()) {
            if (puestos.get(i).isDisponible()) {
                puestos.get(i).setDisponible(false);
                puestos.get(i).getJtfPuesto().setText(sanitario.toString());
                sanitario.setPuesto(i);
                puestos.get(i).setSanitario(sanitario);
                puestoSanitarioObtenido = true;
            }
            i++;
        }
        sanitario.getSanitarioDescansaDistribuida().set(false); //Se pone a false para que el sanitario después de vacunar pueda atender al paciente que esta enfermo
    }

    /**
     * Método que utilizan los pacientes para meterse en una cola que tiene el
     * límite del aforo de la sala de vacunación y que luego gracias a la cola,
     * los sanitarios pueden atender al paciente
     *
     * @param paciente Paciente que llega a la sala de vacunación
     * @throws InterruptedException Excepciones de interrupciones
     */
    public void entraPaciente(Paciente paciente) throws InterruptedException {
        //Se introduce al paciente en una cola con limitación del aforo de la sala de vacunación y es bloqueante para máximo como para vacío
        colaVacunar.put(paciente); //Se mete al paciente en la cola
    }

    /**
     * Este método es muy importante ya que tiene muchas funciones, el sanitario
     * primero tiene que ver si hay vacunas para poder vacunar, después saca al
     * paciente de la cola para comprobar si corresponde el número que tiene el
     * paciente que se lo ha asignado el Auxiliar 1 con el número del puesto
     * donde está el sanitario vacunando. Si no es así lo vuelve a meter a la
     * cola si es así, coge una vacuna, se la pone al paciente en el tiempo
     * especificado y hace un release del semáforo que tiene el paciente
     * mientras espera que le vacunen para poder seguir su ejecución. Después
     * mira si tienen que limpiar la salsa haciendo que el contador llegue a 15
     * y se tenga que salir a descansar mientras limpian la sala
     *
     * @param sanitario Sanitario que vacuna a los paciente
     * @throws InterruptedException Excepxiones de interrupciones
     */
    public void vacunarPaciente(Sanitario sanitario) throws InterruptedException {
        //Se resetea el contador cada vez que un sanitario va a empezar a vacunar
        sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).set(0); //Solucinado el problema de los contadores de los sanitarios porque cada sanitario tiene que tener un contador individual

        //Mientras haya vacunas y el contador de los sanitarios no llegue a 15
        while (sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).get() < 15) {

            //Si el contador de las vacunas es menor que 0, el sanitario se tiene que quedar esperando
            if (contadorVacunas.get() < 0) {
                sinVacunas.acquire();
            }

            Paciente paciente = (Paciente) colaVacunar.take();  //Lo sacamos de la lista y hace el sleep junto con el sanitario porque es cuando le vacuna
            puestoPaciente = paciente.getPuesto();

            //De esta manera solo atiende el sanitario que está en el puesto que el paciente tiene
            if (sanitario.getPuesto() == puestoPaciente) { //No hace falta comprobar que la cola este vacía porque LinkedBlockingQueue lo hace por nosotros
                //Aquí hay que meter al paciente en un puesto
                String pacientePuesto = "";
                pacientePuesto = puestos.get(paciente.getPuesto()).getJtfPuesto().getText();
                pacientePuesto = pacientePuesto + "," + paciente.toString();
                puestos.get(paciente.getPuesto()).getJtfPuesto().setText(pacientePuesto);

                numeroVacunas.setText(contadorVacunas.decrementAndGet() + ""); //El sanitario utiliza una vacuna y actualiza el JTextField
                sinVacunas.acquire(); //Se resta un permiso a las vacunas porque se ha sacado una

                sleep((int) (2000 * Math.random() + 3000)); //El sanitario tarda entre 3 y 5 segundos en vacunar

                puestos.get(paciente.getPuesto()).getJtfPuesto().setText(sanitario.toString()); //Cuando va a salir de la sala de vacunación deja al sanitario solo para que atienda al siguiente paciente

                paciente.getPacienteVacunado().release(); //Se supone que cuando termina el sleep se le avisa al paciente para que entre en la sala de observación

                sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).incrementAndGet(); //Cuando vacuna a un paciente se le suma uno al contador individual del sanitario
            } else {
                colaVacunar.add(paciente);
            }

            //Si el sanitario está en el puesto que el cliente quiere cerrar, se tiene que ir a descansar
            if (puestos.get(sanitario.getPuesto()).isLimpiando()) {
                sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).set(15);
                sanitario.getSanitarioDescansaDistribuida().set(true);
                puestos.get(sanitario.getPuesto()).setLimpiando(false);//Se abre de nuevo el puesto

            }
        }
        puestos.get(sanitario.getPuesto()).getJtfPuesto().setText(""); //Cuando se va a descansar se pone el JTextField limpio
        puestos.get(sanitario.getPuesto()).setDisponible(true); //Cuando terminan de vacunar avisan de que su puesto está disponible

        //Si el sanitario abandona porque se va a limpiar la sala se pone en el JTextField
        if (sanitario.getSanitarioDescansaDistribuida().get()) {
            puestos.get(sanitario.getPuesto()).getJtfPuesto().setText("Limpiando");
        }
    }

    /**
     * Método que se utiliza para la segunda parte de la práctica. Este método
     * mete en un ArrayList toda la información de la sala de vacunación para
     * luego poder enviarsela al clietne
     *
     * @return ArrayList de toda la información de la sala de vacunación
     */
    public ArrayList<String> crearMensajeVacunacion() {//Se crea un array con el contenido de la sala de vacunacion para enviarlo al cliente
        ArrayList mensaje = new ArrayList<String>();
        for (int i = 0; i < puestos.size(); i++) {
            mensaje.add(puestos.get(i).getJtfPuesto().getText());
        }
        mensaje.add(auxiliarVacunacion.getText());
        mensaje.add(numeroVacunas.getText() + "");
        return mensaje;
    }

    /**
     * Método de distribuida que se utiliza para saber que puesto se cierra para
     * poder limpiarlo
     *
     * @param puesto Número del puesto que se cierra
     */
    public void cerrarPuesto(int puesto) {
        puestos.get(puesto).setLimpiando(true);
        puestoCerrado.set(puesto);
    }

    public Semaphore getSinVacunas() {
        return sinVacunas;
    }

    public void setSinVacunas(Semaphore sinVacunas) {
        this.sinVacunas = sinVacunas;
    }

    public AtomicInteger getPuestoCerrado() {
        return puestoCerrado;
    }

    public void setPuestoCerrado(AtomicInteger puestoCerrado) {
        this.puestoCerrado = puestoCerrado;
    }

    public void setPuestos(ArrayList<Puesto> puestos) {
        this.puestos = puestos;
    }

    public ArrayList<Puesto> getPuestos() {
        return puestos;
    }

    public int getAforoVacunacion() {
        return aforoVacunacion;
    }

    public void setAforoVacunacion(int aforoVacunacion) {
        this.aforoVacunacion = aforoVacunacion;
    }

    public ArrayList<JTextField> getPuestosVacunacion() {
        return puestosVacunacion;
    }

    public void setPuestosVacunacion(ArrayList<JTextField> puestosVacunacion) {
        this.puestosVacunacion = puestosVacunacion;
    }

    public JTextField getAuxiliarVacunacion() {
        return auxiliarVacunacion;
    }

    public void setAuxiliarVacunacion(JTextField auxiliarVacunacion) {
        this.auxiliarVacunacion = auxiliarVacunacion;
    }

    public JTextField getNumeroVacunas() {
        return numeroVacunas;
    }

    public void setNumeroVacunas(JTextField numeroVacunas) {
        this.numeroVacunas = numeroVacunas;
    }

    public AtomicInteger getContadorVacunas() {
        return contadorVacunas;
    }

    public void setContadorVacunas(AtomicInteger contadorVacunas) {
        this.contadorVacunas = contadorVacunas;
    }

    public int getPuestoPaciente() {
        return puestoPaciente;
    }

    public void setPuestoPaciente(int puestoPaciente) {
        this.puestoPaciente = puestoPaciente;
    }

    public BlockingQueue getColaVacunar() {
        return colaVacunar;
    }

    public void setColaVacunar(BlockingQueue colaVacunar) {
        this.colaVacunar = colaVacunar;
    }
}
