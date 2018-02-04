package developer.code.kpchandora.wallstore;

/**
 * Created by kpchandora on 3/2/18.
 */

public class DownloadModel {

    private int id;
    private String uri;

    public DownloadModel(int id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }
}
