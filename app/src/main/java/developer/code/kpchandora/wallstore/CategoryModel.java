package developer.code.kpchandora.wallstore;

/**
 * Created by kpchandora on 31/1/18.
 */

public class CategoryModel {

    private String id;
    private String imageUrl;
    private String title;
    private int flag;

    public CategoryModel(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public CategoryModel(String id, String imageUrl, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getCategoryId() {
        return id;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getImageTitle() {
        return title;
    }

    public int getFlag() {
        return flag;
    }
}
