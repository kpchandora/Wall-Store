package developer.code.kpchandora.wallstore;

/**
 * Created by kpchandora on 30/1/18.
 */

public class NotificationModel {

    private int id;
    private String title;
    private String content;
    private String imageUrl;
    private long timeStamp;
    private int flag;


    public NotificationModel(int id, String title, String content, String imageUrl, long timeStamp) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;

    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public String getImageUrl() {
        return imageUrl;
    }
}
