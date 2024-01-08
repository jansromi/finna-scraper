
## Classes

### Core classes

- [`FinnaGenerator`](src/fh/FinnaGenerator.java): Class for fetching Finna records asynchronously. It takes a list of ISBNs and splits it into blocks. Each block is processed by a separate thread.

- [`FinnaParser`](src/fh/FinnaParser.java): Parses the Finna records.

- [`FinnaQuery`](src/fh/FinnaQuery.java): Represents a query to fetch Finna records from Finna api.

- [`FinnaRecord`](src/fh/FinnaRecord.java): Represents a Finna record.

## Usage

```java
List<String> isbnList = new ArrayList<>();
isbnList.add("978-951-0-39214-0");
isbnList.add("951-0-24994-7");
isbnList.add("978-951-23-6668-2");

FinnaGenerator generator = new FinnaGenerator(isbnList);

CompletableFuture<List<FinnaRecord>> future = generator.process();

List<FinnaRecord> lst = generator.yieldList();

future.thenAccept(records -> {
    System.out.println("Fetched records: " + records.size());
    for (FinnaRecord record : records) {
        System.out.println(record.getRecordTitle());
    }
});