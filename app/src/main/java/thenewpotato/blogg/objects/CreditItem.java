package thenewpotato.blogg.objects;

/**
 * Created by thenewpotato on 8/20/17.
 */

public class CreditItem {

    public static final String PROJECT_CODE = "Project Code: ";
    public static final String COPYRIGHT = "Copyright (c) ";
    public static final String LICENSE = "License: ";

    public String name;
    public String codeUrl;
    public String copyrightInfo;
    public String licenseInfo;

    public CreditItem (String name, String codeUrl, String copyrightInfo, String licenseInfo) {
        this.name = name;
        this.codeUrl = codeUrl;
        this.copyrightInfo = copyrightInfo;
        this.licenseInfo = licenseInfo;
    }

}
