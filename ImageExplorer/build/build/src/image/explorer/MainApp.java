package image.explorer;

import java.io.IOException;

import image.explorer.model.CustomImage;
import image.explorer.model.WhereClauseRow;
import image.explorer.view.ImageExplorerController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	public class OperatorChangeListener implements ChangeListener {
        @Override public void changed(ObservableValue ov, Object t, Object t1) {
        	WhereClauseRow newRow = new WhereClauseRow();
        	ComboBox<String> newOperatorBox = newRow.getOperatorBox();
        	newOperatorBox.valueProperty().addListener(new OperatorChangeListener());
        	whereClauseData.add(newRow);
        }    
	}

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private ObservableList<CustomImage> imageData = FXCollections.observableArrayList();
    private ObservableList<WhereClauseRow> whereClauseData = FXCollections.observableArrayList();
    
    public MainApp() {
        // Add some sample data
    	for(int i = 0; i < 100; i++) {
    		if(i%7 == 0)
    	        imageData.add(new CustomImage(new Image("file:../../images/img1.JPG", 200, 0, true, false), "img1.JPG", "Crater Lake, OR", "3/29/2013 12:28 PM"));
    		else if(i%7 == 1)
    	        imageData.add(new CustomImage(new Image("file:../../images/img2.jpg", 200, 0, true, false), "img2.jpg", "Mountain View, CA", "12/6/2014 2:07 PM")); 
    		else if(i%7 == 2)
    			imageData.add(new CustomImage(new Image("file:../../images/img3.JPG", 200, 0, true, false), "img3.JPG", "Tacoma, WA", "6/14/2010 4:47 PM"));
    		else if(i%7 == 3)
    			imageData.add(new CustomImage(new Image("file:../../images/img4.jpg", 200, 0, true, false), "img4.jpg", "Tacoma, WA", "8/10/2014 8:46 AM"));
    		else if(i%7 == 4)
    			imageData.add(new CustomImage(new Image("file:../../images/img5.jpeg", 200, 0, true, false), "img5.jpeg", "Federal Way, WA", "8/13/2014 11:36 PM"));
    		else if(i%7 == 5)
    			imageData.add(new CustomImage(new Image("file:../../images/img6.jpg", 200, 0, true, false), "img6.jpg", "", "10/11/2013 1:54 AM"));
    		else if(i%7 == 6)
    			imageData.add(new CustomImage(new Image("file:../../images/img7.jpg", 200, 0, true, false), "img7.jpg", "Oregon", "8/19/2014 6:25 PM")); 			
    	}
    	WhereClauseRow whereRow = new WhereClauseRow();
    	ComboBox<String> operatorBox = whereRow.getOperatorBox();
    	operatorBox.valueProperty().addListener(new OperatorChangeListener());
    	whereRow.setOperatorBox(operatorBox);
    	whereClauseData.add(whereRow);
    }

    public ObservableList<CustomImage> getImageData() {
        return imageData;
    }

    public ObservableList<WhereClauseRow> getWhereClauseData() {
        return whereClauseData;
    }

    @Override
    public void start(Stage primaryStage) {
    	// Set the stage
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Image Explorer");

        initRootLayout();

        showImageExplorer();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showImageExplorer() {
        try {
            // Load Image Explorer.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ImageExplorer.fxml"));
            AnchorPane imageExplorer = (AnchorPane) loader.load();

            // Set Image Explorer into the center of root layout.
            rootLayout.setCenter(imageExplorer);

            // Give the controller access to the main app.
            ImageExplorerController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
    	// Launch the GUI
        launch(args);
    }
}