
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaVacunacion {

    private int aforoVacunacion; //Número máximo de pacientes en la sala de vacunación
    private ArrayList<Puesto> puestos = new ArrayList<Puesto>();
    private ArrayList<JTextField> puestosVacunacion;
    private JTextField auxiliarVacunacion, numeroVacunas;
    private AtomicInteger contadorVacunas;
    private SalaObservacion salaObservacion;    //Sala necesaria para que los pacientes pasen de vacunar a observar
    private BlockingQueue colaVacunar;

    private Semaphore capacidadVacunacion; //Semaforo con la capacidad máxima de la sala de vacunación

    private Semaphore semEsperaPaciente = new Semaphore(0); //Con este semáforo se espera hasta que haya pacientes en la cola

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

            semEsperaPaciente.release(); //Se avisa de que hay un paciente en la cola para que no tenga que esperar

            //Aquí hay que meter al paciente en un puesto
            String pacientePuesto = "";
            pacientePuesto = puestos.get(paciente.getPuesto()).getJtfPuesto().getText();
            pacientePuesto = pacientePuesto + "," + paciente.toString();
            puestos.get(paciente.getPuesto()).getJtfPuesto().setText(pacientePuesto);
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void vacunarPaciente(Sanitario sanitario) {
        //Se resetea el contador cada vez que un sanitario va a empezar a vacunar
        sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).set(0); //Solucinado el problema de los contadores de los sanitarios porque cada sanitario tiene que tener un contador individual

        //Mientras haya vacunas y el contador de los sanitarios no llegue a 15
        while (sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).get() < 15) {

            if (colaVacunar.isEmpty()) { //Se va a esperar hasta que entre un paciente y se pueda hacer release
                try {
                    semEsperaPaciente.acquire();   //En este if se quedan esperando los sanitarios hasta que hay un paciente en la cola
                    puestos.get(sanitario.getPuesto()).setDisponiblePaciente(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                contadorVacunas.decrementAndGet(); //El sanitario utiliza una vacuna

                Paciente paciente = (Paciente) colaVacunar.take();  //Lo sacamos de la lista y hace el sleep junto con el sanitario porque es cuando le vacuna

                sleep((int) (2000 * Math.random() + 3000)); //El sanitario tarda entre 3 y 5 segundos en vacunar

                puestos.get(sanitario.getPuesto()).getJtfPuesto().setText("");
                puestos.get(sanitario.getPuesto()).getJtfPuesto().setText(sanitario.toString()); //Cuando va a salir de la sala de vacunación deja al sanitario solo para que atienda al siguiente paciente

                System.out.println("Puesto paciente:  " + paciente.getPuesto() + paciente.toString());
                System.out.println("Puesto sanitario:  " + sanitario.getPuesto());

                //puestos.get(paciente.getPuesto()).setDisponiblePaciente(true); //Se queda libre el puesto del paciente para que pueda entrar otro
                paciente.getPacienteVacunado().release(); //Se supone que cuando termina el sleep se le avisa al paciente para que entre en la sala de observación

            } catch (Exception ex) {
                Logger.getLogger(SalaVacunacion.class.getName()).log(Level.SEVERE, null, ex);
            }
            sanitario.getContadoresSanitarios().get(sanitario.getNumeroSanitario()).incrementAndGet(); //Cuando vacuna a un paciente se le suma uno al contador individual del sanitario

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

    public SalaObservacion getSalaObservacion() {
        return salaObservacion;
    }

    public void setSalaObservacion(SalaObservacion salaObservacion) {
        this.salaObservacion = salaObservacion;
    }

    public Semaphore getCapacidadVacunacion() {
        return capacidadVacunacion;
    }

    public void setCapacidadVacunacion(Semaphore capacidadVacunacion) {
        this.capacidadVacunacion = capacidadVacunacion;
    }

}
