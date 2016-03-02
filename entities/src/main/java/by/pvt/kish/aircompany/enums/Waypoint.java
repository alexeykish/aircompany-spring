package by.pvt.kish.aircompany.enums;

/**
 * @author Kish Alexey
 */
public enum Waypoint {
    DEPARTURE("DEPARTURE"),
    ARRIVAL("ARRIVAL");

    private String waypoint;

    private Waypoint(final String waypoint){
        this.waypoint = waypoint;
    }

    public String getWaypoint(){
        return this.waypoint;
    }

    @Override
    public String toString(){
        return this.waypoint;
    }

    public String getName(){
        return this.name();
    }
}
