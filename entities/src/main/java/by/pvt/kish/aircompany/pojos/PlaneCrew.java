package by.pvt.kish.aircompany.pojos;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * This class represents the Plane crew model
 * @author Kish Alexey
 */
@Entity
public class PlaneCrew {

    @Id
    @GenericGenerator(
            name = "generator",
            strategy = "foreign",
            parameters = @Parameter(name = "property", value = "plane"))
    @GeneratedValue(generator = "generator")
    public Long getPid() {
        return pid;
    }
    public void setPid(Long pid) {
        this.pid = pid;
    }
    private Long pid;

    @Column(nullable = false)
    @NotNull()
    @Min(value = 1)
    public Integer getNumberOfPilots() {
        return numberOfPilots;
    }
    public void setNumberOfPilots(Integer numberOfPilots) {
        this.numberOfPilots = numberOfPilots;
    }
    private Integer numberOfPilots;

    @Column
    @NotNull()
    @Min(value = 0)
    public Integer getNumberOfNavigators() {
        return numberOfNavigators;
    }
    public void setNumberOfNavigators(Integer numberOfNavigators) {
        this.numberOfNavigators = numberOfNavigators;
    }
    private Integer numberOfNavigators;

    @Column
    @NotNull()
    @Min(value = 0)
    public Integer getNumberOfRadiooperators() {
        return numberOfRadiooperators;
    }
    public void setNumberOfRadiooperators(Integer numberOfRadiooperators) {
        this.numberOfRadiooperators = numberOfRadiooperators;
    }
    private Integer numberOfRadiooperators;

    @Column
    @NotNull()
    @Min(value = 0)
    public Integer getNumberOfStewardesses() {
        return numberOfStewardesses;
    }
    public void setNumberOfStewardesses(Integer numberOfStewardesses) {
        this.numberOfStewardesses = numberOfStewardesses;
    }
    private Integer numberOfStewardesses;

    @OneToOne
    @PrimaryKeyJoinColumn
    public Plane getPlane() {
        return plane;
    }
    public void setPlane(Plane plane) {
        this.plane = plane;
    }
    private Plane plane;

    public PlaneCrew() {
    }

    public PlaneCrew(int numberOfPilots, int numberOfNavigators, int numberOfRadiooperators, int numberOfStewardesses) {
        this.numberOfPilots = numberOfPilots;
        this.numberOfNavigators = numberOfNavigators;
        this.numberOfRadiooperators = numberOfRadiooperators;
        this.numberOfStewardesses = numberOfStewardesses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlaneCrew planeCrew = (PlaneCrew) o;

        if (pid != null ? !pid.equals(planeCrew.pid) : planeCrew.pid != null) return false;
        if (numberOfPilots != null ? !numberOfPilots.equals(planeCrew.numberOfPilots) : planeCrew.numberOfPilots != null)
            return false;
        if (numberOfNavigators != null ? !numberOfNavigators.equals(planeCrew.numberOfNavigators) : planeCrew.numberOfNavigators != null)
            return false;
        if (numberOfRadiooperators != null ? !numberOfRadiooperators.equals(planeCrew.numberOfRadiooperators) : planeCrew.numberOfRadiooperators != null)
            return false;
        return numberOfStewardesses != null ? numberOfStewardesses.equals(planeCrew.numberOfStewardesses) : planeCrew.numberOfStewardesses == null;

    }

    @Override
    public int hashCode() {
        int result = pid != null ? pid.hashCode() : 0;
        result = 31 * result + (numberOfPilots != null ? numberOfPilots.hashCode() : 0);
        result = 31 * result + (numberOfNavigators != null ? numberOfNavigators.hashCode() : 0);
        result = 31 * result + (numberOfRadiooperators != null ? numberOfRadiooperators.hashCode() : 0);
        result = 31 * result + (numberOfStewardesses != null ? numberOfStewardesses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlaneCrew{" +
                "pid=" + pid +
                ", numberOfPilots=" + numberOfPilots +
                ", numberOfNavigators=" + numberOfNavigators +
                ", numberOfRadiooperators=" + numberOfRadiooperators +
                ", numberOfStewardesses=" + numberOfStewardesses +
                '}';
    }
}
