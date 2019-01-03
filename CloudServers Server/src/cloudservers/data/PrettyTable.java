package cloudservers.data;

import java.util.ArrayList;
import java.util.List;

public class PrettyTable {
    List<List<String>> table;
    boolean valid;
    public PrettyTable(List<List<String>> table){
        int nCols=table.get(0).size();
        this.valid=true;
        for(List<String> row : table){
            if(row.size()!=nCols)
                this.valid=false;
        }
        if(this.valid)
            this.table=table;            
    }
    
    public void print(){
        if(this.valid){
            List<Integer> maxLengths=new ArrayList<>();
            for(String column : table.get(0))
                maxLengths.add(0);
            maxLengths.forEach(x->{x=0;});
            int i,j;
            for(i=0;i<table.size();i++){
                for(j=0;j<table.get(i).size();j++)
                    if(table.get(i).get(j).length() > maxLengths.get(j))
                        maxLengths.set(j, table.get(i).get(j).length());
            }
            
            List<String> format= new ArrayList<>();
            for(Integer colSize : maxLengths )
                format.add("");
            
            for(i=0;i<maxLengths.size();i++){
                format.set(i, "%-"+maxLengths.get(i)+"s | ");
                if(i==0)
                    format.set(i,"| "+format.get(i));
            }
            
            
            i=0;
            for(List<String> row : table){
                int c=0;
                for(String column : row){
                    System.out.format(format.get(c), column);
                    c++;
                }
                System.out.println();
                if(i==0){
                    for(String column : row){
                        String sep="";
                        int k;
                        for(k=0;k<maxLengths.get(row.indexOf(column));k++)
                            sep+="-";
                        System.out.format(format.get(row.indexOf(column)), sep);
                    }
                    System.out.println();
                }
                i++;
            }
            System.out.println();
            
        }
        else{
            System.out.println("Invalid table: All rows must have the same number of columns!");
        }
        
        
    }
    
    public String toString(){
        StringBuilder res = new StringBuilder();
    
        if(this.valid){
            List<Integer> maxLengths=new ArrayList<>();
            for(String column : table.get(0))
                maxLengths.add(0);
            maxLengths.forEach(x->{x=0;});
            int i,j;
            for(i=0;i<table.size();i++){
                for(j=0;j<table.get(i).size();j++)
                    if(table.get(i).get(j).length() > maxLengths.get(j))
                        maxLengths.set(j, table.get(i).get(j).length());
            }
            
            List<String> format= new ArrayList<>();
            for(Integer colSize : maxLengths )
                format.add("");
            
            for(i=0;i<maxLengths.size();i++){
                format.set(i, "%-"+maxLengths.get(i)+"s | ");
                if(i==0)
                    format.set(i,"| "+format.get(i));
            }
            
            
            i=0;
            for(List<String> row : table){
                int c=0;
                for(String column : row){
                     res.append(String.format(format.get(c), column));
                    c++;
                }
                res.append("\n");
                if(i==0){
                    for(String column : row){
                        String sep="";
                        int k;
                        for(k=0;k<maxLengths.get(row.indexOf(column));k++)
                            sep+="-";
                        res.append(String.format(format.get(row.indexOf(column)), sep));
                    }
                    res.append("\n");
                }
                i++;
            }
            res.append("\n");
            
        }
        else{
            //TODO: throw exception
            System.out.println("Invalid table: All rows must have the same number of columns!");
        }
        
        return res.toString();
    }
}
