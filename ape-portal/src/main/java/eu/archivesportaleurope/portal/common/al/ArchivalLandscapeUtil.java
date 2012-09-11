package eu.archivesportaleurope.portal.common.al;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.persistence.vo.Country;

public class ArchivalLandscapeUtil {
	private ResourceBundleSource resourceBundleSource;

	public ArchivalLandscapeUtil(ResourceBundleSource resourceBundleSource) {
		this.resourceBundleSource = resourceBundleSource;
	}

	protected String getLanguage() {
		return resourceBundleSource.getLocale().getLanguage().substring(0, 2);
	}

	public Locale getLocale() {
		return resourceBundleSource.getLocale();
	}

	public List<CountryUnit> localizeCountries(List<Country> countries) {
		List<CountryUnit> countriesUnits = new ArrayList<CountryUnit>();
		for (Country co : countries) {
			String otherCountryName = resourceBundleSource.getString("country." + co.getCname().toLowerCase(),
					co.getCname());
			countriesUnits.add(new CountryUnit(co, otherCountryName));
		}

		Collections.sort(countriesUnits);
		return countriesUnits;
	}

}
