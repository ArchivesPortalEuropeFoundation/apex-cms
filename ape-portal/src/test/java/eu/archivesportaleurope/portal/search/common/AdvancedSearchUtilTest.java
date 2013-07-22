package eu.archivesportaleurope.portal.search.common;

import junit.framework.Assert;

import org.junit.Test;

public class AdvancedSearchUtilTest {


	@Test
	public void testIsValidDate() {
		// start date
		Assert.assertEquals("1988-12-30", AdvancedSearchUtil.parseDate("1988/12/30", true));
		Assert.assertEquals("1988-12-01", AdvancedSearchUtil.parseDate("1988/12", true));
		Assert.assertEquals("1988-01-01", AdvancedSearchUtil.parseDate("1988", true));
		// enddate
		Assert.assertEquals("1988-10-30", AdvancedSearchUtil.parseDate("1988/10/30", false));
		Assert.assertEquals("1988-10-31", AdvancedSearchUtil.parseDate("1988/10", false));
		Assert.assertEquals("1988-12-31", AdvancedSearchUtil.parseDate("1988", false));
	
		Assert.assertEquals(null, AdvancedSearchUtil.parseDate("1988/30/30", true));
		Assert.assertEquals(null, AdvancedSearchUtil.parseDate("11-12-1988", true));
		
	}


}
