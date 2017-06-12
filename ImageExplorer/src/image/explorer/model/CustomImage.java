package image.explorer.model;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.java.com.lynden.gmapsfx.javascript.object.LatLong;

public class CustomImage {
	public class ComparableTextField extends TextField implements Comparable<ComparableTextField> {
		@Override
		public int compareTo(ComparableTextField other) {
			return this.getText().compareTo(other.getText());
		}
	}
	public class ComparableComboBox extends ComboBox<String> implements Comparable<ComparableComboBox> {
		@Override
		public int compareTo(ComparableComboBox other) {
			return ((String)this.getSelectionModel().getSelectedItem()).compareTo((String)other.getSelectionModel().getSelectedItem());
		}
	}
	
    private final ObjectProperty<ImageView> img;
    private final StringProperty fileName;
    private final ObjectProperty<ComparableTextField> route;
    private final StringProperty location;
    private final ObjectProperty<LocalDateTime> timeStamp;
    private final SimpleObjectProperty<ComparableComboBox> tags;
    private LatLong latLng;
    //private final StringProperty tags;

    public CustomImage() {
        this(null, null, null, null, null, null, null, null);
    }
    
    public CustomImage(Image img, String fileName, String route, String location, String lat, String lng, LocalDateTime timeStamp, ObservableList<String> tag) {
    	// Create and assign the ImageView to hold the image
    	ImageView imageView = new ImageView(img);
    	imageView.setPreserveRatio(true);
    	imageView.setFitHeight(50);
    	this.img = new SimpleObjectProperty<ImageView>(imageView);
    	// Assign the other field properties
        this.fileName = new SimpleStringProperty(fileName);
        ComparableTextField routeField = new ComparableTextField();
        routeField.setText(route);
        routeField.setStyle("-fx-font: 12px \"Serif\";");
        this.route = new SimpleObjectProperty<ComparableTextField>(routeField);
        this.location = new SimpleStringProperty(location);
        try { 
        	double dlat = Double.parseDouble(lat), dlng = Double.parseDouble(lng); 
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    latLng = new LatLong(Double.parseDouble(lat), Double.parseDouble(lng));
                }
            });
        }
        catch(Exception e) {  }
        this.timeStamp = new SimpleObjectProperty<LocalDateTime>(timeStamp);
        this.tags = new SimpleObjectProperty<ComparableComboBox>(createTags(tag));
    }
    
    public String get(String header) {
    	if(header.equals("File Name")) 
    		return fileName.get();
    	else if(header.equals("Route"))
    		return route.get().getText();
    	else if(header.equals("Location"))
    		return location.get();
    	else if(header.equals("Time Stamp"))
    		return timeStamp.get().toString();
    	else if(header.equals("Tags")) {
    		String tagsStr = "";
    		ObservableList<String> list = tags.get().getItems();
    		if(list.size() > 0) {
    			int i = 0;
    			for(; i < list.size()-1; i++)
    				tagsStr += list.get(i) + ", ";
    			tagsStr += list.get(i);
    		}
    		return tagsStr;
    	}
    	return "";
    }

    public String getFileName() { return fileName.get(); }
    public void setFileName(String fileName) { this.fileName.set(fileName); }
    public StringProperty fileNameProperty() { return fileName; }

    public String getRoute() { return route.get().getText(); }
    public void setRoute(String route) { this.route.get().setText(route); }
    public ObjectProperty<ComparableTextField> routeProperty() { return route; }

    public String getLocation() { return location.get(); }
    public void setLocation(String location) { this.location.set(location); }
    public StringProperty locationProperty() { return location; }

    public LocalDateTime getTimeStamp() { return timeStamp.get(); }
    public void setTimeStamp(LocalDateTime timeStamp) { this.timeStamp.set(timeStamp); }
    public ObjectProperty<LocalDateTime> timeStampProperty() { return timeStamp; }
    
    public ComboBox<String> getTags() { return tags.get(); }
    public ComparableComboBox createTags(ObservableList<String> tag) {
        ComparableComboBox comboBox = new ComparableComboBox();
        comboBox.setItems(tag);
        comboBox.setStyle("-fx-font: 12px \"Serif\";");
        return comboBox;
    }
    public void setTags(ObservableList<String> tag) { 
    	this.tags.set(createTags(tag)); 
    }
    public ObjectProperty<ComparableComboBox> tagsProperty() { return tags; }

    public ImageView getImage() { return img.get(); }
    public void setImage(Image img) { this.img.set(new ImageView(img)); }
    public ObjectProperty<ImageView> imageProperty() { return img; }
    
    public LatLong getLatLong() { return latLng; }
}