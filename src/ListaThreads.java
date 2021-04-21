
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aleja
 */
public class ListaThreads {                                                     //Array de hilos para las colas
    private ArrayList<Paciente>Lista;
    private JTextField jtf=null;
    private JTextArea jta=null;

    public ListaThreads(JTextField jtf) {
        this.jtf = jtf;
        Lista=new ArrayList<Paciente>();
    }
    public ListaThreads(JTextArea jta) {
        this.jta = jta;
        Lista=new ArrayList<Paciente>();
    }
    
    public ArrayList<Paciente> getLista(){
        return this.Lista;
    }

    public synchronized void introducir(Paciente hilo){                           //Método para introducir hilos en la lista e imprimir para actualizar el cambio                      
        Lista.add(hilo);
        imprimir();
    }
    public synchronized void expulsar(Paciente hilo){                             //Método para expulsar hilos de la lista e imprimir para actualizar el cambio
        Lista.remove(hilo);
        imprimir();
    }
    public void imprimir(){                                                     //Método para guardar la cola en un JTextField que mostrar en pantalla
        String cola="";
        for(int i=0;i<Lista.size();i++){
            cola=cola+Lista.get(i).toString()+", ";
        }
        if (jta==null) {
        jtf.setText(cola);
        }else{
            jta.setText(cola);
        }
    }

}
