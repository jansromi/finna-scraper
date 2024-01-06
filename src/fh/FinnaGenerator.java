package fh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * The FinnaGenerator class is an iterator that generates FinnaRecord objects based on a list of ISBNs.
 * It uses a fixed thread pool of 10 threads to asynchronously fetch the FinnaRecord objects.
 * The generator can be iterated using the hasNext() and next() methods.
 * It also provides a method isDone() to check if all the items have been fetched.
 */
public class FinnaGenerator implements Iterator<FinnaRecord> {
    private final List<String> isbnList;
    private int currentIndex;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final List<Future<FinnaRecord>> futures;

    public FinnaGenerator(List<String> isbnList) {
        this.isbnList = isbnList;
        this.currentIndex = 0;
        this.futures = new ArrayList<>();

        for (String isbn : isbnList) {
            System.out.println("Säie käynnistetty");
            Future<FinnaRecord> future = executorService.submit(() -> FinnaQuery.fetchRecord(isbn));
            futures.add(future);
        }
    }

    @Override
    public boolean hasNext() {
        return currentIndex < isbnList.size();
    }

    @Override
    public FinnaRecord next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more items to fetch");
        }

        try {
            Future<FinnaRecord> future = futures.get(currentIndex);
            FinnaRecord record = future.get(10, TimeUnit.SECONDS); // Blocking call to get the result
            currentIndex++;
            return record;
        } catch (TimeoutException e) {
            System.out.println("Aikakatkaisu");
            return null;
        }
        
        catch (InterruptedException | ExecutionException e) {
            // Handle exceptions as needed
            return null; // Replace with actual error handling
        }
    }

    /**
     * Checks if all the items have been fetched
     */
    public boolean isDone() {
        for (Future<FinnaRecord> future : futures) {
            if (!future.isDone()) {
                return false;
            }
        }
        return true;
    }


    public static void main(String[] args) {
        List<String> isbnList = new ArrayList<>();
        isbnList.add("123");
        isbnList.add("456");
        isbnList.add("789");

        FinnaGenerator generator = new FinnaGenerator(isbnList);
        System.out.println("kestää..");
        if (generator.isDone()) {
            System.out.println("Kaikki haettu");
        } else {
            System.out.println("Ei vielä valmis");
        }
    }
    
}
