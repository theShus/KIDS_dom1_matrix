package App.data;

public class FileJob {

    private final String path;
    private final String corpusName;

    public FileJob( String path, String corpusName) {
        this.path = path;
        this.corpusName = corpusName;
    }

    public String getPath() {
        return path;
    }

    public String getCorpusName() {
        return corpusName;
    }

}
