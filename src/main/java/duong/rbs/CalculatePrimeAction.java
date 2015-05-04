package duong.rbs;

import com.opensymphony.xwork2.ActionSupport;

import java.util.Arrays;

public class CalculatePrimeAction extends ActionSupport {
    // use a single instance of the PrimeCalculator but ensure thread-safe
    private static PrimeCalculator calculator = new PrimeCalculator();
    private final String FORMAT_JSON = "json";
    private final String FORMAT_TEXT = "text";
    private final String FORMAT_XML = "xml";

    private String upperLimit;
    private String[] validFormats = {FORMAT_JSON, FORMAT_TEXT, FORMAT_XML};

    private String format = FORMAT_JSON;
    private String result;

    public String execute() {
        String exCode = SUCCESS;

        boolean validFormat = false;
        for (String f: validFormats) {
            if (f.equals(format)) {
                validFormat = true;
                break;
            }
        }

        if (!validFormat) {
            exCode = setCodeAndResult(ERROR, "Invalid format value, acceptable values are " + Arrays.toString(validFormats));
        } else {
            try {
                int limit = Integer.parseInt(upperLimit);
                if (limit > 0) {
                    result = formatResult(calculator.getPrimesBefore(limit));
                } else {
                    exCode = setCodeAndResult(ERROR, "Invalid input value, only accept positive long value");
                }
            } catch (NumberFormatException e) {
                exCode = setCodeAndResult(ERROR, "Invalid input value, only accept positive long value");
            }
        }

        return exCode;
    }

    /**
     * @param result
     * @return formatted result based on user request
     */
    private String formatResult(String result) {
        switch (format) {
            case FORMAT_JSON: return "{<br>\n\"Initial\": \"" + upperLimit + "\"<br>\n" + "\"Priimes\": [" + result + "]<br>\n}<br>\n";
            case FORMAT_XML: return "&lt;result&gt;<br>\n  &lt;request&gt;" + upperLimit + "&lt;/request&gt;<br>\n  &lt;primes&gt;" + result + "&lt;/primes&gt;<br>\n&lt;/result&gt;";            default: return result;
        }
    }

    private String setCodeAndResult(String code, String result) {
        this.result = result;
        return code;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getResult() {
        return result;
    }
}
