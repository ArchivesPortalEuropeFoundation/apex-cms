package eu.archivesportaleurope.portal.search.ead.tree;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.solr.client.solrj.response.FacetField.Count;

import eu.apenet.commons.solr.SolrValues;

public class TreeFacetValue {
	public enum Type {CLEVEL, COUNTRY, ARCHIVAL_INSTITUTION, FOND}
    private String id;
    private String name;
    private long orderId;
    private String count;
    private boolean leaf;
	public TreeFacetValue(Count count, Type type, Locale locale){

        String temp = count.getName();
        int lastColonIndex = temp.lastIndexOf(":");
        
        //temp solution... has to be reverted back
        if (lastColonIndex == -1) {
            lastColonIndex = temp.lastIndexOf("|");
        }
        //
        id = temp.substring(lastColonIndex + 1);
        temp = temp.substring(0, lastColonIndex);
        lastColonIndex = temp.lastIndexOf(":");
        //temp solution... has to be reverted back
        if (lastColonIndex != -1) {
            //
            String leaf = temp.substring(lastColonIndex + 1);
            this.leaf = SolrValues.TYPE_LEAF.equals(leaf);
            temp = temp.substring(0, lastColonIndex);
            //temp solution... has to be reverted back
        }
        //
        
        if (Type.CLEVEL.equals(type)) {
            int firstColonIndex = temp.indexOf(":");
            //temp solution... has to be reverted back
            if (firstColonIndex == -1) {
                firstColonIndex = temp.indexOf("|");
            }//
            orderId = Long.parseLong(temp.substring(0, firstColonIndex));
            name = temp.substring(firstColonIndex + 1);
        } else {
            name = temp.substring(0);
        }
        this.count = NumberFormat.getInstance(locale).format(count.getCount());

    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }
    public long getOrderId() {
        return orderId;
    }
    public boolean isLeaf() {
        return leaf;
    }

}
