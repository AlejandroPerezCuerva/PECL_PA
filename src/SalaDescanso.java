
import static java.lang.Thread.sleep;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author aleja
 */
public class SalaDescanso {

    private JTextArea colaSalaDescanso;
    private BlockingQueue colaSala = new LinkedBlockingDeque();
    private SalaVacunacion salaVacunacion;

    public SalaDescanso(JTextArea colaSalaDescanso) {
        this.colaSalaDescanso = colaSalaDescanso;
    }

    //Añadimos los sanitarios a la sala de Descanso
    public void cambiarseSanitario(Sanitario sanitario) {
        System.out.println("Sanitario " + sanitario + " empieza a cambiarse");
        try {
            //Primero se meten, hacen el sleep y luego salen a sus puestos
            colaSala.put(sanitario);
            colaSalaDescanso.setText(colaSala.toString());
            sleep((int) (2000 * Math.random() + 1000)); //Sleep de 1 a 3 segundos que es lo que tardan en cambiarse
            colaSala.take();
            colaSalaDescanso.setText(colaSala.toString()); //Se actualiza el JTextFiel con los Sanitarios que han ido saliendo
        } catch (Exception e) {
        }
        System.out.println("Sanitario " + sanitario + " termina de cambiarse");
    }

    public void descansoAuxiliares(Auxiliar auxiliar, int maximo, int minimo) {
        System.out.println("Auxiliar " + auxiliar + " comienza su descanso");
        try {
            colaSala.put(auxiliar); //Metemos el auxiliar en la cola
            colaSalaDescanso.setText(colaSala.toString());
            sleep((int) ((maximo - minimo) * Math.random() + minimo)); //Dormimos al auxiliar el tiempo que nos indican
            colaSala.take();
            colaSalaDescanso.setText(colaSala.toString()); //Actualizamos la cola 
        } catch (InterruptedException ex) {
            Logger.getLogger(SalaDescanso.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Auxiliar " + auxiliar + " termina su descanso");
    }
    
    public void descansoSanitarios(Sanitario sanitario, int maximo, int minimo){
        System.out.println("Sanitario " + sanitario + " empieza su descanso");
        try {
            //Primero se meten, hacen el sleep y luego salen a sus puestos
            colaSala.put(sanitario);
            colaSalaDescanso.setText(colaSala.toString());
            sleep((int) ((maximo-minimo) * Math.random() + minimo)); //Sleep de 5 a 8 segundos que es lo que tardan en descansar
            colaSala.take();
            colaSalaDescanso.setText(colaSala.toString()); //Se actualiza el JTextFiel con los Sanitarios que han ido saliendo
            if (false) {//Si hay que atender al paciente
                atenderPaciente(sanitario, 0); //Hay que pasar el numero del puesto real
            }
        } catch (Exception e) {
        }
        System.out.println("Sanitario " + sanitario + " termina su descanso");        
    }
    
    public void atenderPaciente(Sanitario sanitario, int puesto){
        if(salaVacunacion.getPuestos().get(puesto).getAtendido().equals(false)){//Si el puesto no esta siendo atendido por nadie
            salaVacunacion.getPuestos().get(puesto).getAtendido().set(true);//Se indica que el puesto esta siendo atendido a otros sanitarios
            try {
                wait((int)(3000*Math.random()+2000));//Tarda entre 2 y 5 segundos
            } catch (InterruptedException ex) {
                Logger.getLogger(SalaDescanso.class.getName()).log(Level.SEVERE, null, ex);
            }
            salaVacunacion.getPuestos().get(puesto).getPaciente().getReaccionVacuna().set(false);//El sanitario indica que no hay problema con el paciente
        }
    }

}
