/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservers.client;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;



public class Menu {
    private List<Option> linhas;
    private String nome;
    
    //Construtor por omissão
    public Menu(){
        this.linhas = new ArrayList<>();
        this.nome = "";
    }
    
    //Construtor parametrizado
    public Menu(List<Option> linhas, String nome){
        setLinhas(linhas);
        this.nome = nome;
    }
    
    //Setter
    public void setLinhas(List<Option> linhas){
        this.linhas = new ArrayList<>();
        linhas.forEach(o -> {this.linhas.add(o);});
    }
    
    
    public void show(){
        //StringBuffer sb = new StringBuffer();
        System.out.println("--------------------------------------------------------------------");
        System.out.println("                          " + this.nome);
        System.out.println("--------------------------------------------------------------------");
        for(Option o: this.linhas){
            System.out.println(o.toString());
        }
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Select one option ");
    }
}
