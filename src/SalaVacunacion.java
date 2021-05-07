
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaVacunacion {

    private int aforoVacunacion, puestoPaciente = 0; //Número máximo de pacientes en la sala de vacunación
    private ArrayList<Puesto> puestos = new ArrayList<Puesto>();
    private ArrayList<JTextField> puestosVacunacion;
    private JTextField auxiliarVacunacion, numeroVacunas;
    private AtomicInteger contadorVacunas;
    private BlockingQueue colaVacunar;

    public SalaVacunacion(int aforoVacunacion, JTextField auxiliarVacunacion, JTextField numeroVacunas, ArrayList<JTextField> puestosVacunacion) {
        this.aforoVacunacion = aforoVacunacion;
        this.puestosVacunacion = puestosVacunacion;
        for (int i = 0; i < puestosVacunacion.size(); i++) {
            Puesto nuevoPuesto = new Puesto(puestosVacunacion.get(i), true, true); //Creamos los puestos con los JTextField del main y con dos variables para controlar si están vacios o llenos
            puestos.add(nuevoPuesto);
        }
        this.auxiliarVacunacion = auxiliarVacunacion;
        this.numeroVacunas = numeroVacunas;
        this.colaVacunar = new LinkedBlockingQueue(aforoVacunacion);
        contadorVacunas = new AtomicInteger(0); //Inicializamos el contador de las vacunas
    }

    //Método para el auxiliar 2, genera 20 dosis con el periodo indicado. Tiene un contador que llega hasta 20
    public void introducirDosis(Auxiliar auxiliar2) throws InterruptedException {
        //Primero ponemos el contador del auxiliar 2 a 0
        auxiliar2.getContadorAux2().set(0);
        auxiliarVacunacion.setText(auxiliar2.toString());
        while (auxiliar2.getContadorAux2().incrementAndGet() <= 20) { //Comprobamos el contador y lo incrementamos
            numeroVacunas.setText(contadorVacunas.incrementAndGet() + ""); //Incrementamos en 1 el número de vacunas y lo sacamos en el JTextField correspondiente
            auxiliar2.currentThread().sleep((int) (500 * Math.random() + 500)); //Tiempo que tarda en preparar una dosis
        }
        auxiliarVacunacion.setText(""); //Actualizamos el JTextField cuando el auxiliar se va a descansar
    }

    //Los sanitarios se colocan en el puesto que tengan libre
    public void colocarSanitarios(Sanitario sanitario) {
        int i = 0;
        // Hacemos un bucle para que el sanitario elija puesto, una vez elegido el puesto se queda interrumpido para que salga del bucle while y solo ocupe un puesto
        boolean puestoSanitarioObtenido = false;
        while (!puestoSanitarioObtenido && i < puestos.size()) {
            if (puestos.get(i).isDisponible()) {
                puestos.get(i).entraSanitario();
                puestos.get(i).getJtfPuesto().setText(sanitario.toString());
                sanitario.setPuesto(i);
                puestoSanitarioObtenido = true;
            }
            i++;
        }
    }

    //procedimiento para meter al paciente en la salaVacunacion
    public void entraPaciente(Paciente paciente) {
        try {
            //Semaforo porque solo puede haber 10 como mucho en la cola porque se estarán vacunando
            colaVacunar.put(paciente); //Se mete al paciente en la cola
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void vacunarPaciente(Sanitario sanitario) throws InterruptedException {
        //Se resetea el contador cada vez que un sanitario va a empezar a vacunar
        sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).set(0); //Solucinado el problema de los contadores de los sanitarios porque cada sanitario tiene que tener un contador individual

        //Mientras haya vacunas y el contador de los sanitarios no llegue a 15
        while (sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).get() < 15) {

            Paciente paciente = (Paciente) colaVacunar.take();  //Lo sacamos de la lista y hace el sleep junto con el sanitario porque es cuando le vacuna
            puestoPaciente = paciente.getPuesto();

            //De esta manera solo atiende el sanitario que está en el puesto que el paciente tiene
            if (sanitario.getPuesto() == puestoPaciente) { //No hace falta comprobar que la cola este vacía porque LinkedBlockingQueue lo hace por nosotros
                //Aquí hay que meter al paciente en un puesto
                String pacientePuesto = "";
                pacientePuesto = puestos.get(paciente.getPuesto()).getJtfPuesto().getText();
                pacientePuesto = pacientePuesto + "," + paciente.toString();
                puestos.get(paciente.getPuesto()).getJtfPuesto().setText(pacientePuesto);

                contadorVacunas.decrementAndGet(); //El sanitario utiliza una vacuna

                sleep((int) (2000 * Math.random() + 3000)); //El sanitario tarda entre 3 y 5 segundos en vacunar

                puestos.get(paciente.getPuesto()).getJtfPuesto().setText("");
                puestos.get(paciente.getPuesto()).getJtfPuesto().setText(sanitario.toString()); //Cuando va a salir de la sala de vacunación deja al sanitario solo para que atienda al siguiente paciente

                paciente.getPacienteVacunado().release(); //Se supone que cuando termina el sleep se le avisa al paciente para que entre en la sala de observación

                paciente.getRecepcion().getSemSalasOcupadas().release(); //Se avisa de que hay hueco en la sala de vacunación

                sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).incrementAndGet(); //Cuando vacuna a un paciente se le suma uno al contador individual del sanitario
            } else {
                colaVacunar.add(paciente);
            }
        }
        puestos.get(sanitario.getPuesto()).getJtfPuesto().setText(""); //Cuando se va a descansar se pone el JTextField limpio
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
