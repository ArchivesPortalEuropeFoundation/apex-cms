package eu.archivesportaleurope.portal.common.tree;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceResponse;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.search.eaccpf.EacCpfSearcher;
import eu.archivesportaleurope.portal.search.ead.EadSearcher;
import eu.archivesportaleurope.portal.search.eag.EagSearcher;

public abstract class AbstractJSONWriter {

	private static final String APPLICATION_JSON = "application/json";
	protected final Logger log = Logger.getLogger(this.getClass());
	protected static final String FOLDER_LAZY = "\"isFolder\": true, \"isLazy\": true";
	protected static final String UTF8 = "UTF-8";
	protected static final String END_ARRAY = "]\n";
	protected static final String START_ARRAY = "[\n";
	protected static final String END_ITEM = "}";
	protected static final String START_ITEM = "{";
	protected static final String FOLDER_WITH_CHILDREN = "\"isFolder\": true, \"children\": \n";
	protected static final String COMMA = ",";
	private static final String MORE_CLASS = "more";
	private static final String MORE_TEXT = "advancedsearch.context.more";
	protected static final String ADVANCEDSEARCH_TEXT_NOTITLE = "advancedsearch.text.notitle";
	private EadSearcher eadSearcher;
	private EacCpfSearcher eacCpfSearcher;
	private EagSearcher eagSearcher;
	private MessageSource messageSource;
	
	public EadSearcher getEadSearcher() {
		return eadSearcher;
	}

	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public EacCpfSearcher getEacCpfSearcher() {
		return eacCpfSearcher;
	}

	public void setEacCpfSearcher(EacCpfSearcher eacCpfSearcher) {
		this.eacCpfSearcher = eacCpfSearcher;
	}


	
	public EagSearcher getEagSearcher() {
		return eagSearcher;
	}

	public void setEagSearcher(EagSearcher eagSearcher) {
		this.eagSearcher = eagSearcher;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected static void addStartArray(StringBuilder buffer) {
		buffer.append(START_ARRAY);
	}

	protected static void addEndArray(StringBuilder buffer) {
		buffer.append(END_ARRAY);
	}

	protected static Writer getResponseWriter(ResourceResponse resourceResponse) throws UnsupportedEncodingException,
			IOException {
		resourceResponse.setCharacterEncoding(UTF8);
		resourceResponse.setContentType(APPLICATION_JSON);
		return new OutputStreamWriter(resourceResponse.getPortletOutputStream(), UTF8);
	}

	protected static void writeToResponseAndClose(StringBuilder stringBuilder, Writer writer)
			throws UnsupportedEncodingException, IOException {
		writer.write(stringBuilder.toString());
		writer.flush();
		writer.close();
	}

	protected static void writeToResponseAndClose(StringBuilder stringBuilder, ResourceResponse resourceResponse)
			throws UnsupportedEncodingException, IOException {
		Writer writer = getResponseWriter(resourceResponse);
		writer.write(stringBuilder.toString());
		writer.flush();
		writer.close();
	}
	protected static void writeToResponseAndClose(List<? extends TreeNode> treeNodes, ResourceResponse resourceResponse)
			throws UnsupportedEncodingException, IOException {
		Writer writer = getResponseWriter(resourceResponse);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(TreeNode.START_ARRAY);
		boolean first = true;
		for (TreeNode treeNode: treeNodes){
			if (first){
				first = false;
			}else {
				stringBuilder.append(TreeNode.COMMA);
			}
			stringBuilder.append(treeNode.toString());
		}
		stringBuilder.append(TreeNode.END_ARRAY);
		writer.write(stringBuilder.toString());
		writer.flush();
		writer.close();
	}
	protected void addMore(TreeNode treeNode, Locale locale) {
		addMore(treeNode,MORE_TEXT, "true", locale);

	}
	protected void addMore(TreeNode treeNode, String moreText, String moreType, Locale locale) {
		String title = this.getMessageSource().getMessage(moreText, null, locale);
		addTitle(treeNode, MORE_CLASS, title, locale);
		treeNode.setMore(moreType);
	}
	protected void addMore(StringBuilder buffer, Locale locale) {
		addMore(buffer,MORE_TEXT, "true", locale);

	}
	protected void addMore(StringBuilder buffer, String moreText, String moreType, Locale locale) {
		String title = this.getMessageSource().getMessage(moreText, null, locale);
		addTitle(MORE_CLASS,buffer, title, locale);
		buffer.append(COMMA);
		buffer.append("\"more\":");
		buffer.append(" \"" + moreType + "\"");		
	}
	protected static void addNoIcon(StringBuilder buffer) {
		buffer.append("\"icon\":");
		buffer.append(" false");
		buffer.append(COMMA);
	}
	protected void addTitle(String styleClass, StringBuilder buffer, String title, Locale locale) {
		addNoIcon(buffer);
		String convertedTitle = PortalDisplayUtil.replaceQuotesAndReturnsForTree(title);
		convertedTitle = PortalDisplayUtil.replaceLessThan(convertedTitle);
		boolean hasTitle = convertedTitle != null && convertedTitle.length() > 0;
		if (!hasTitle) {
			if (styleClass == null){
				styleClass = "notitle";
			}else {
				styleClass += " notitle";
			}
		}
		
		if (styleClass != null){
			buffer.append("\"addClass\":");
			buffer.append(" \"" + styleClass + "\"");
			buffer.append(COMMA);
		}
		
		buffer.append("\"title\":\"");
		if (hasTitle){
			buffer.append(convertedTitle);
		}else {
			buffer.append(this.getMessageSource().getMessage(ADVANCEDSEARCH_TEXT_NOTITLE, null, locale));
		}
		buffer.append("\"");
	}
	protected static void addNoIcon(TreeNode dynaTreeNode) {
	}
	protected void addTitle(TreeNode dynaTreeNode, String styleClass, String title, Locale locale) {
		String convertedTitle = PortalDisplayUtil.replaceQuotesAndReturnsForTree(title);
		boolean hasTitle = convertedTitle != null && convertedTitle.length() > 0;
		if (!hasTitle) {
			if (styleClass == null){
				styleClass = "notitle";
			}else {
				styleClass += " notitle";
			}
		}
		dynaTreeNode.setCssClass(styleClass);
	
		if (hasTitle){
			dynaTreeNode.setTitle(convertedTitle);
		}else {
			dynaTreeNode.setTitle(this.getMessageSource().getMessage(ADVANCEDSEARCH_TEXT_NOTITLE, null, locale));
		}
	}
	protected static void addExpand(StringBuilder buffer) {
		buffer.append("\"expand\":true");
	}
	protected static void addStart(StringBuilder buffer, Integer start) {
		buffer.append("\"start\":");
		buffer.append(" \"" + start + "\"");
	}
	protected static void addStart(TreeNode node, Integer start) {
		node.setStart(start);
	}
}
