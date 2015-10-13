package com.nexacro.spring.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetUtil {

    private static Properties languageMappingProperties = new Properties();
    private static String languageMappingResourceName = "language_mapping.properties";
    private static final Logger logger = LoggerFactory.getLogger(CharsetUtil.class);

    static {
        
        InputStream inputStream = null;
        try {
            inputStream = CharsetUtil.class.getResourceAsStream(languageMappingResourceName);
            languageMappingProperties.load(inputStream);
        } catch (IOException e) {
             if(logger.isWarnEnabled()) {
                 logger.warn("fail to reading language to charset mapping file. properties file='"+languageMappingResourceName+"'");
             }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignore) {
                }
            }
        }

    }

    public static String getCharsetOfRequest(HttpServletRequest request, String defaultChasrset) {

        String charset = null;

        String contentsType = HttpUtil.getHeaderValue(request, HttpUtil.HEADER_CONTENT_TYPE);

        charset = getCharsetOfContentType(contentsType);

        if (charset == null) {
            String charsetValue = HttpUtil.getHeaderValue(request, HttpUtil.HEADER_ACCEPT_CHARSET);
            charset = getCharsetOfAcceptCharset(charsetValue);
        }

        // "ko-kr,ko;q=0.8,en-us;q=0.5,en;q=0.3"와 같이 여러개가 오는 경우가 있다.
        if (charset == null) {
            String languageValue = HttpUtil.getHeaderValue(request, HttpUtil.HEADER_ACCEPT_LANGUAGE);
            charset = getCharsetOfAcceptLanguage(languageValue);
        }

        if (charset == null) {
            charset = defaultChasrset;
        }

        if (charset == null) {
            // do nothing
        }
        // 이래도 null이면 그대로 반환해야 한다.
        // null도 의미를 갖는다.
        // 만약 null이 발생할 수 없다면 모델에 설정된 charset이 사용될 수 없다.

        return getTrimValue(charset);

    }

    private static String getCharsetOfContentType(String contentsType) {

        if (contentsType == null) {
            return null;
        }

        String charset = null;

        String[] contentsTypes = contentsType.split(";");

        boolean isUTF8Enabled = false;
        boolean isUTF16Enabled = false;
        for (int i = 0; i < contentsTypes.length; i++) {
            String[] keyAndValue = contentsTypes[i].split("=");

            if (keyAndValue.length == 2) {
                String trimedValue = keyAndValue[0].trim();

                if (trimedValue.equalsIgnoreCase("charset")) {
                    charset = keyAndValue[1];

                    if (!isUTF8Enabled) {
                        isUTF8Enabled = isUTF8Enabled(charset);
                    }

                    if (!isUTF16Enabled) {
                        isUTF16Enabled = isUTF16Enabled(charset);
                    }
                }
            }
        }

        return getOutputCharset(getTrimValue(charset), isUTF8Enabled, isUTF16Enabled);
    }

    private static String getCharsetOfAcceptCharset(String acceptCharsets) {

        if (acceptCharsets == null) {
            return null;
        }

        String charset = null;
        boolean isUTF8Enabled = false;
        boolean isUTF16Enabled = false;
        // "EUC-KR,utf-8;q=0.7,*;q=0.7"와 같이 여러개가 오는 경우가 있다.
        String[] charsetValues = acceptCharsets.split(",");

        double maxQuality = 0.0d;
        for (int i = 0; i < charsetValues.length; i++) {
            int index = charsetValues[i].indexOf(";q=");
            String charsetName = charsetValues[i];
            try {
                if (!Charset.isSupported(charsetName)) {
                    continue;
                }
            } catch (IllegalCharsetNameException e) {
                // do nothing
            }

            if (!isUTF8Enabled) {
                isUTF8Enabled = isUTF8Enabled(charsetName);
            }

            if (!isUTF16Enabled) {
                isUTF16Enabled = isUTF16Enabled(charsetName);
            }

            double quality = 1.0d;
            if (index >= 0) {
                charsetName = charsetValues[i].substring(0, index - 1);
                quality = Double.valueOf(charsetValues[i].substring(index + ";q=".length()));
            }

            if (quality >= maxQuality && maxQuality <= 1.0d) {

                charset = charsetName;
                maxQuality = quality;

            }
        }

        return getOutputCharset(getTrimValue(charset), isUTF8Enabled, isUTF16Enabled);
    }

    private static String getCharsetOfAcceptLanguage(String languagevalue) {

        if (languagevalue == null) {
            return null;
        }
        String charset = null;
        boolean isUTF8Enabled = false;
        boolean isUTF16Enabled = false;
        String[] languageValues = languagevalue.split(",");
        String charsetName = null;
        double maxQuality = 0.0d;
        for (int i = 0; i < languageValues.length; i++) {
            int index = languageValues[i].indexOf(";q=");
            String languageName = languageValues[i];
            double quality = 1.0d;
            if (index >= 0) {
                languageName = languageValues[i].substring(0, index - 1);
                quality = Double.valueOf(languageValues[i].substring(index + ";q=".length()));
            }

            charsetName = languageMappingProperties.getProperty(languageName);

            if (!isUTF8Enabled) {
                isUTF8Enabled = isUTF8Enabled(charsetName);
            }

            if (!isUTF16Enabled) {
                isUTF16Enabled = isUTF16Enabled(charsetName);
            }

            // 여기서는 위에서와 같이 isSupporting을 확인하지 않는다. properties파일에 설정되어 있는 값은
            // 유효하다는 전제 하에.
            if (charsetName != null) {
                if (quality >= maxQuality && maxQuality <= 1.0d) {
                    charset = charsetName;
                    maxQuality = quality;
                }
                break;
            }

        }
        if (languageValues != null && charset == null) {
            // Accept-Language 체크를 한다는 것은 Accept-Charset이 없고 Accept-Charset만 있는
            // 경우이다.
            // 그럼에도 불구하고 charset이 null이라면 X-UP에서 해당 언어가 뭔지를 모르는 것이다.
            // 이경우에는 기본값을 사용하자.
            charset = "UTF-8";
        }

        return getOutputCharset(getTrimValue(charset), isUTF8Enabled, isUTF16Enabled);
    }

    private static String getTrimValue(String charset) {
        if (charset != null) {
            return charset.trim();
        } else {
            return charset;
        }
    }

    private static boolean isUTF8Enabled(String charset) {
        if (charset != null && charset.trim().equalsIgnoreCase("utf-8")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isUTF16Enabled(String charset) {
        if (charset != null && charset.trim().equalsIgnoreCase("utf-16")) {
            return true;
        } else {
            return false;
        }
    }

    private static String getOutputCharset(String charset, boolean isUTF8Enabled, boolean isUTF16Enabled) {
        if (isUTF8Enabled) {
            return "utf-8";
        } else if (isUTF16Enabled) {
            return "utf-16";
        } else {
            return charset;
        }
    }

}
