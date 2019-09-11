package util;

/**
 * @author Rich Dionne
 * @project BaMSensorSetup
 * @package util
 * @date 9/24/2018
 */
public enum OSCAddress {
    SETUP("/setup"),
    CALIBRATE("/calibrate"),
    SAVE_SETTINGS("/save_settings")
    ;

    private final String url;

    OSCAddress(String url) {
        this.url = url;
    }

    public static OSCAddress fromString(String text) {
        for(OSCAddress u : OSCAddress.values()) {
            if(u.url.equalsIgnoreCase(text)) {
                return u;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return url;
    }
}
