package duong.rbs;

import org.apache.log4j.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.*;

public class PrimeCalculatorTest {
    private PrimeCalculator calculator;
    private Writer logContent;

    @Before
    public void setUp() {
        calculator = new PrimeCalculator();

        // Adding a log4j capture to see what PrimeCalculator is doing
        logContent = new StringWriter();
        Layout l = new PatternLayout("%m%n");
        WriterAppender wa = new WriterAppender(l, logContent);
        wa.setThreshold(Level.ALL);
        Logger log = Logger.getLogger(PrimeCalculator.class);
        log.addAppender(wa);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testOutputForEmptyResult() {
        String res = calculator.getPrimesBefore(0);
        assertTrue(res.equals(""));

        res = calculator.getPrimesBefore(-1);
        assertTrue(res.equals(""));
    }

    @Test
    public void testOutputForSmallResult() {
        String res = calculator.getPrimesBefore(10);
        assertTrue(res.equals("2,3,5,7"));
    }

    @Test
    public void testOutputForLargerResult() {
        String res = calculator.getPrimesBefore(1000);
        System.out.println(res);
        assertTrue(res.equals("2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97,101,103,107,109,113,127,131,137,139,149,151,157,163,167,173,179,181,191,193,197,199,211,223,227,229,233,239,241,251,257,263,269,271,277,281,283,293,307,311,313,317,331,337,347,349,353,359,367,373,379,383,389,397,401,409,419,421,431,433,439,443,449,457,461,463,467,479,487,491,499,503,509,521,523,541,547,557,563,569,571,577,587,593,599,601,607,613,617,619,631,641,643,647,653,659,661,673,677,683,691,701,709,719,727,733,739,743,751,757,761,769,773,787,797,809,811,821,823,827,829,839,853,857,859,863,877,881,883,887,907,911,919,929,937,941,947,953,967,971,977,983,991,997"));
    }

    @Test
    public void testNoDuplicateCalculation() {
        calculator.getPrimesBefore(1001);
        assertTrue(logContent.toString().contains("Calculating from 3 to 1001 completed"));
        calculator.getPrimesBefore(1200);
        // it should calculate from the 1001 instead of beginning
        assertTrue(logContent.toString().contains("Calculating from 1001 to 1200 completed"));
    }

    class ThreadCalculator<T extends String> implements Callable {
        private final long limit;
        private final PrimeCalculator calculator;
        private final CountDownLatch latch;

        public ThreadCalculator(long limit, PrimeCalculator calculator, CountDownLatch latch) {
            this.limit = limit;
            this.calculator = calculator;
            this.latch = latch;
        }

        @Override
        public String call() throws Exception {
            try {
                latch.countDown();
                latch.await();
            } catch (InterruptedException e) {
            }
            return calculator.getPrimesBefore(limit);
        }
    }

    @Test
    public void testMultiThreadAccess() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        final int params[] = {1201, 1501};
        ArrayList<Callable<String>> threads = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            threads.add(new ThreadCalculator(params[i], calculator, latch));
        }

        try {
            executor.invokeAll(threads);
            String log = logContent.toString();
            assertTrue((log.contains("Calculating from 3 to 1201 completed") && log.contains("Calculating from 1201 to 1501 completed"))
                    ||
                    (log.contains("Calculating from 3 to 1501 completed") && !log.contains("Calculating from 3 to 1201 completed")));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
