package eu.archivesportaleurope.portal.ead;

import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.AbstractJSONWriter;

/**
 * JSON Writer for the directory tree
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "eadTreeJSONWriter")
@RequestMapping(value = "VIEW")
public class EadTreeJSONWriter extends AbstractJSONWriter {
	private static final String MORE_VALUE_BEFORE = "before";
	private static final String MORE_VALUE_AFTER = "after";
	public static final int MAX_NUMBER_OF_CLEVELS = 20;
	private static final int ZERO = 0;
	private static final String END_ITEM_WITH_RETURN = "}\n";
	private static final String END_ITEM_WITH_COMMA = "},";
	private CLevelDAO clevelDAO;
	private EadDAO eadDAO;
	private EadContentDAO eadContentDAO;
	
	public CLevelDAO getClevelDAO() {
		return clevelDAO;
	}

	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}

	public EadDAO getEadDAO() {
		return eadDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	public EadContentDAO getEadContentDAO() {
		return eadContentDAO;
	}

	public void setEadContentDAO(EadContentDAO eadContentDAO) {
		this.eadContentDAO = eadContentDAO;
	}

	@ResourceMapping(value = "eadTree")
	public void writeJSON(@ModelAttribute(value = "eadTreeParams") EadTreeParams eadParams, ResourceRequest request,
			ResourceResponse response) {
		try {
			Locale locale = request.getLocale();

			if (eadParams.getMax() == null) {
				eadParams.setMax(MAX_NUMBER_OF_CLEVELS);
			}
			if (eadParams.getMax() > MAX_NUMBER_OF_CLEVELS) {
				eadParams.setMax(MAX_NUMBER_OF_CLEVELS);
			}
			if (eadParams.getParentId() != null) {
				List<CLevel> clevels = clevelDAO.findChildCLevels(eadParams.getParentId(), eadParams.getOrderId(),
						eadParams.getMax());
				writeToResponseAndClose(generateCLevelJSON(clevels, eadParams, locale), response);
			} 
			else if (eadParams.getMore() != null) {
				/*
				 * used for more option
				 */
				List<CLevel> clevels = clevelDAO.findTopCLevels(eadParams.getEcId()
						,eadParams.getOrderId(),
						eadParams.getMax());
				writeToResponseAndClose(generateCLevelJSON(clevels, eadParams, locale), response);
			} 
			else if (StringUtils.isNotBlank(eadParams.getSolrId())) {
				Long solrId = Long.parseLong(eadParams.getSolrId().substring(1));
				CLevel clevel = clevelDAO.findById(solrId);
				writeToResponseAndClose(generateJSONWithSelectedItem(clevel, eadParams, locale),
						response);
			} 
			else if (eadParams.getEcId() != null) {
				EadContent eadContent = eadContentDAO.findById(eadParams.getEcId());
				List<CLevel> clevels = clevelDAO.findTopCLevels(eadContent.getEcId(), eadParams.getOrderId(),
						eadParams.getMax());
				StringBuilder topCLevelsBuffer = generateCLevelJSON(clevels, eadParams, locale);
				writeToResponseAndClose(generateRootJSON(eadContent, topCLevelsBuffer, false, eadParams, locale),
						response);

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private StringBuilder generateCLevelJSON(List<CLevel> clevels, EadTreeParams eadParams, Locale locale) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
		if (!MORE_VALUE_AFTER.equalsIgnoreCase(eadParams.getMore()) && clevels.size() > 0) {
			CLevel firstCLevel = clevels.get(0);
			addBefore(buffer, firstCLevel, eadParams, locale);
		}
		for (int i = 0; i < clevels.size(); i++) {
			CLevel clevel = clevels.get(i);
			buffer.append(START_ITEM);
			addTitle(buffer, clevel, locale);
			buffer.append(COMMA);
			addId(buffer, clevel.getClId());
			addChildren(buffer, clevel, eadParams, locale);
			if (i < clevels.size() - 1) {
				buffer.append(END_ITEM_WITH_COMMA);
			} else {
				buffer.append(END_ITEM);
			}
		}
		if (!MORE_VALUE_BEFORE.equalsIgnoreCase(eadParams.getMore()) && clevels.size() > 0
				&& clevels.size() == MAX_NUMBER_OF_CLEVELS) {
			CLevel lastCLevel = clevels.get(clevels.size() - 1);
			addAfter(buffer, lastCLevel, eadParams, locale);
		}
		buffer.append(END_ARRAY);
		return buffer;
	}
	private StringBuilder generateJSONWithSelectedItem(CLevel clevel, EadTreeParams eadParams, Locale locale) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
		addBefore(buffer, clevel, eadParams, locale);
		buffer.append(START_ITEM);
		addTitle(buffer, clevel, locale);
		buffer.append(COMMA);
        addId(buffer, clevel.getClId());
		buffer.append(COMMA);
		buffer.append("\"selected\":true, \"activate\": true");

		addChildren(buffer, clevel, eadParams, locale);
		buffer.append(END_ITEM);

		addAfter(buffer, clevel, eadParams, locale);
		buffer.append(END_ARRAY);
		return generateParentCLevelJSON(clevel, buffer, eadParams, locale);
	}
	private StringBuilder generateParentCLevelJSON(CLevel child, StringBuilder childBuffer, EadTreeParams eadParams, Locale locale) {
		CLevel parent = child.getParent();
		if (parent != null) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(START_ARRAY);
			addBefore(buffer, parent, eadParams,locale);
			buffer.append(START_ITEM);
			addTitle(buffer, parent, locale);
			buffer.append(COMMA);
            addId(buffer, parent.getClId());
			buffer.append(COMMA);
			addExpand(buffer);
			buffer.append(COMMA);
			buffer.append(FOLDER_WITH_CHILDREN);
			buffer.append(childBuffer);
			buffer.append(END_ITEM);
			addAfter(buffer, parent, eadParams,locale);
			buffer.append(END_ARRAY);
			return generateParentCLevelJSON(parent, buffer, eadParams, locale);
		} else {
			EadContent eadContent = child.getEadContent();
			return generateRootJSON(eadContent, childBuffer, true,  eadParams, locale);
		}
	}
    private StringBuilder generateRootJSON(EadContent eadContent, StringBuilder childBuffer, boolean expand, EadTreeParams eadParams, Locale locale) {
		return generateRootJSON(eadContent, childBuffer, expand, true, eadParams, locale);
	}
	private StringBuilder generateRootJSON(EadContent eadContent, StringBuilder childBuffer, 
			boolean expand, boolean isWithPreface, EadTreeParams eadParams, Locale locale) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
		buffer.append(START_ITEM);
		addTitle(buffer, eadContent.getTitleproper(), locale);
		buffer.append(COMMA);
        if(isWithPreface){
		    addType(buffer, AbstractEadTag.FRONTPAGE_XSLT);
		    buffer.append(COMMA);
        }
		buffer.append(FOLDER_WITH_CHILDREN);
		buffer.append(generateChildrenOfRootJSON(eadContent, childBuffer,  expand, isWithPreface, locale));
		buffer.append(END_ITEM_WITH_RETURN);
		buffer.append(END_ARRAY);
		return buffer;
	}
	private StringBuilder generateChildrenOfRootJSON(EadContent eadContent, StringBuilder childBuffer, boolean expand, boolean isWithPreface, Locale locale) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
        buffer.append(START_ITEM);
        if(isWithPreface){
            addTitle(buffer, this.getMessageSource().getMessage("eadcontent.introduction", null, locale), locale);
            buffer.append(COMMA);
            addType(buffer, AbstractEadTag.INTRODUCTION_XSLT);
            buffer.append(END_ITEM_WITH_COMMA);
            buffer.append(START_ITEM);
            addTitle(buffer, eadContent.getUnittitle(), locale);
            buffer.append(COMMA);
            addType(buffer, AbstractEadTag.DIDCONTENT_XSLT);
        } else {
            addTitle(buffer, eadContent.getUnittitle(), locale);
        }
        if (expand) {
            buffer.append(COMMA);
            addExpand(buffer);
        }
		buffer.append(COMMA);

		buffer.append(FOLDER_WITH_CHILDREN);
		buffer.append(childBuffer);
		buffer.append(END_ITEM_WITH_RETURN);
		buffer.append(END_ARRAY);
		return buffer;

	}

	private void addChildren(StringBuilder buffer, CLevel clevel, EadTreeParams eadParams, Locale locale) {
		if (!clevel.isLeaf()) {
			buffer.append(COMMA);
			buffer.append(FOLDER_LAZY);
			buffer.append(COMMA);
			addId(buffer, clevel.getClId());
		}
	}

	private void addBefore(StringBuilder buffer, CLevel clevel, EadTreeParams eadParams, Locale locale) {
		if (!MORE_VALUE_AFTER.equalsIgnoreCase(eadParams.getMore()) && (clevel.getOrderId() > 0)) {
			buffer.append(START_ITEM);
			addMore(buffer, "eadcontent.more.before", MORE_VALUE_BEFORE, locale);
			buffer.append(COMMA);
			buffer.append(FOLDER_LAZY);
			buffer.append(COMMA);
			if (clevel.getParentClId() != null){
				buffer.append("\"parentId\":");
				buffer.append(" \"" + clevel.getParentClId() + "\" ");
				buffer.append(COMMA);
			}
			buffer.append("\"orderId\":");
			int orderId = clevel.getOrderId() - MAX_NUMBER_OF_CLEVELS;
			int max = MAX_NUMBER_OF_CLEVELS;
			if (orderId < 0) {
				max = clevel.getOrderId();
				orderId = 0;

			}
			buffer.append(" \"" + orderId + "\" ");
			buffer.append(COMMA);
			buffer.append("\"max\":");
			buffer.append(" \"" + max + "\" ");
			buffer.append(END_ITEM);
			buffer.append(COMMA);

		}
	}

	private void addAfter(StringBuilder buffer, CLevel clevel, EadTreeParams eadParams, Locale locale) {
		if (!MORE_VALUE_BEFORE.equalsIgnoreCase(eadParams.getMore())) {
			buffer.append(COMMA);
			buffer.append(START_ITEM);
			addMore(buffer, "eadcontent.more.after", MORE_VALUE_AFTER, locale);
			buffer.append(COMMA);
			if (clevel.getParentClId() != null){
				buffer.append("\"parentId\":");
				buffer.append(" \"" + clevel.getParentClId() + "\" ");
				buffer.append(COMMA);
			}
			buffer.append("\"orderId\":");
			buffer.append(" \"" + (clevel.getOrderId() + 1) + "\" ");
			buffer.append(COMMA);
			buffer.append(FOLDER_LAZY);
			buffer.append(END_ITEM_WITH_RETURN);
		}

	}

	private void addTitle(StringBuilder buffer, CLevel clevel, Locale locale) {
		addTitle(buffer, clevel.getUnittitle(), locale);
	}

	private void addTitle(StringBuilder buffer, String title, Locale locale) {
		addTitle(null, buffer, title, locale);
	}

	private static void addId(StringBuilder buffer, Long clId) {
		buffer.append("\"id\":");
		buffer.append("\"");
		buffer.append(clId);
		buffer.append("\"");
	}


	
	private static void addType(StringBuilder buffer, String type) {
		buffer.append("\"type\":");
		buffer.append(" \"" + type);

		buffer.append("\" ");
	}
	private static void addExpand(StringBuilder buffer) {
		buffer.append("\"expand\":true");
	}
}
