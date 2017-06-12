package image.explorer.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomImage {
    private final ObjectProperty<ImageView> img;
    private final StringProperty fileName;
    private final StringProperty location;
    private final StringProperty timeStamp;

    public CustomImage() {
        this(null, null, null, null);
    }
    
    public CustomImage(Image img, String fileName, String location, String timeStamp) {
    	// Create and assign the ImageView to hold the image
    	ImageView imageView = new ImageView(img);
    	imageView.setPreserveRatio(true);
    	imageView.setFitHeight(92);
    	this.img = new SimpleObjectProperty<ImageView>(imageView);
    	// Assign the other field properties
        this.fileName = new SimpleStringProperty(fileName);
        this.location = new SimpleStringProperty(location);
        this.timeStamp = new SimpleStringProperty(timeStamp);
    }

    public String getFileName() { return fileName.get(); }
    public void setFileName(String fileName) { this.fileName.set(fileName); }
    public StringProperty fileNameProperty() { return fileName; }

    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }
    public StringProperty locationProperty() { return location; }

    public String getTimeStamp() { return timeStamp.get(); }
    public void setTimeStamp(String timeStamp) { this.timeStamp.set(timeStamp); }
    public StringProperty timeStampProperty() { return timeStamp; }

    public ImageView getImage() { return img.get(); }
    public void setImage(Image img) { this.img.set(new ImageView(img)); }
    public ObjectProperty<ImageView> imageProperty() { return img; }
}