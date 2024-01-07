
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fh.FinnaQuery;
import fh.FinnaRecord;

public class FinnaQueryTest {

    @Test
    public void testFetchingABook() {
        Assertions.assertDoesNotThrow(() -> {
            FinnaRecord pelonValtakunta = FinnaQuery.fetchRecord("978-952-483-142-0");
            Assertions.assertTrue(pelonValtakunta.getRecordTitle().contains("Pelon valtakunta"), "Book title should contain 'Pelon valtakunta' but was " + pelonValtakunta.getRecordTitle());
        });
    }
}