package image.explorer.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

import main.java.com.lynden.gmapsfx.GoogleMapView;
import main.java.com.lynden.gmapsfx.MapComponentInitializedListener;
import main.java.com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import main.java.com.lynden.gmapsfx.javascript.event.MapStateEventType;
import main.java.com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import main.java.com.lynden.gmapsfx.javascript.event.StateEventHandler;
import main.java.com.lynden.gmapsfx.javascript.event.UIEventType;
import main.java.com.lynden.gmapsfx.javascript.object.GoogleMap;
import main.java.com.lynden.gmapsfx.javascript.object.InfoWindow;
import main.java.com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import main.java.com.lynden.gmapsfx.javascript.object.LatLong;
import main.java.com.lynden.gmapsfx.javascript.object.MapOptions;
import main.java.com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import main.java.com.lynden.gmapsfx.javascript.object.Marker;
import main.java.com.lynden.gmapsfx.javascript.object.MarkerOptions;

import image.explorer.MainApp;
import image.explorer.model.ConnectionDriver;
import image.explorer.model.CustomImage;
import image.explorer.model.CustomImage.ComparableComboBox;
import image.explorer.model.CustomImage.ComparableTextField;
import image.explorer.model.DBCommands;
import image.explorer.model.DBConnect;
import image.explorer.model.WhereClauseRow;
import image.explorer.view.RootLayoutController;

public class ImageExplorerController implements MapComponentInitializedListener {
    @FXML private GoogleMapView mapView;
    @FXML private Button gmaps;
    private GoogleMap map;
    public boolean mapLoaded;
    
	@FXML private TableView<CustomImage> imageTable;
    @FXML private TableColumn<CustomImage, ImageView> imageColumn;
    @FXML private TableColumn<CustomImage, String> fileNameColumn;
    @FXML private TableColumn<CustomImage, ComparableTextField> routeColumn;
    @FXML private TableColumn<CustomImage, String> locationColumn;
    @FXML private TableColumn<CustomImage, String> timeStampColumn;
    @FXML private TableColumn<CustomImage, ComparableComboBox> tagsColumn;
    
	@FXML private TableView<WhereClauseRow> queryTable;
    @FXML private TableColumn<WhereClauseRow, ComboBox<String>> headerColumn;
    @FXML private TableColumn<WhereClauseRow, TextField> valueColumn;
    @FXML private TableColumn<WhereClauseRow, ComboBox<String>> operatorColumn;
    @FXML private TableColumn<WhereClauseRow, Button> removeColumn;
    

    @FXML private SplitPane splitPane;
    @FXML private AnchorPane queryPane;
    @FXML private AnchorPane imagePane;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane detailPane;
    @FXML private RadioButton imageRadio;
    @FXML private RadioButton fileNameRadio;
    @FXML private RadioButton routeRadio;
    @FXML private RadioButton locationRadio;
    @FXML private RadioButton timeStampRadio;
    @FXML private RadioButton tagsRadio;
    @FXML private ImageView imageView;
    @FXML private ProgressBar progressBar;
    @FXML private Button scrollQuery;
    @FXML private Button scrollView;
    @FXML private Label populating;
    @FXML private Label currentState;
    @FXML private Label fileNameLabel;
    @FXML private Label routeLabel;
    @FXML private Label locationLabel;
    @FXML private Label timeStampLabel;
    
    public TableColumn<CustomImage, ImageView> getImageColumn() { return imageColumn; }

    // Reference to the main application.
    private MainApp mainApp;
    
    private ObservableList<CustomImage> mainImageList;
    
    //private ObservableList<CustomImage> queryImageList;

    public ImageExplorerController() { }

    @FXML
    private void initialize() {
    	scrollQuery();
    	mapView.addMapInializedListener(this);
    	// Add divider positioning events
    	SplitPane.Divider divider0 = splitPane.getDividers().get(0);
    	SplitPane.Divider divider1 = splitPane.getDividers().get(1);
    	divider0.positionProperty().addListener(new ChangeListener<Number>(){
    	    public void changed(ObservableValue<? extends Number> observableValue, Number oldWindowWidth, Number newWindowWidth){
    	    	sliderChanged(divider0, divider1);
    	    }
    	});
    	divider1.positionProperty().addListener(new ChangeListener<Number>(){
    	    public void changed(ObservableValue<? extends Number> observableValue, Number oldWindowWidth, Number newWindowWidth){
    	    	sliderChanged(divider0, divider1);
    	    }
    	});
    	// Allow user to select multiple rows in imageTable and add double click event
        imageTable.getSelectionModel().setSelectionMode(javafx.scene.control.SelectionMode.MULTIPLE);
        imageTable.setRowFactory( tv -> {
            TableRow<CustomImage> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                	imageChanged(row);
                	if(currentState.getText().equals("Query Mode") || currentState.getText().equals("Explorer Mode"))
                		scrollView();
                }
            });
            return row ;
        });
        // Set the cell values from data
        imageColumn.setCellValueFactory(cellData -> cellData.getValue().imageProperty());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        routeColumn.setCellValueFactory(cellData -> cellData.getValue().routeProperty());
        /*routeColumn.setComparator(new Comparator<String>() {
			@Override
			public int compare(TextField arg0, TextField arg1) {
	        	return 0;
			}
        });*/
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        timeStampColumn.setCellValueFactory(cellData -> {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            SimpleStringProperty property = new SimpleStringProperty();
            property.setValue(cellData.getValue().timeStampProperty().get().format(formatter));
            return property;
        });
        timeStampColumn.setComparator(new Comparator<String>() {
			@Override
			public int compare(String arg0, String arg1) {
	        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
				return LocalDateTime.parse(arg0, formatter).compareTo(LocalDateTime.parse(arg1, formatter));
			}
        });
        tagsColumn.setCellValueFactory(cellData -> cellData.getValue().tagsProperty());
        headerColumn.setCellValueFactory(cellData -> cellData.getValue().headerProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        operatorColumn.setCellValueFactory(cellData -> cellData.getValue().operatorProperty());
        removeColumn.setCellValueFactory(cellData -> cellData.getValue().removeProperty());
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        mainImageList = mainApp.getImageData();
        imageTable.setItems(mainImageList);
        queryTable.setItems(mainApp.getWhereClauseData());
    }
    
    private void sliderChanged(SplitPane.Divider divider0, SplitPane.Divider divider1) {
    	// Query Mode (Left slider open, right slider closed)
        if(divider0.getPosition() > 0.01 && divider1.getPosition() > 0.99) {
        	scrollQuery.setDisable(true);
        	currentState.setText("Query Mode");
        	//divider1.setPosition(1.0);
        }
    	// View Mode (Left slider closed, right slider open)
        else if(divider0.getPosition() < 0.01 && divider1.getPosition() < 0.99) {
        	scrollView.setDisable(true);
        	currentState.setText("Viewer Mode");
        	//divider0.setPosition(0.0);
        }
    	// Explorer Mode (Both sliders closed)
        else if(divider0.getPosition() < 0.01 && divider1.getPosition() > 0.99) {
        	scrollQuery.setDisable(false);
        	scrollView.setDisable(false);
        	currentState.setText("Explorer Mode");
        	//divider0.setPosition(0.0);
        	//divider1.setPosition(1.0);
        }
    	// Dual Mode (Both sliders open)
        else if(divider0.getPosition() > 0.01 && divider1.getPosition() < 0.99) {
        	scrollQuery.setDisable(false);
        	scrollView.setDisable(false);
        	currentState.setText("Dual Mode");
        }
    }
    
    private void imageChanged(TableRow<CustomImage> row) {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
    	CustomImage img = row.getItem();
    	String fileName = img.getFileName();
		InputStream in = mainApp.getConnectionDriver().downloadFile(Integer.parseInt(fileName.substring(fileName.indexOf("_")+1, fileName.indexOf("."))), fileName.substring(fileName.indexOf(".")), false);
		Image image = new Image(in);
		try { in.close(); } catch(Exception e) { System.out.println("Could not close file input stream."); }
    	imageView.setImage(image);
    	fileNameLabel.setText(img.getFileName());
    	routeLabel.setText(img.getRoute());
    	locationLabel.setText(img.getLocation());
    	timeStampLabel.setText(img.getTimeStamp().format(formatter));
    }
    
    private void scrollQuery() {
    	splitPane.setDividerPosition(0, 0.31);
    	splitPane.setDividerPosition(1, 1);
    	scrollQuery.setDisable(true);
    	scrollView.setDisable(false);
    	currentState.setText("Query Mode");
    }
    
    private void scrollView() {
    	splitPane.setDividerPosition(0, 0);
    	splitPane.setDividerPosition(1, 0.5);
    	scrollQuery.setDisable(false);
    	scrollView.setDisable(true);
    	currentState.setText("View Mode");
    }
    
    private boolean doubleFix = false;
    
    private void query() {
    	ObservableList<WhereClauseRow> queryItems = queryTable.getItems();
    	ArrayList<String> whereColumnsList = new ArrayList<String>();
    	ArrayList<String> whereValuesList = new ArrayList<String>();
    	ArrayList<String> whereBooleansList = new ArrayList<String>();
    	String[] selectColumns = {"*"}, 
    			 whereColumns = null,
    			 whereValues = null,
    			 whereBooleans = null;
    	if(!queryItems.get(0).getValue().equals("")) {
    		for(int i = 0; i < queryItems.size(); i++) {
    			WhereClauseRow whereClauseRow = queryItems.get(i);
    			String header = whereClauseRow.getHeader();
    			String value = whereClauseRow.getValue();
    			if(header.equals("File Name")) {
    				whereColumnsList.add("tblFileData.FileID");
    				whereValuesList.add(value.substring(0, value.indexOf(".")));
    				whereColumnsList.add("FileType");
    				whereValuesList.add(value.substring(value.indexOf(".")));
    				whereBooleansList.add("AND");
    			}
    			else if(header.equals("Collection")) {
    				whereColumnsList.add("SubCollectionName");
    				whereValuesList.add(value);
    			}
    			else {
    				whereColumnsList.add(header);
    				whereValuesList.add(value);
    			}
    			if(whereClauseRow.getOperator() != null)
    				whereBooleansList.add(whereClauseRow.getOperator());
    		}
    		whereColumns = whereColumnsList.toArray(new String[whereColumnsList.size()]);
    		whereValues = whereValuesList.toArray(new String[whereValuesList.size()]);
    		whereBooleans = whereBooleansList.toArray(new String[whereBooleansList.size()]);
    		System.out.println("booleans length: " + whereBooleans.length);
    		if(whereBooleans.length == 0)
    			whereBooleans = null;
    		
    		mainApp.updateImageData(selectColumns, whereColumns, whereValues, whereBooleans);
    		
        }
    	else
    		mainApp.updateImageData(selectColumns, null, null, null);
    	/*
    		boolean valid = false;
    		for(WhereClauseRow whereClauseRow : queryItems) {
    			String imageValue = image.get(whereClauseRow.getHeader());
    			String whereClauseValue = whereClauseRow.getValue();
    			if(imageValue.equals(whereClauseValue))
    				valid = true;
    		}
    		if(!valid) {
    			queryImageList.remove(i);
    			i = i-1;
    		}
    	imageTable.setItems(queryImageList);
    	*/
    }
    
    @FXML protected void click_scrollQuery(ActionEvent event) {
    	scrollQuery();
    }
    
    @FXML protected void click_scrollView(ActionEvent event) {
    	scrollView();
    }
    
    @FXML protected void click_query(ActionEvent event) {
    	query();
    }
    
    @FXML protected void click_maps(ActionEvent event) {
    	mapView.setVisible(!mapView.isVisible());
    }
    
    @FXML protected void click_import(ActionEvent event) {
    	RootLayoutController.imageImport(mainApp);
    }
    
    @FXML protected void click_export(ActionEvent event) {
    	exportData();
    }
    
    @FXML protected void click_delete(ActionEvent event) {
    	deleteFile();
    }
    
    @FXML protected void click_addRow(ActionEvent event) {
    	mainApp.getWhereClauseData().add(new WhereClauseRow());
    }
    
    public void exportData() {
    	try {
    	DirectoryChooser dc = new DirectoryChooser();
    	File dir = dc.showDialog(mainApp.getPrimaryStage().getScene().getWindow());
    	
    	ObservableList<Integer> intlist = imageTable.getSelectionModel().getSelectedIndices();
    	ObservableList<CustomImage> itemlist = imageTable.getItems();
    	for(Integer i : intlist) {
    		CustomImage image = itemlist.get(i.intValue());
    		String fName = image.getFileName();
    		File f = new File(dir.getAbsolutePath() + "\\" + fName);
    		FileOutputStream os;
				os = new FileOutputStream(f);

    		mainApp.getConnectionDriver().downloadFile(os, fName, false);
    	}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @FXML protected void click_update(ActionEvent event) {
    	updateData();
    }
    
    @FXML protected void toggle_query(ActionEvent event) {
    	if(imageRadio.isSelected() == true)
    		imageColumn.setVisible(true);
    	else
    		imageColumn.setVisible(false);
    	if(fileNameRadio.isSelected() == true)
    		fileNameColumn.setVisible(true);
    	else
    		fileNameColumn.setVisible(false);
    	if(routeRadio.isSelected() == true)
    		routeColumn.setVisible(true);
    	else
    		routeColumn.setVisible(false);
    	if(locationRadio.isSelected() == true)
    		locationColumn.setVisible(true);
    	else
    		locationColumn.setVisible(false);
    	if(timeStampRadio.isSelected() == true)
    		timeStampColumn.setVisible(true);
    	else
    		timeStampColumn.setVisible(false);
    	if(tagsRadio.isSelected() == true)
    		tagsColumn.setVisible(true);
    	else
    		tagsColumn.setVisible(false);
    }
    
    public void updateData() {
    	ConnectionDriver cd = mainApp.getConnectionDriver();
    	DBConnect db = cd.getDB();
    	DBCommands command = new DBCommands(db);
    	
    	// Get FileID's for each row currently in table.
    	ArrayList<String> alSubCollectionFileIDs = new ArrayList<>();
    	int i = 0;
    	String id;
    	while((id = this.fileNameColumn.getCellData(i)) != null) {
    		id = id.substring(id.indexOf("_")+1, id.indexOf("."));
    		alSubCollectionFileIDs.add(id);
    		i++;
    	}
    	String[] subCollectionFileIDs = alSubCollectionFileIDs.toArray(new String[alSubCollectionFileIDs.size()]);
    	
    	// Update SubCollectionName's (routes).
    	String[] subCollectionNames = new String[subCollectionFileIDs.length];
    	for(int j = 0; j < subCollectionFileIDs.length; j++) {
    		subCollectionNames[j] = this.routeColumn.getCellData(j).getText();
    	}
    	
    	String tableName = "tblSubCollections";
    	String[] setColumns = {"SubCollectionName"};
    	String[] whereColumns = {"FileID"};
    	String[] whereBooleans = null;
    	for(int x = 0; x < subCollectionFileIDs.length; x++) {
	    	String[] setValues = {subCollectionNames[x]};
	    	String[] whereValues = {subCollectionFileIDs[x]};
	    	command.update(tableName, setColumns, setValues, whereColumns, whereValues, whereBooleans);
    	}
    }
    
    public void setProgress(int i, int total) {
    	double di = (double)i;
    	double dt = (double)total;
    	progressBar.setProgress(di/dt);
    }
    
    public void showProgress() {
    	progressBar.setVisible(true);
    	populating.setVisible(true);
    }
    
    public void hideProgress() {
    	progressBar.setVisible(false);
    	progressBar.setProgress(0.0);
    	populating.setVisible(false);
    }

	@Override
	public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        
        mapOptions.center(new LatLong(46.01832757392108, -116.96044921875))
             .mapType(MapTypeIdEnum.ROADMAP)
             .overviewMapControl(false)
             .panControl(false)
             .rotateControl(false)
             .scaleControl(false)
             .streetViewControl(false)
             .zoomControl(false)
             .zoom(5);
                
        map = mapView.createMap(mapOptions);
        mainApp.setMap(map);
        map.addMouseEventHandler(UIEventType.click, new MouseEventHandler() {

			@Override
			public void handle(GMapMouseEvent mouseEvent) {
				mouseEvent.getLatLong();
				
			}
        	
        });
		map.addStateEventHandler(MapStateEventType.idle, new StateEventHandler() {
			@Override
			public void handle() {
				ImageExplorerController controller = getController();
				synchronized(controller) {
					mapLoaded = true;
					controller.notify();
				}
			}
		});
        
        //InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        //infoWindowOptions.content("<h2>Eagle's Pub</h2>"
        //                     	+ "Current Location: Cheney, WA<br>"
        //                     	+ "ETA: Unknown" );

        //InfoWindow cheneyInfoWindow = new InfoWindow(infoWindowOptions);
        //cheneyInfoWindow.open(map, cheneyMarker);
		
	}
	
	public ImageExplorerController getController() { return this; }
	
	public void mapUpdate(LatLong latLng) {
		for(CustomImage image : mainImageList) {
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			        MarkerOptions markerOptions = new MarkerOptions();
			        markerOptions.position(latLng);
			        Marker marker = new Marker(markerOptions);
			        map.addMarker( marker );
			    }
			});
		}
	}
	
	public void mapCenter(LatLong latLng) {
		map.setCenter(latLng);
	}

	public void comboBoxSelect() {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
				for(int i = 0; tagsColumn.getCellData(i) != null; i++) {
					tagsColumn.getCellData(i).getSelectionModel().select(0);
				}
		    }
		});
	}
    
    public void deleteFile() {
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	alert.setContentText("Are you sure?");
    	alert.showAndWait();
    	if(!alert.getResult().getButtonData().isCancelButton()) {
    		ConnectionDriver cd = mainApp.getConnectionDriver();
	    	DBConnect db = cd.getDB();
	    	DBCommands command = new DBCommands(db);
	    	
	    	ObservableList<Integer> intlist = imageTable.getSelectionModel().getSelectedIndices();
	    	ObservableList<CustomImage> itemlist = imageTable.getItems();
	    	for(Integer i : intlist) {
	    		CustomImage image = itemlist.get(i);
	    		String fileName = image.getFileName();
	    		String id = fileName.substring(fileName.indexOf("_")+1, fileName.indexOf("."));
	    		
	    		// Delete from database.
	    		String[] whereColumns = {"FileID"};
	    		String[] whereValues = {id};
	    		String[] whereBooleans = null;
	    		
	    		String tableName = "tblSubCollections";
	    		command.delete(tableName, whereColumns, whereValues, whereBooleans);
	    		
	    		tableName = "tblTags";
	    		command.delete(tableName, whereColumns, whereValues, whereBooleans);
	    		
	    		tableName = "tblFileData";
	    		command.delete(tableName, whereColumns, whereValues, whereBooleans);
	    		
	    		// Delete from collection directories (originals & thumbnails).
	    		cd.deleteFile(fileName);
	    	}
	    	mainApp.updateImageData(new String[]{"*"}, null, null, null);
    	}
    }
}
