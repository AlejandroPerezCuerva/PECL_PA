
import java.util.ArrayList;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaObservacion {

    private int max;
    private ArrayList<JTextField> puestos= new ArrayList<JTextField>();

    public SalaObservacion(int max) {
        this.max = max;
        for (int i = 0; i < max; i++) {
            puestos.add(new JTextField());
        }
    }
    
    public void entraPaciente(Paciente p){
        //procedimiento para que el paciente entre en la sala de observacion
    }
    
    public void salePaciente(Paciente p){
        //procedimiento para que el paciente se vaya a casa
    }
}