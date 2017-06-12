/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.explorer.importer;

import main.java.com.lynden.gmapsfx.GoogleMapView;
import main.java.com.lynden.gmapsfx.MapComponentInitializedListener;
import main.java.com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import main.java.com.lynden.gmapsfx.javascript.event.UIEventType;
import main.java.com.lynden.gmapsfx.javascript.object.GoogleMap;
import main.java.com.lynden.gmapsfx.javascript.object.LatLong;
import main.java.com.lynden.gmapsfx.javascript.object.MapOptions;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author short
 */
public class DialogController extends Dialog<ButtonType> implements Initializable, MapComponentInitializedListener {

    @FXML
    private TextField inputField, collectionField;
    @FXML
    private Text count, lat, lon;
    @FXML
    private VBox mapBox;
    @FXML
    private SplitPane mainPane;
    @FXML
    CheckBox named;
    private int counter;
    private final ButtonType finish;
    private final ButtonType next;
    //ArrayList<String> tags;
    private GoogleMapView mapView;
    private GoogleMap map;
    private DialogResult rv;
    private boolean selectedLoc;

    public DialogController() throws IOException {
	super();
	mapView = new GoogleMapView();
	mapView.addMapInializedListener(this);
	rv = new DialogResult();
	selectedLoc = false;
	FXMLLoader myLoader = new FXMLLoader(getClass().getResource("CustomInputDialog.fxml"));
	myLoader.setController(this);
	Parent root = (Parent) myLoader.load();
	//tags = new ArrayList();
	counter = 0;
	getDialogPane().setContent(root);
	ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	finish = new ButtonType("Finished", ButtonData.OK_DONE);
	next = new ButtonType("Apply Tag");
	getDialogPane().getButtonTypes().addAll(cancel, next, finish);
	getDialogPane().lookupButton(next).addEventFilter(ActionEvent.ACTION, event -> onApplyTag(event));
	getDialogPane().lookupButton(finish).addEventFilter(ActionEvent.ACTION, event -> onFinish(event));
	//getDialogPane().lookup("collectionField").addEventFilter(ActionEvent.ACTION, event -> isNamed());

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
	mapBox.getChildren().add(mapView);
	collectionField.setDisable(true);
	named.setOnAction(event -> isNamed(event));
    }

    /*
    Checks if arraylist is empty. if so, adds default NONE tag so that imported
    images can still be looked up to have tags added at a later point. Closes
    dialog
     */
    private void onFinish(ActionEvent event) {
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	alert.setTitle("Warning");
	if (selectedLoc) {
	    rv.setLat(Double.parseDouble(lat.getText()));
	    rv.setLon(Double.parseDouble(lon.getText()));
	}
	if (named.isSelected()) {
	    rv.setCollectionName(collectionField.getText());
	}
	if (named.isSelected() && collectionField.getText().isEmpty()) {
	    alert.setHeaderText("Add a name or uncheck box");
	    event.consume();
	    alert.showAndWait();
	} else if (named.isSelected()) {
	    rv.setCollectionName(collectionField.getText());
	}
	if (rv.getTags().isEmpty()) {
	    alert.setHeaderText("Default tag NONE will be used");
	    rv.getTags().add("NONE");
	    alert.showAndWait();
	} else {
	    alert.setTitle("Confirmation");
	    alert.setHeaderText(String.format("%d %s added", rv.getTags().size(), rv.getTags().size() == 1 ? "tag" : "tags"));
	    alert.showAndWait();
	}

    }

    /*
    Check input, add to arraylist if valid, clear selection, increment count, 
    and request focus for next tag input. Enforces lowercase input. Leaves 
    dialog open
     */
    private void onApplyTag(ActionEvent event) {
	Alert alert = new Alert(Alert.AlertType.INFORMATION);
	alert.setTitle("Error");
	String text = inputField.getText();
	if (text == null || text.isEmpty()) {
	    alert.setHeaderText("Please enter a tag");
	    alert.showAndWait();
	    event.consume();
	} else {
	    rv.getTags().add(text.toLowerCase());
	    inputField.clear();
	    inputField.requestFocus();
	    counter++;
	    count.setText(Integer.toString(counter));
	    event.consume();
	}
    }

//    public ArrayList<String> getTags() {
//	return tags;
//    }
    public DialogResult getResults() {
	return rv;
    }

    /*
    Create and configure MapOptions
    Use configured MapOptions to build map
    //TODO: configurable map
     */
    @Override
    public void mapInitialized() {
	//Set the initial properties of the map.
	MapOptions mapOptions = new MapOptions();

	mapOptions.center(new LatLong(47.6097, -122.3331))
		.overviewMapControl(true)
		.panControl(false)
		.rotateControl(false)
		.scaleControl(false)
		.streetViewControl(false)
		.zoomControl(true)
		.zoom(5);

	map = mapView.createMap(mapOptions);

	//On-click event handler to get lat/long
	map.addMouseEventHandler(UIEventType.click, (GMapMouseEvent event) -> onMapClicked(event));

    }

    private void onMapClicked(GMapMouseEvent event) {
	LatLong latLong = event.getLatLong();
	selectedLoc = true;
	map.panTo(latLong);
	lat.setText(Double.toString(latLong.getLatitude()));
	lon.setText(Double.toString(latLong.getLongitude()));
    }

    private void isNamed(ActionEvent event) {

	if (named.isSelected()) {
	    //named.setSelected(false);
	    collectionField.setDisable(false);
	} else {
	    //named.setSelected(true);
	    collectionField.setDisable(true);
	}
	//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
