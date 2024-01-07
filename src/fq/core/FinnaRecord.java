package fh;

import java.util.List;

import org.json.JSONObject;

public class FinnaRecord {
    private String finnaId;
    private String isbn;
    private JSONObject record;

    public FinnaRecord(JSONObject record, String isbn, String finnaId) {
        this.record = record;
        this.isbn = isbn;
        this.finnaId = finnaId;
    }

    /**
     * Debug constructor
     * @param isbn
     */
    public FinnaRecord(String isbn) {
        this.record = new JSONObject();
        this.isbn = isbn;
        this.finnaId = "";
    }

    /**
     * @return The record as a JSONObject
     */
    public JSONObject getRecord() {
        record.put("isbn", isbn);
        record.put("finnaId", finnaId);
        return record;
    }

    /**
     * @return Title of the record
     */
    public String getRecordTitle() {
        return FinnaParser.parseRecordTitle(record);
    }

    /**
     * @return Languages of the record
     */
    public List<String> getRecordLanguages() {
        return FinnaParser.parseRecordLanguages(record);
    }

    /**
     * @return Primary and secondary authors of the record
     */
    public List<String> getRecordAuthors() {
        return FinnaParser.parseRecordAuthors(record);
    }

    /**
     * @return Subjects of the record
     */
    public List<String> getRecordSubjects() {
        return FinnaParser.parseRecordSubjects(record);
    }

    /**
     * @return Publishers of the record
     */
    public List<String> getRecordPublishers() {
        return FinnaParser.parseRecordPublishers(record);
    }

    /**
     * @return Publication dates of the record
     */
    public List<String> getRecordPublicationDates() {
        return FinnaParser.parseRecordPublicationDates(record);
    }

    /**
     * @return YKL classes of the record
     */
    public List<String> getRecordYKLClasses() {
        return FinnaParser.parseRecordYKLClasses(record);
    }
}
