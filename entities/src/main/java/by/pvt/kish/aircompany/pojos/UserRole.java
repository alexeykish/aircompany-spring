package by.pvt.kish.aircompany.pojos;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Kish Alexey
 */
@Entity
public class UserRole {

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }
    public void setId(Long uid) {
        this.id = id;
    }
    private Long id;

    
}
