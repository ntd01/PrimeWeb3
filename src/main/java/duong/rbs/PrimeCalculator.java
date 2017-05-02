package duong.rbs;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PrimeCalculator {
    // cached the result here
    private final List<Long> primes;
    private long currentLimit;

    private ReadWriteLock lock;
    private Logger logger = Logger.getLogger(PrimeCalculator.class);

    public PrimeCalculator() {
        // use ArrayList to have fast access
        primes = new ArrayList<>();
        primes.add(2L);
        primes.add(3L);
        currentLimit = 3;
        lock = new ReentrantReadWriteLock();
    }

    /**
     * Using StringBuilder to construct the list of primes
     * Need to lock the array, otherwise we might have ConcurrentModificationException
     *
     * @param limit
     * @return the string with the list of primes up to and including limit
     */
    private String generateString(long limit) {
        lock.readLock().lock();
        try {
            StringBuilder res = new StringBuilder();
            boolean firstIndex = true;

            for (long i: primes) {
                if (i <= limit) {
                    if (!firstIndex)
                        res.append(',');
                    res.append(i);
                    firstIndex = false;
                } else
                    break;
            }

            return res.toString();
        } finally {
            lock.readLock().unlock();
        }
    }

    public long getCurrentLimit() {
        return currentLimit;
    }

    /**
     * Generate the list of primes up to and including limit
     *
     * @param limit
     * @return the string with the list of primes up to and including limit
     */
    public String getPrimesBefore(long limit) {
        logger.info("Getting request for " + limit + " current limit " + currentLimit);
        if (limit > currentLimit) {
            lock.writeLock().lock();
            try {
                // when we get the lock, the limit might have changed so to avoid recalculation, we need to check
                if (limit > currentLimit) {
                    logger.info("Calculating from " + currentLimit + " to " + limit);
                    if (currentLimit % 2 == 0) {
                        currentLimit = currentLimit + 1;
                    }

                    for (long n = currentLimit; n <= limit; n += 2) {
                        boolean isPrime = true;
                        for (long p : primes) {
                            if (n % p == 0) {
                                isPrime = false;
                                break;
                            }
                        }
                        if (isPrime) {
                            primes.add(n);
                        }
                    }

                    logger.info("Calculating from " + currentLimit + " to " + limit + " completed");
                    currentLimit = limit;
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

		limit = "Just changed";
		
        return generateString(limit);
    }
}
