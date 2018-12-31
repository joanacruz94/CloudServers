/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.client;
/**
 *
 * @author joanacruz
 */
public class Option {
    //variáveis de instância
    private String text;
    private String number;
    
    //Construtor parametrizado
    public Option(String text, String number){
        this.text = text;
        this.number = number;
    }
    
    // Método toString
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(this.number).append(" - ").append(this.text);
        
        return sb.toString();
    }
}
