package eu.archivesportaleurope.portal.tagcloud;

import org.junit.Test;

import java.text.Collator;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by yoannmoranville on 03/04/15.
 */
public class TagCloudUtilTest {
    @Test
    public void testIsComparatorOk() {
        String[] list = {"Loire", "Bétise", "Radio", "Éducation", "Semaine"};
        Collections.sort(Arrays.asList(list), new TagCloudComparator(Locale.FRANCE));
        for(String string : list) {
            System.out.println(string);
        }
    }

    private static class TagCloudComparator implements Comparator<String> {
        private Collator collator;
        public TagCloudComparator(Locale locale) {
            collator = Collator.getInstance(locale);
        }
        @Override
        public int compare(String o1, String o2) {
            return collator.compare(o1, o2);
        }

    }
}
