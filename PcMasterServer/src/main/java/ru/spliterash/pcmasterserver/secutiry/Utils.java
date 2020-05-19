package ru.spliterash.pcmasterserver.secutiry;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Base64;

public class Utils {
    public static String removeStart(String string, String remove) {
        return string.substring(remove.length());
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String getVirtualHost(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName();
    }

    public static String replacePlaceholders(String str, boolean replace) {
        if (replace) {
            HttpServletRequest request = getRequest();
            if (request == null)
                return str;
            else
                return
                        str.replace("%domain%", getVirtualHost(request));
        } else
            return str;
    }

    public static HttpServletRequest getRequest() {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        } else
            return null;

    }
}
