package image.explorer.view;

import image.explorer.MainApp;
import image.explorer.importer.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.WindowEvent;

public class RootLayoutController {
	private MainApp mainApp;

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	protected void menu_close(ActionEvent event) {
		mainApp.getPrimaryStage()
				.fireEvent(new WindowEvent(mainApp.getPrimaryStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	@FXML
	protected void menu_import(ActionEvent event) {
		imageImport(mainApp);
	}

	@FXML
	protected void menu_export(ActionEvent event) {
		mainApp.getController().exportData();
	}

	public static void imageImport(MainApp mainApp) {
		Importer importer = new Importer(mainApp.getPrimaryStage(), mainApp.getConnectionDriver());
		importer.importImage();
		mainApp.updateImageData(new String[] { "*" }, null, null, null);
	}

	@FXML
	protected void menu_about(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("About Image Explorer");
		alert.setHeaderText("Image Explorer v1.0");
		alert.setContentText("JSch License:\n" + "JSch 0.0.* was released under the GNU LGPL license.\n"
				+ "Later, we have switched over to a BSD-style license.\n\n"
				+ "----------------------------------------------------------------------\n"
				+ "Copyright (c) 2002-2015 Atsuhiko Yamanaka, JCraft, Inc.\n" + "All rights reserved.\n\n"
				+ "Redistribution and use in source and binary forms, with or without "
				+ "modification, are permitted provided that the following conditions are met:\n\n"
				+ "1. Redistributions of source code must retain the above copyright notice, "
				+ "this list of conditions and the following disclaimer.\n\n"
				+ "2. Redistributions in binary form must reproduce the above copyright "
				+ "notice, this list of conditions and the following disclaimer in "
				+ "the documentation and/or other materials provided with the distribution.\n\n"
				+ "3. The names of the authors may not be used to endorse or promote products "
				+ "derived from this software without specific prior written permission.\n\n"
				+ "THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, "
				+ "INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND "
				+ "FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT, "
				+ "INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, "
				+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT "
				+ "LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, "
				+ "OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF "
				+ "LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING "
				+ "NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, "
				+ "EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.");
		alert.showAndWait();
	}
}
