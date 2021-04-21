
import javax.swing.JTextField;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aleja
 */
public class Puesto {
    private JTextField jtfPuesto;
    private boolean disponible;
    
    public Puesto(JTextField jtfPuesto, boolean disponible){
        this.jtfPuesto=jtfPuesto;
        this.disponible=disponible;
    }
}
