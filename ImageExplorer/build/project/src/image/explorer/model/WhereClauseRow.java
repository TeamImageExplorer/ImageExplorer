package image.explorer.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class WhereClauseRow {
    private final ObjectProperty<ComboBox<String>> headers;
    private final ObjectProperty<TextField> value;
    private final ObjectProperty<ComboBox<String>> operators;

    public WhereClauseRow() {
    	// Create and assign ComboBox to hold headers
    	ObservableList<String> headerOptions = 
    		    FXCollections.observableArrayList(
    		        "File Name",
    		        "Location",
    		        "Time Stamp"
    		    );
    	ComboBox<String> headerBox = new ComboBox<String>(headerOptions);
    	this.headers = new SimpleObjectProperty<ComboBox<String>>(headerBox);
    	// Create and assign TextBox to hold value
    	TextField valueArea = new TextField();
    	this.value = new SimpleObjectProperty<TextField>(valueArea);
    	// Create and assign ComboBox to hold operators
    	ObservableList<String> operatorOptions = 
    		    FXCollections.observableArrayList(
    		        "OR",
    		        "AND"
    		    );
    	ComboBox<String> operatorBox = new ComboBox<String>(operatorOptions);
    	this.operators = new SimpleObjectProperty<ComboBox<String>>(operatorBox);
    }
    
    public String getHeader() { return headers.get().getSelectionModel().getSelectedItem().toString(); }
    public ObjectProperty<ComboBox<String>> headerProperty() { return headers; }
    
    public String getValue() { return value.get().getText(); }
    public void setValue(String str) { this.value.set(new TextField(str)); }
    public ObjectProperty<TextField> valueProperty() { return value; }

    public String getOperator() { return operators.get().getSelectionModel().getSelectedItem().toString(); }
    public ComboBox<String> getOperatorBox() { return operators.get(); }
    public void setOperatorBox(ComboBox<String> comboBox) { operators.set(comboBox); }
    public ObjectProperty<ComboBox<String>> operatorProperty() { return operators; }
}