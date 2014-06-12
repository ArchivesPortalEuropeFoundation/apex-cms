package eu.archivesportaleurope.portal.common.urls;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.archivesportaleurope.portal.contact.FeedbackController;
import eu.archivesportaleurope.util.ApeUtil;

public class AbstractUrl {
    private static final Logger LOGGER = Logger.getLogger(AbstractUrl.class);
	public static final String PARAMETER_AICODE = "/aicode/";
	public static final String PARAMETER_PAGE = "/page/";	
	private String repoCode;
	private String pageNumber;
	public AbstractUrl(String repoCode) {
		this.repoCode = repoCode;
	}

	protected String getRepoCode() {
		return ApeUtil.encodeRepositoryCode(repoCode);
	}
	

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public void setPageNumberAsInt(Integer pageNumber) {
		if (pageNumber != null){
			this.pageNumber = pageNumber.toString();
		}
	}
	

	@Override
	public String toString() {
		return PARAMETER_AICODE + getRepoCode();
	}
	public String getPageNumberSuffix(){
		if (StringUtils.isNotBlank(pageNumber)){
			return PARAMETER_PAGE+ pageNumber;
		}else {
			return "";
		}
	}

	public static String encodeUrl(String url){
		if (StringUtils.isNotBlank(url)){
			try {
				return URLEncoder.encode(url, "UTF-8").replaceAll("%2F", "/").replaceAll("%3A", ":");
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(ApeUtil.generateThrowableLog(e));
			}
		}
		return null;
	}
}
