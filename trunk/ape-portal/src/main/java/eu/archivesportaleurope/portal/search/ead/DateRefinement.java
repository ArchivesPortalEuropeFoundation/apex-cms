package eu.archivesportaleurope.portal.search.ead;

public class DateRefinement extends Refinement {

	public DateRefinement(String fieldName, String fieldValue, String longDescription) {
		super(fieldName, fieldValue, longDescription);
	}

	@Override
	public boolean isDate() {
		return true;
	}

}
