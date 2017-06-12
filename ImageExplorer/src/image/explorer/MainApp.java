package image.explorer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import image.explorer.model.*;
import image.explorer.view.ImageExplorerController;
import image.explorer.view.RootLayoutController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.com.lynden.gmapsfx.javascript.object.GoogleMap;
import main.java.com.lynden.gmapsfx.javascript.object.LatLong;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private ObservableList<CustomImage> imageData = FXCollections.observableArrayList();
    private ObservableList<WhereClauseRow> whereClauseData = FXCollections.observableArrayList();
    
    private ImageExplorerController controller;
    private RootLayoutController rootController;
    private int total;
    private ProgressCount count;
    private final HttpsClient https = new HttpsClient();
    private ArrayList<ObservableList<String>> imageTagList;
    private ConnectionDriver cd = new ConnectionDriver();
    
    private int numThreads = 1;
    private Thread[] threads = new Thread[0];
    
    private GoogleMap map;
    private DBCommands command = null;
    
    public MainApp() {
    	
    }
    
    public void updateImageData(String[] selectColumns, String[] whereColumns, String[] whereValues, String[] whereBooleans) {
    	controller.showProgress();
    	// Retrieve data from database (Format for String[]: {"FileID"};
    	if(command == null) {
    		command = new DBCommands(cd.getDB());
    		System.out.println("Made DBCommands");
    	}
    	
    	String tableName = "tblFileData ORDER BY FileID DESC LIMIT 0, 1";
    	
    	int max = command.getNextPID()-1;
    	imageTagList = new ArrayList<ObservableList<String>>();
    	for(int i = 0; i < max; i++)
    		imageTagList.add(null);
		
    	String[] tables = {"tblFileData", "tblSubCollections", "tblTags"};
    	String[] joins = null;
    	String[] joinOns = {"tblFileData.FileID", "tblSubCollections.FileID", "tblFileData.FileID", "tblTags.FileID"};
    	String order = "tblFileData.FileID";
		if(whereColumns != null) {
			joins = new String[2];
			joins[0] = "JOIN";
			joins[1] = "JOIN";
		}
		else {
			joins = new String[2];
			joins[0] = "LEFT JOIN";
			joins[1] = "LEFT JOIN";
		}
		
		total = command.count(tables, joins, joinOns, order, whereColumns, whereValues, whereBooleans);
		
		count.reset();
		
		String[][] res = command.select(selectColumns, tables, joins, joinOns, order, whereColumns, whereValues, whereBooleans);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		interruptThreads();
		imageData.clear();
		threads = new Thread[numThreads];
		Thread threadManager = new Thread(new Runnable() {
			@Override
			public void run() {
				controller.showProgress();
				synchronized(controller) {
					try { while(!controller.mapLoaded) controller.wait(); }
					catch(InterruptedException e) {  }
				}
				Instant start = Instant.now();
				for(int i = 0; i < numThreads; i++) {
					threads[i] = new Thread(new FilePopulate(getMainApp(), numThreads, i, total, count, imageTagList, res, formatter, imageData, controller, https));
			    	threads[i].start();
				}
		    	try {
		    		for(int i = 0; i < numThreads; i++)
		    			threads[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	controller.comboBoxSelect();
		    	Instant end = Instant.now();
		    	long ellapsedTime = end.toEpochMilli() - start.toEpochMilli();
		    	long minutes = ellapsedTime/60000;
		    	String fEllapsedTime;
		    	if(minutes == 0)
		    		fEllapsedTime = ellapsedTime/1000 + "." + ellapsedTime%1000 + " seconds.";
		    	else {
		    		long mEllapsedTime = ellapsedTime%60000;
		    		fEllapsedTime = minutes + ":" + mEllapsedTime/1000 + "." + mEllapsedTime%1000 + " minutes.";
		    	}
		    	ArrayList<LatLong> latLngs = new ArrayList<LatLong>();
		    	boolean found = false;
		    	for(CustomImage image : imageData) {
		    		if(image != null) {
		    			for(LatLong latLng : latLngs) {
		    				if(latLng != null) {
		    					try {
		    						if(Double.compare(image.getLatLong().getLatitude(), latLng.getLatitude()) == 0 && Double.compare(image.getLatLong().getLongitude(), latLng.getLongitude()) == 0)
		    							found = true;
		    					}
		    					catch(Exception e) {
		    						//System.out.println("Caught Exception.");
		    						e.printStackTrace();
		    					}
		    				}
		    			}
		    			if(!found)
		    				latLngs.add(image.getLatLong());
		    			found = false;
		    			
		    		}
		    	}
		    	int i = 0;
		    	for(; i < latLngs.size(); i++) {
		    		if(latLngs.get(i) != null)
		    			controller.mapUpdate(latLngs.get(i));
		    	}
		    	//controller.mapCenter(latLngs.get(i-1));
				System.out.println("\r\nPopulated " + count.getCount() + " images in " + fEllapsedTime);
				controller.hideProgress();
			}
		});
		threadManager.start();
    }
    
    public ConnectionDriver getConnectionDriver() { return cd; }
    
    public MainApp getMainApp() { return this; }
    
    public ImageExplorerController getController() { return controller; }

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
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
          public void handle(WindowEvent we) {
              System.out.println("\r\nStage is closing...");
              interruptThreads();
              System.out.println("\r\nStage closed.");
              cd.closeConnections();
          }
      });
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            rootController = loader.getController();
            rootController.setMainApp(this);

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
            controller = loader.getController();
            controller.setMainApp(this);
            
        	count = new ProgressCount();
        	//updateImageData(new String[]{"*"}, null, null, null);
        	
        	WhereClauseRow whereRow = new WhereClauseRow();
        	ComboBox<String> operatorBox = whereRow.getOperatorBox();
        	whereRow.setOperatorBox(operatorBox);
        	whereClauseData.add(whereRow);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    private void interruptThreads() {
		for(int i = 0; i < threads.length; i++) {
			threads[i].interrupt();
			try { threads[i].join(); } catch (InterruptedException e) { e.printStackTrace(); }
		}
    }

    public static void main(String[] args) {
    	// Launch the GUI
        launch(args);
    }
    
    public void setMap(GoogleMap map) {
    	this.map = map;
    }
}

/*
Path dir = Paths.get("images");
try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.{bmp,gif,jpg,jpeg,png}")) {
	for (Path entry: stream) { 
		File file = entry.toFile();
		String location = "";
		int randLocation = (int)(Math.random()*5);
		if(randLocation == 1)
			location = "Mountain View, CA";
		else if(randLocation == 2)
			location = "Tacoma, WA";
		else if(randLocation == 3)
			location = "Cheney, WA";
		else if(randLocation == 4)
			location = "Vale, AZ";
		imageData.add(new CustomImage(new Image(new FileInputStream(file), 100, 0, true, false), file.getName(), location, LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()))); 
	}
} catch(Exception e) { System.err.println(e); }
*/