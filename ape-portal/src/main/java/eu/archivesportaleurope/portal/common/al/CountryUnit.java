package eu.archivesportaleurope.portal.common.al;

import eu.apenet.persistence.vo.Country;

/**
 * 
 * @author Eloy
 * Date: 8th, Nov
 *
 * This class represents a country in the Portal
 */

public class CountryUnit implements Comparable<CountryUnit>{

	//Attributes
	private Country country;		//Identifier in country table
	private String localizedName;	//Country name in the language selected
	
	public String getLocalizedName() {
		return localizedName;
	}
	public void setLocalizedName(String localizedName) {
		this.localizedName = localizedName;
	}
	//Constructor
	public CountryUnit(Country country, String localizedName) {
			this.country = country;	
			this.localizedName=localizedName;
	}



	public Country getCountry() {
		return country;
	}
	@Override
	public int compareTo(CountryUnit o) {
		return localizedName.compareTo(o.getLocalizedName());
	}


}
