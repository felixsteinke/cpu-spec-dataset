package cpu.spec.scraper;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ScraperAppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ScraperAppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ScraperAppTest.class);
    }

    public void testApp() {
        assertTrue(true);
    }
}
