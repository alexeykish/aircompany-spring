package by.pvt.kish.aircompany.pojos;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import java.io.Serializable;

/**
 * This class represents the Airport model.
 * The airport is used as a flights place of departure and place of arrival.
 * This model class can be used throughout all
 * layers, the data layer, the controller layer and the view layer.
 *
 * @author Kish Alexey
 */
@Entity
public class Airport implements Serializable {

    @Id
    @GeneratedValue
    @Column
    public Long getAid() {
        return aid;
    }
    public void setAid(Long aid) {
        this.aid = aid;
    }
    private Long aid;

    @Column(nullable = false)
    @NotEmpty()
    public String getName() {
        return name;
    }
    public void setName(String city) {
        this.name = city;
    }
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "F_CONTRY")),
            @AttributeOverride(name = "city", column = @Column(name = "F_CITY"))
    })
    @Valid
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    private Address address;

    public Airport() {
    }

    public Airport(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Airport airport = (Airport) o;

        if (aid != null ? !aid.equals(airport.aid) : airport.aid != null) return false;
        if (name != null ? !name.equals(airport.name) : airport.name != null) return false;
        return address != null ? address.equals(airport.address) : airport.address == null;

    }

    @Override
    public int hashCode() {
        int result = aid != null ? aid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "aid=" + aid +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }


}
