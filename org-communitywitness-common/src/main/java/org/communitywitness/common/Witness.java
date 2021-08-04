package org.communitywitness.common;

import java.util.List;

public class Witness {
    private int id;
    private String name;
    private double rating;
    private String location;
    private List<Integer> reports;

    /**
     * 0-parameter constructor so that frameworks that work with beans 
     * can work with this type of object (like Jersey and GSON).
     */
    public Witness() {}
    
    /**
     * A constructor that sets all member variables at once for convenience.
     * @param id see {@link #setId(int)}
     * @param name see {@link #setName(String)}
     * @param rating see {@link #setRating(double)}
     * @param location see {@link #setLocation(String)}
     * @param reports see {@link #setReports(List)}
     */
    public Witness(int id, String name, double rating, String location, List<Integer> reports) {
    	setId(id);
    	setName(name);
    	setRating(rating);
    	setLocation(location);
    	setReports(reports);
    }

    /**
     * Returns the database's id number of this witness.
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of this witness.
     * @param id the id number associated with this witness
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the witnesses anonymous pseudonym.
     * @return this.name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets this witnesses name.
     * @param name an anonymous pseudonym to refer to this witness by
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the overall rating this witness has received from investigators.
     * @return this.rating
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the rating for this witness.
     * @param rating the overall/average rating given to this witness by investigators
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Returns the location that this witness lives in.
     * @return this.location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location for this witness.
     * @param location the location this witness lives in
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns a list of the id numbers of the reports this witness has filed.
     * @return this.reports
     */
	public List<Integer> getReports() {
		return reports;
	}

	/**
	 * Sets the reports this witness has filed.
	 * @param reports a list of the id numbers of the reports this witness has filed
	 */
	public void setReports(List<Integer> reports) {
		this.reports = reports;
	}


}
