
import java.util.ArrayList;
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
public class ListaThreads {                                                     //Array de hilos para las colas
    private ArrayList<Thread>Lista;
    private JTextField jtf;

    public ListaThreads(JTextField jtf) {
        this.jtf = jtf;
        Lista=new ArrayList<Thread>();
    }
    
    public ArrayList<Thread> getLista(){
        return this.Lista;
    }

    public synchronized void introducir(Thread hilo){                           //Método para introducir hilos en la lista e imprimir para actualizar el cambio                      
        Lista.add(hilo);
        imprimir();
    }
    public synchronized void expulsar(Thread hilo){                             //Método para expulsar hilos de la lista e imprimir para actualizar el cambio
        Lista.remove(hilo);
        imprimir();
    }
    public void imprimir(){                                                     //Método para guardar la cola en un JTextField que mostrar en pantalla
        String cola="";
        for(int i=0;i<Lista.size();i++){
            cola=cola+Lista.get(i).getName()+", ";
        }
        jtf.setText(cola);
    }

}
