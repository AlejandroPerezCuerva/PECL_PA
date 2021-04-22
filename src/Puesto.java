
import javax.swing.JTextField;

public class Puesto {
    private JTextField jtfPuesto;
    private boolean disponible;
    private Paciente paciente;
    
    public Puesto(JTextField jtfPuesto, boolean disponible){
        this.jtfPuesto=jtfPuesto;
        this.disponible=disponible;
    }

    public JTextField getJtfPuesto() {
        return jtfPuesto;
    }

    public boolean isDisponible() {
        return disponible;
    }
    public void entraPaciente(Paciente paciente){
        this.paciente=paciente;
        disponible=false;
    }
    public void salePaciente(){
        paciente=null;
        disponible=true;
    }
}
