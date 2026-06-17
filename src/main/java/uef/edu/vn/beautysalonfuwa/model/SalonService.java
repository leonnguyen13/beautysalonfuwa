package uef.edu.vn.beautysalonfuwa.model;

public class SalonService {
    private int id;
    private String name;
    private String description;
    private String priceText;
    private String imageUrl;
    private String category;

    public SalonService() {
    }

    public SalonService(int id, String name, String description, String priceText, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceText = priceText;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
