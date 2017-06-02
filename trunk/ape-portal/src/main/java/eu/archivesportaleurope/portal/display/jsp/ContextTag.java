package eu.archivesportaleurope.portal.display.jsp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.EadContent;

public class ContextTag extends SimpleTagSupport{
	private final static Logger LOGGER = Logger.getLogger(ContextTag.class);
	private Object clevel;
	private Object eadContent;
	private String onlyArchives;
	private Object country;

	public void doTag() {
		try {
			List<String> hierarchy = new ArrayList<String>();
			EadContent eadContent = null;
			StringBuilder result = new StringBuilder();
			if (clevel != null){
				CLevel currentCLevel = (CLevel) clevel;
				CLevel parent = currentCLevel.getParent();
				
				while (parent != null ){
					hierarchy.add(this.escapeChars(parent.getUnittitle()));
					parent = parent.getParent();
				}
				eadContent = currentCLevel.getEadContent();
				hierarchy.add(this.escapeChars(eadContent.getUnittitle()));
			}else if (this.eadContent != null){
				eadContent = (EadContent) this.eadContent;
			}
			
			if (!"true".equals(onlyArchives)){
				hierarchy.add(this.escapeChars(eadContent.getTitleproper()));
			}
			if (eadContent != null){
				ArchivalInstitution ai  = eadContent.getEad().getArchivalInstitution();
				while (ai != null){
					hierarchy.add(this.escapeChars(ai.getAiname()));
					ai = ai.getParent();
				}
				hierarchy.add(this.escapeChars((String) country));
				int numberOfWhitespaces = 0;
				for (int i = hierarchy.size() -1; i >=0;i--){
					//result.append("<span class=\"contextHierarchyItem\">");
					for (int j = 0; j < numberOfWhitespaces;j++){
						result.append("&nbsp;&nbsp;&nbsp;");
					}
					result.append(hierarchy.get(i));
					//result.append("</span>");
					result.append("<br/>");
					numberOfWhitespaces++;
				}
			}
			this.getJspContext().getOut().print(result);
		}catch (Exception e){
			LOGGER.error(APEnetUtilities.generateThrowableLog(e));
		}
	}

	/**
	 * Method to escape the chars '<' and '>', in order to prevent JS execution.
	 *
	 * @param str The string to be escaped.
	 * @return The escaped string.
	 */
	private String escapeChars(String str) {
		if (StringUtils.isBlank(str)){
			return "---";
		}else {
			return str.replaceAll(">", "&#62;").replaceAll("<","&#60;");
		}
	}


	public Object getClevel() {
		return clevel;
	}



	public void setClevel(Object clevel) {
		this.clevel = clevel;
	}



	public Object getEadContent() {
		return eadContent;
	}



	public void setEadContent(Object eadContent) {
		this.eadContent = eadContent;
	}



	public String getOnlyArchives() {
		return onlyArchives;
	}



	public void setOnlyArchives(String onlyArchives) {
		this.onlyArchives = onlyArchives;
	}



	public Object getCountry() {
		return country;
	}



	public void setCountry(Object country) {
		this.country = country;
	}


}
