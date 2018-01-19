package developer.code.kpchandora.wallstore;

/**
 * Created by kpchandora on 15/1/18.
 */

public class ImagePOJO {

    private String imageUrl;
    private String newImageUrl;
    private String title;
    private String[] categoryArray;
    private int countOfImages;

    public ImagePOJO(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImagePOJO(String newImageUrl, String title, String[] categoryArray, int countOfImages ) {
        this.newImageUrl = newImageUrl;
        this.title = title;
        this.categoryArray = categoryArray;
        this.countOfImages = countOfImages;
    }

    public String[] getCategoryArray(){
        return categoryArray;
    }

    public String getNewImageUrl() {
        return newImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCountOfImages(){
        return countOfImages;
    }
}
