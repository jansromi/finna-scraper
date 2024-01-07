package fh;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * The FinnaGenerator class is responsible for fetching Finna records asynchronously.
 * It takes a list of ISBNs and splits it into blocks. Each block is processed by a separate thread.
 * The class uses a fixed thread pool executor to manage the threads.
 * 
 * The process() method starts the threads and monitors their progress. When all threads are done,
 * it shuts down the executor service and returns a CompletableFuture that completes with a list of FinnaRecord objects.
 * 
 * The class also provides methods to check if all records have been fetched (isDone()),
 * to get the list of records fetched so far (yieldList()), and to shut down the executor service (shutdown()).
 * 
 * Example usage:
 * 
 * ```java
 * public static void main(String[] args) {
 *     List<String> isbnList = new ArrayList<>();
 *     isbnList.add("978-951-0-39214-0");
 *     isbnList.add("951-0-24994-7");
 *     isbnList.add("978-951-23-6668-2");
 * 
 *     FinnaGenerator generator = new FinnaGenerator(isbnList);
 * 
 *     CompletableFuture<List<FinnaRecord>> future = generator.process();
 * 
 *     List<FinnaRecord> lst = generator.yieldList();
 *     
 *     future.thenAccept(records -> {
 *         System.out.println("Fetched records: " + records.size());
 *         for (FinnaRecord record : records) {
 *             System.out.println(record.getRecordTitle());
 *         }
 *     });
 * }
 * ```
 */
public class FinnaGenerator {
    private static final int MAX_THREADS = 10;
    private final List<String> isbnToProcess;
    private final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
    // a list of futures that will contain the results of the threads
    private final List<Future<List<FinnaRecord>>> futureBlocks;
    private final List<FinnaRecord> yieldedRecords;
    

    public FinnaGenerator(List<String> isbnList) {
        this.isbnToProcess = isbnList;
        this.futureBlocks = new ArrayList<>();
        this.yieldedRecords = new ArrayList<>();
    }

    /**
     * Processes the Finna records asynchronously.
     * 
     * Method starts the threads, monitors their progress and shuts them down when they are done.
     * 
     * @return a CompletableFuture that completes with a list of FinnaRecord objects
     */
    public CompletableFuture<List<FinnaRecord>> process() {
        return CompletableFuture.supplyAsync(() -> {
            runThreads();
            while (!isDone()) {
                wait(1000);
            }
            shutdown();
            return yieldList();
        }, executorService);
    }
    
    /**
     * Starts the execution. First it splits the ISBN list into blocks
     * and then starts a thread for each block.
     * 
     * @
     */
    private void runThreads() {
        // calculate the block size
        int blockSize = (isbnToProcess.size() + (MAX_THREADS - 1)) / MAX_THREADS;
        List<List<String>> isbnBlocks = new ArrayList<>();

        // divide the isbnList into blocks
        for (int i = 0; i < isbnToProcess.size(); i += blockSize) {
            int end = Math.min(i + blockSize, isbnToProcess.size());
            List<String> block = isbnToProcess.subList(i, end);
            isbnBlocks.add(block);
        }

        // start a thread for each block
        for (List<String> isbnBlock : isbnBlocks) {
            System.out.println("Thread started for block: " + isbnBlock);
            Future<List<FinnaRecord>> future = executorService.submit(() -> processIsbnBlock(isbnBlock));
            futureBlocks.add(future);
        }
    }

    /**
     * Processes a block of ISBNs.
     * @param isbnBlock list of ISBNs
     * @return list of FinnaRecord objects
     */
    private List<FinnaRecord> processIsbnBlock(List<String> isbnBlock) {
        List<FinnaRecord> fetched = new ArrayList<>();
        for (String isbn : isbnBlock) {
            FinnaRecord record = FinnaQuery.fetchRecord(isbn);
            if (record == null) {
                continue;
            } else {
                fetched.add(record);
                yieldedRecords.add(record);
            }
            
        }
        return fetched;
    }

    /**
     * Shuts down the executor service.
     */
    private void shutdown() {
        System.out.println("Shutting down...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            } 
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    /**
     * @return list of records fetched so far
     */
    public List<FinnaRecord> yieldList() {
        return this.yieldedRecords;
    }

    /**
     * @return if all the items have been fetched
     */
    public boolean isDone() {
        for (Future<List<FinnaRecord>> future : futureBlocks) {
        if (!future.isDone()) {
            return false;
        }
    }
    return true;
    }

    /**
     * Waits for a given amount of time
     * @param millis sleep time in milliseconds
     */
    private static void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while sleeping");
        }
    }
}
