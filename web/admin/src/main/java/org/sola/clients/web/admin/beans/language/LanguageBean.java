package org.sola.clients.web.admin.beans.language;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sola.clients.web.admin.beans.AbstractBackingBean;
import org.sola.clients.web.admin.beans.helpers.LanguageBeanSorter;
import org.sola.common.StringUtility;
import org.sola.services.ejb.refdata.businesslogic.RefDataEJBLocal;
import org.sola.services.ejb.refdata.entities.Language;

@Named
@SessionScoped
public class LanguageBean extends AbstractBackingBean {

    @EJB
    RefDataEJBLocal refDataEjb;

    private Language currentLanguage;
    private Language[] languages;
    private static final String ADMIN_LANGAUGE = "admin_language";
    private final String delimiter = "::::";

    public LanguageBean() {
    }

    @PostConstruct
    private void init() {
        loadLanguages();
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getLocalizedPageUrl(String defaultPage) {
        try {
            if (defaultPage == null || defaultPage.equals("")) {
                return defaultPage;
            }

            String localizaedPage = defaultPage.substring(0, defaultPage.lastIndexOf("."))
                    + "_" + getLocaleCodeForBundle() + defaultPage.substring(defaultPage.lastIndexOf("."), defaultPage.length());
            URL pageUrl = FacesContext.getCurrentInstance().getExternalContext().getResource(localizaedPage);
            if (pageUrl != null) {
                return localizaedPage;
            }
            return defaultPage;
        } catch (MalformedURLException ex) {
            return defaultPage;
        }
    }

    private Language getCurrentLanguage() {
        if (currentLanguage != null) {
            return currentLanguage;
        }

        if (languages == null || languages.length < 1) {
            return new Language();
        }

        String langCode = null;

        // Get language from cookie
        Map<String, Object> cookies = FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();
        if (cookies != null) {
            Cookie langCookie = (Cookie) cookies.get(ADMIN_LANGAUGE);
            if (langCookie != null && langCookie.getValue() != null) {
                langCode = langCookie.getValue();
            }
        }

        // If no cookie found, get from browser
        if (langCode == null) {
            langCode = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale().toLanguageTag();
        }

        if (langCode != null && !langCode.equals("")) {
            // Search for language
            for (Language lang : languages) {
                if (lang.getCode().equalsIgnoreCase(langCode)) {
                    currentLanguage = lang;
                    break;
                }
            }
            // If language not found try with prefix only
            String langName = langCode;
            if (langCode.indexOf("-") > -1) {
                langName = langCode.substring(0, langCode.indexOf("-"));
            }
            for (Language lang : languages) {
                String langName2 = lang.getCode();
                if (langName2.indexOf("-") > -1) {
                    langName2 = langName2.substring(0, langName2.indexOf("-"));
                }
                if (langName2.equalsIgnoreCase(langName)) {
                    currentLanguage = lang;
                    break;
                }
            }
        }

        // If language is still not found, select default
        if (currentLanguage == null) {
            for (Language lang : languages) {
                if (lang.isIsDefault()) {
                    currentLanguage = lang;
                    break;
                }
            }
        }

        // If deafult language is not defined, select first available language
        if (currentLanguage == null) {
            currentLanguage = languages[0];
        }
        return currentLanguage;
    }

    public String getLocaleCodeForBundle() {
        return getCurrentLanguage().getCode().replace("-", "_");
    }

    public String getLocale() {
        return getCurrentLanguage().getCode();
    }

    public void setLocale(String locale) {
        if (locale != null && !locale.equals("") && languages != null) {
            // Search for existing language
            for (Language lang : languages) {
                if (lang.getCode().equalsIgnoreCase(locale)) {
                    currentLanguage = lang;
                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    Cookie cookie = new Cookie(ADMIN_LANGAUGE, currentLanguage.getCode());
                    cookie.setMaxAge(315360000);
                    response.addCookie(cookie);
                    // Reload languages
                    loadLanguages();
                    break;
                }
            }
        }
    }

    public void changeLocale(ValueChangeEvent e) {
        setLocale((String) e.getNewValue());
        try {
            // Redirect to the same page to avoid postback
            getExtContext().redirect(((HttpServletRequest) getRequest()).getRequestURI());
        } catch (IOException ex) {
        }
    }

    public Language[] getLanguages() {
        if (languages == null) {
            return new Language[]{};
        }
        return languages;
    }

    /**
     * Parses provided string containing multiple localized values and returns
     * string related to current language.
     *
     * @param mixedString Multilingual string.
     * @return
     */
    public String getLocalizedString(String mixedString) {
        return getLocalizedString(mixedString, getLocale());
    }

    /**
     * Parses provided string containing multiple localized values and returns
     * string related to requested language.
     *
     * @param mixedString Multilingual string.
     * @param langCode Language code
     * @return
     */
    public String getLocalizedString(String mixedString, String langCode) {
        if (StringUtility.isEmpty(mixedString)) {
            return mixedString;
        }
        String[] strings = mixedString.split(delimiter);
        String defaultValue = "";
        
        if (strings != null) {
            int i = 0;
            for (Language language : languages) {
                if (i >= strings.length) {
                    break;
                }
                if (language.getCode().equalsIgnoreCase(langCode)) {
                    return strings[i];
                }
                if (language.isIsDefault()) {
                    defaultValue = strings[i];
                }
                i += 1;
            }
        }
        
        if(!StringUtility.isEmpty(defaultValue)){
            return defaultValue;
        }
        return "";
    }

    public void loadLanguages() {
        String localeCode = getLocale();
        List<Language> langs = refDataEjb.getLanguages(localeCode);
        if (langs != null && (localeCode == null || localeCode.equals(""))) {
            // Reload languages after first initialization, when locale code was null
            languages = langs.toArray(new Language[langs.size()]);
            Arrays.sort(languages, new LanguageBeanSorter());
            langs = refDataEjb.getLanguages(getLocale());
        }
        if (langs != null) {
            languages = langs.toArray(new Language[langs.size()]);
            Arrays.sort(languages, new LanguageBeanSorter());
        }
    }
}
