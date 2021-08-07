package org.communitywitness.common;

import java.util.List;

public class Investigator {
	private int id = SpecialIds.UNSET_ID;
	private String name;
	private String organization;
	private String organizationType;
	private double rating;
	private List<Integer> reports;

	/**
	 * 0-parameter constructor so that frameworks that work with beans 
	 * can work with this type of object (like Jersey and GSON).
	 */
	public Investigator() {}

	/**
	 * A constructor that sets all member variables at once for convenience.
	 * @param id see {@link #setId(int)}
	 * @param name see {@link #setName(String)}
	 * @param organization see {@link #setOrganization(String)}
	 * @param organizationType see {@link #setOrganizationType(String)}
	 * @param rating see {@link #setRating(double)}
	 * @param reports see {@link #setReports(List)}
	 */
	public Investigator(int id, String name, String organization, String organizationType, 
			double rating, List<Integer> reports) {
		setId(id);
		setName(name);
		setOrganization(organization);
		setOrganizationType(organizationType);
		setRating(rating);
		setReports(reports);
	}

	/**
	 * Returns the database's id number of this investigator.
	 * @return this.id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id of this investigator.
	 * @param id the id number associated with this investigator
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns this investigators name.
	 * @return this.name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this investigator.
	 * @param name this investigators real name or name they work with
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the name of the organization this investigator works for.
	 * @return this.organization
	 */
	public String getOrganization() {
		return organization;
	}

	/**
	 * Sets the organization of this investigator.
	 * @param organization the name of the organization this investigator works for
	 */
	public void setOrganization(String organization) {
		this.organization = organization;
	}

	/**
	 * Returns the type of organization this investigator works for.
	 * @return this.organizationType
	 */
	public String getOrganizationType() {
		return organizationType;
	}

	/**
	 * Sets the organization type of this investigator.
	 * @param organizationType the type of organization this investigator works for
	 */
	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	/**
	 * Returns the overall rating this investigator has received from witnesses.
	 * @return this.rating
	 */
	public double getRating() {
		return rating;
	}

	/**
	 * Sets the rating for this investigator.
	 * @param rating the overall/average rating this investigator has received from witnesses
	 */
	public void setRating(double rating) {
		this.rating = rating;
	}

	/**
	 * Returns a list of the ids of reports being investigated by this investigator.
	 * @return this.reports
	 */
	public List<Integer> getReports(){
		return reports;
	}

	/**
	 * Sets the reports for this investigator.
	 * @param reports a list of ids of reports being investigated by this investigator
	 */
	public void setReports(List<Integer> reports) {
		this.reports = reports;
	}
}
