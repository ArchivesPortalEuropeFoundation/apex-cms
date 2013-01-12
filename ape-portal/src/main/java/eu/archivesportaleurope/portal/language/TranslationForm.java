package eu.archivesportaleurope.portal.language;

import java.util.List;

/**
 * User: Yoann Moranville
 * Date: 11/01/2013
 *
 * @author Yoann Moranville
 */
public class TranslationForm {

    private List<Translation> translations;

    public TranslationForm() {}

    public TranslationForm(List<Translation> translations) {
        this.translations = translations;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    }
}
