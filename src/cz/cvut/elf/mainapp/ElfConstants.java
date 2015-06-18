package cz.cvut.elf.mainapp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ElfConstants {
	public static boolean DEBUG = true; 

	public static final String NAMESPACE_PREFIX = "cz.cvut.elf.";
	public static final String PACKAGE_PREFIX = "package:" + NAMESPACE_PREFIX;

	public static final String SERVER_DIR_URL = "http://jindra.webz.cz/BAP/";
	public static final String PLANETS_XML_URL = SERVER_DIR_URL + "planets.xml";
	public static final String XML_TAG_PLANET = "planet";
	public static final String XML_ATTR_CZ = "cz";
	public static final String XML_ATTR_EN = "en";

	public static final String EDIT_USER_INTENT_EXTRA = "EDIT_USER";
	public static final String BUNDLE_USER_NAME = "USER_NAME";
	public static final String BUNDLE_USER_ICON = "USER_ICON";
	public static final String BUNDLE_USER_ID = "USER_ID";
	public static final String BUNDLE_PLANET_SHORTCUT = "SHORTCUT";

	public static final char MALE = 'm';
	public static final char FEMALE = 'f';
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());

	public static final long UPDATE_CHECK_INTERVAL = 24 * 60 * 60 * 1000;

	private ElfConstants() {
	};
}
