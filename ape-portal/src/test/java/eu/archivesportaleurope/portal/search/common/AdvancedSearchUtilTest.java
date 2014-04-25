package eu.archivesportaleurope.portal.search.common;

import junit.framework.Assert;

import org.junit.Test;

public class AdvancedSearchUtilTest {


	@Test
	public void testIsValidDate() {
		// start date
		Assert.assertEquals("1988-12-30", SearchUtil.parseDate("1988/12/30", true));
		Assert.assertEquals("1988-12-01", SearchUtil.parseDate("1988/12", true));
		Assert.assertEquals("1988-01-01", SearchUtil.parseDate("1988", true));
		// enddate
		Assert.assertEquals("1988-10-30", SearchUtil.parseDate("1988/10/30", false));
		Assert.assertEquals("1988-10-31", SearchUtil.parseDate("1988/10", false));
		Assert.assertEquals("1988-12-31", SearchUtil.parseDate("1988", false));
	
		Assert.assertEquals(null, SearchUtil.parseDate("1988/30/30", true));
		Assert.assertEquals(null, SearchUtil.parseDate("11-12-1988", true));
		
	}


}
