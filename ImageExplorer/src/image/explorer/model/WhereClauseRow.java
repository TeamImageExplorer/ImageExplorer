package image.explorer.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WhereClauseRow {
    private final ObjectProperty<ComboBox<String>> headers;
    private final ObjectProperty<TextField> value;
    private final ObjectProperty<ComboBox<String>> operators;
    private final ObjectProperty<Button> remove;

    public WhereClauseRow() {
    	// Create and assign ComboBox to hold headers
    	ObservableList<String> headerOptions = 
    		    FXCollections.observableArrayList(
    		        "File Name",
    		        "Collection",
    		        "Location",
    		        "Tag"
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
    	Button button = new Button();
    	button.setText("X");
    	button.setTextFill(Color.RED);
    	button.setOnMouseClicked(new EventHandler() {
			@Override
			public void handle(Event event) {
				TableRow tableRow = (TableRow)button.getParent().getParent();
				TableView tableView = tableRow.getTableView();
				if(tableView.getItems().size() > 1)
					tableView.getItems().remove(tableRow.getItem());
			}
    	});
    	this.remove = new SimpleObjectProperty<Button>(button);
    }
    
    public String getHeader() { return headers.get().getSelectionModel().getSelectedItem().toString(); }
    public ObjectProperty<ComboBox<String>> headerProperty() { return headers; }
    
    public String getValue() { return value.get().getText(); }
    public void setValue(String str) { this.value.set(new TextField(str)); }
    public ObjectProperty<TextField> valueProperty() { return value; }

    public String getOperator() { 
    	if(operators.get() != null && operators.get().getSelectionModel() != null && operators.get().getSelectionModel().getSelectedItem() != null)
    		return operators.get().getSelectionModel().getSelectedItem().toString(); 
    	else
    		return null;
    }
    public ComboBox<String> getOperatorBox() { return operators.get(); }
    public void setOperatorBox(ComboBox<String> comboBox) { operators.set(comboBox); }
    public ObjectProperty<ComboBox<String>> operatorProperty() { return operators; }
    
    public Button getRemove() { return remove.get(); }
    public void setRemove(Button button) { this.remove.set(button); }
    public ObjectProperty<Button> removeProperty() { return remove; }
}