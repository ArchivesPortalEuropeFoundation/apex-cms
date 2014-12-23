package eu.archivesportaleurope.portal.common.xslt;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.SingletonIterator;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.apache.log4j.Logger;

import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.factory.DAOFactory;
import eu.apenet.persistence.vo.FindingAid;

public class EadidCheckerExtension extends ExtensionFunctionDefinition {
	/**
	 * 
	 */
	private static final long serialVersionUID = 654874379518388994L;
	private static final StructuredQName funcname = new StructuredQName("ape",
			"http://www.archivesportaleurope.eu/xslt/extensions", "linked");
	private static final Logger LOG = Logger.getLogger(EadidCheckerExtension.class);
	private EadidCheckerCall eadidCheckerCall;

	public EadidCheckerExtension(Integer aiId, boolean preview) {
		this.eadidCheckerCall = new EadidCheckerCall(aiId, preview);
	}

	@Override
	public StructuredQName getFunctionQName() {
		return funcname;
	}

	@Override
	public int getMinimumNumberOfArguments() {
		return 1;
	}

	public int getMaximumNumberOfArguments() {
		return 1;
	}

	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] { SequenceType.OPTIONAL_STRING };
	}

	@Override
	public SequenceType getResultType(SequenceType[] sequenceTypes) {
		return SequenceType.OPTIONAL_STRING;
	}

	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return eadidCheckerCall;
	}

	class EadidCheckerCall extends ExtensionFunctionCall {
		private static final long serialVersionUID = 6761914863093344493L;
		private Integer aiId;
		private boolean preview;

		public EadidCheckerCall(Integer aiId, boolean preview) {
			this.aiId = aiId;
			this.preview = preview;
		}

		public SequenceIterator call(SequenceIterator[] arguments, XPathContext context) throws XPathException {
			if (arguments.length == 1) {
				Item firstArgument = arguments[0].next();
				String value = "notavailable";
				if (firstArgument != null) {
					String eadid = firstArgument.getStringValue();
					if (aiId != null) {
						EadDAO eadDao = DAOFactory.instance().getEadDAO();
						if (eadDao.isEadidIndexed(eadid, aiId, FindingAid.class) != null) {
							if (preview) {
								value = "indexed-preview";
							} else {
								value = "indexed";
							}
						} else if (eadDao.isEadidUsed(eadid, aiId, FindingAid.class) != null) {
							value = "notindexed";
						}

					}
				}
				return SingletonIterator.makeIterator(new StringValue(value));
			} else {
				return SingletonIterator.makeIterator(new StringValue("ERROR"));
			}
		}
	}
}
