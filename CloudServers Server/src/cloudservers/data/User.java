package cloudservers.data;

import java.util.Objects;

public class User {

    private String mail;
    private String password;
    private float currentDebt;

    public User(){
        this.mail = "";
        this.password = "";
        this.currentDebt = 0;
    }

    public User(String mail, String password){
        this.mail = mail;
        this.password = password;
        this.currentDebt = 0;
    }

    public boolean validPassword(String password){

        return this.password.equals(password);
    }
    
    public float getCurrentDebt(){
        return this.currentDebt;
    }
    
    public void addtoCurrentDebt(double debt){
        this.currentDebt += debt;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.mail);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final User other = (User) obj;
        if (!Objects.equals(this.mail, other.mail)) {
            return false;
        }
        return true;
    }
    
    
}
