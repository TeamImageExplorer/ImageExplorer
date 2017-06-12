package image.explorer.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import image.explorer.MainApp;
import image.explorer.model.CustomImage;
import image.explorer.model.WhereClauseRow;

public class ImageExplorerController {
	@FXML
    private TableView<CustomImage> imageTable;
    @FXML
    private TableColumn<CustomImage, ImageView> imageColumn;
    @FXML
    private TableColumn<CustomImage, String> fileNameColumn;
    @FXML
    private TableColumn<CustomImage, String> locationColumn;
    @FXML
    private TableColumn<CustomImage, String> timeStampColumn;
	@FXML
    private TableView<WhereClauseRow> queryTable;
    @FXML
    private TableColumn<WhereClauseRow, ComboBox<String>> headerColumn;
    @FXML
    private TableColumn<WhereClauseRow, TextField> valueColumn;
    @FXML
    private TableColumn<WhereClauseRow, ComboBox<String>> operatorColumn;
    @FXML
    private Label currentState;
    @FXML
    private Button scrollQuery;
    @FXML
    private Button scrollView;
    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane queryPane;
    @FXML
    private AnchorPane imagePane;
    @FXML
    private AnchorPane tablePane;
    @FXML
    private AnchorPane detailPane;
    @FXML
    private ImageView imageView;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label timeStampLabel;
    
    public TableColumn<CustomImage, ImageView> getImageColumn() { return imageColumn; }

    // Reference to the main application.
    private MainApp mainApp;

    public ImageExplorerController() { }

    @FXML
    private void initialize() {
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
                if (event.getClickCount() == 2 && (! row.isEmpty()) )
                	imageChanged(row);
            });
            return row ;
        });
        // Set the cell values from data
        imageColumn.setCellValueFactory(cellData -> cellData.getValue().imageProperty());
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        timeStampColumn.setCellValueFactory(cellData -> cellData.getValue().timeStampProperty());
        headerColumn.setCellValueFactory(cellData -> cellData.getValue().headerProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
        operatorColumn.setCellValueFactory(cellData -> cellData.getValue().operatorProperty());
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        imageTable.setItems(mainApp.getImageData());
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
    	CustomImage img = row.getItem();
    	imageView.setImage(new Image("file:../../images/" + img.getFileName()));
    	fileNameLabel.setText(img.getFileName());
    	locationLabel.setText(img.getLocation());
    	timeStampLabel.setText(img.getTimeStamp());
    }
    
    @FXML protected void click_scrollQuery(ActionEvent event) {
    	splitPane.setDividerPosition(0, 0.31);
    	splitPane.setDividerPosition(1, 1);
    	scrollQuery.setDisable(true);
    	scrollView.setDisable(false);
    	currentState.setText("Query Mode");
    }
    
    @FXML protected void click_scrollView(ActionEvent event) {
    	splitPane.setDividerPosition(0, 0);
    	splitPane.setDividerPosition(1, 0.5);
    	scrollQuery.setDisable(false);
    	scrollView.setDisable(true);
    	currentState.setText("View Mode");
    	
    }
}
