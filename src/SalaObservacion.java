
iimport java.util.ArrayList;
import javax.swing.JTextField;

/**
 *
 * @author aleja
 */
public class SalaObservacion {

    private int max, puestoLibre;
    private ArrayList<Puesto> puestos= new ArrayList<Puesto>();

    public SalaObservacion(int max) {
        this.max = max;
        for (int i = 0; i < max; i++) {
            Puesto nuevoPuesto=  new Puesto(new JTextField(), true);
            puestos.add(nuevoPuesto);
        }
    }
    
    public ArrayList<Puesto> getPuestos(){
        return puestos;
    }
    
    public boolean puestoLibre(){
        boolean resultado=false;
        for (int i = 0; i < puestos.size(); i++) {
            resultado=resultado | puestos.get(i).isDisponible();//falso o dispoible ->true, falso o no disponible ->false
            if (resultado) 
                puestoLibre=i;
        }
        return resultado;
    }
    
    public void entraPaciente(Paciente paciente){
        //procedimiento para que el paciente entre en la sala de observacion
        if (puestoLibre()) {
            puestos.get(puestoLibre).entraPaciente(paciente);
        }
    }
    
    public void salePaciente(int puesto){
        //procedimiento para que el paciente se vaya a casa
        System.out.println("Paciente: " +   puestos.get(puesto).toString() + " sale del hospital");
        puestos.get(puesto).salePaciente();
    }
}
