package duong.rbs;

import com.opensymphony.xwork2.ActionProxy;
import org.apache.struts2.StrutsTestCase;

public class CalculatePrimeActionTest extends StrutsTestCase {
    public void testCorrectInputs() throws Exception {
        ActionProxy proxy = getActionProxy("/10/text");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("success", result);
        assertEquals("2,3,5,7", primeAction.getResult());
    }

    public void testCorrectInputsXml() throws Exception {
        ActionProxy proxy = getActionProxy("/10/xml");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("success", result);
        assertEquals("&lt;result&gt;<br>\n" +
                "  &lt;request&gt;10&lt;/request&gt;<br>\n" +
                "  &lt;primes&gt;2,3,5,7&lt;/primes&gt;<br>\n" +
                "&lt;/result&gt;", primeAction.getResult());
    }

    public void testCorrectInputsJson() throws Exception {
        ActionProxy proxy = getActionProxy("/10/json");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("success", result);
        assertEquals("{<br>\n" +
                "\"Initial\": \"10\"<br>\n" +
                "\"Priimes\": [2,3,5,7]<br>\n" +
                "}<br>\n", primeAction.getResult());
    }

    public void testNotANumberInput() throws Exception {
        ActionProxy proxy = getActionProxy("/a");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("error", result);
        assertEquals("Invalid input value, only accept positive long value", primeAction.getResult());
    }

    public void testInvalidFormat() throws Exception {
        ActionProxy proxy = getActionProxy("/10/foo");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("error", result);
        assertEquals("Invalid format value, acceptable values are [json, text, xml]", primeAction.getResult());
    }

    public void testNegativeNumberInput() throws Exception {
        ActionProxy proxy = getActionProxy("/-10");

        CalculatePrimeAction primeAction = (CalculatePrimeAction) proxy.getAction();

        String result = proxy.execute();

        assertTrue(primeAction.getFieldErrors().size() == 0);
        assertEquals("error", result);
        assertEquals("Invalid input value, only accept positive long value", primeAction.getResult());
    }
}
