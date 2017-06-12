/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image.explorer.importer;

//import com.jcraft.jsch.Channel;
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.JSchException;
//import com.jcraft.jsch.Session;
//import com.jcraft.jsch.SftpException;
import main.java.com.lynden.gmapsfx.GoogleMapView;
import main.java.com.lynden.gmapsfx.javascript.object.GoogleMap;
import java.io.File;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
//import db.*;
import image.explorer.model.ConnectionDriver;
import image.explorer.model.DBCommands;
import image.explorer.model.HttpsClient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
//import sftp.SFTPConnector;

/**
 *
 * @author short
 */
public class Importer {

    private Stage mainStage;
    GoogleMapView mapView;
    GoogleMap map;
    ConnectionDriver cd;
    HttpsClient https;
    DBCommands db;

    public Importer(Stage s, ConnectionDriver cd) {
	mainStage = s;
	this.cd = cd;
	https = new HttpsClient();
	db = new DBCommands(cd.getDB());
    }

    public void importImage() {
	double start, stop;
	String path = System.getProperty("user.home") + "\\thumbnails\\";
	File imageDir = new File(path);
	//makes local directory
	if (!imageDir.exists()) {
	    imageDir.mkdir();
	}
	//System.out.printf("Working from %s\n", path);

	List<File> files = selectFiles();
	try {
	    if (files == null || files.isEmpty()) {
		throw new IllegalStateException("Files should be selected to process");
	    }
	    //start = System.currentTimeMillis();
	    processFiles(files, path);
	    //stop = System.currentTimeMillis();
	    //System.out.printf("TOTAL RUN TIME: %f ms\n", stop-start);
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (IllegalStateException e) {
	    System.out.println("File selection cancelled!");
	    e.printStackTrace();
	} catch (NullPointerException e) {
	    System.out.println("Tag collection cancelled!");
	    e.printStackTrace();
	}

    }

    //User selects images, returns list of files
    //TODO: expand to handle directories too
    private List<File> selectFiles() {
	FileChooser fileChooser = new FileChooser();
	fileChooser.setTitle("Import Image");
	fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Pictures\\")); //set default to pictures. need to test on linux
	fileChooser.getExtensionFilters().addAll(
		new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
	List<File> images = fileChooser.showOpenMultipleDialog(mainStage);

	return images;
    }

    private void processFiles(List<File> images, String path) throws IOException, NullPointerException {
//	SFTPConnector sftp = new SFTPConnector();
//	DBCommands db = new DBCommands();
//	SSHConnect sftp = cd.getSSH();
	
    //DBCommands db = new DBCommands(cd.getDB());

	double start, stop;
	String location = "";

	String[] fileDataColumns = new String[]{"FileType", "ResWidth", "ResHeight", "Latitude", "Longitude"};
	String[] tagColumns = new String[]{"FileID", "Tag"};
	String[] subCollectionColumns = new String[]{"FileID", "SubCollectionName", "Location"};
	DialogResult results = collectTags();
	//System.out.println("COLLECTION NAME: " + results.getCollectionName());
	ArrayList<String> tags = results.getTags();
	if (tags == null) {
	    throw new NullPointerException("Import cancelled!");
	}
	
	if (Double.compare(results.getLat(), 9999) != 0) {
	    Object json;

	    json = https.get(Double.toString(results.getLat()), Double.toString(results.getLon()));

	    location = HttpsClient.getAddressComponent(json, false, "locality") + ", "
		    + HttpsClient.getAddressComponent(json, true, "administrative_area_level_1");
	    //path = System.getProperty("user.dir") + "\\thumbnails\\";
	}

	Image temp;
	int i = 0;
	if (images != null) {
	    start = System.currentTimeMillis();
	    for (File f : images) {
		String[][] fileDataValues = new String[1][5];
		String[][] tagValues = new String[1][2];
		String[][] subCollectionValues = new String[1][3];
		String extension = getExtension(f.getAbsolutePath());
		int nextID = db.getNextPID();

		//Resize original to 720p and resize thumbnail to 10% of that
		InputStream fileIS = Files.newInputStream(f.toPath());
		//make a temp file to get the images original resolution
		temp = new Image(fileIS);
		Image original = reduceSize(f.toPath(), temp.getWidth(), temp.getHeight(), 1280);
		Image thumbnail = reduceSize(f.toPath(), temp.getWidth(), temp.getHeight(), 120);

		//Debug Statements
		//System.out.printf("TEMP -- WIDTH: %f -- HEIGHT: %f\n", temp.getWidth(), temp.getHeight());
		//System.out.printf("ORIGINAL -- WIDTH: %f -- HEIGHT: %f\n", original.getWidth(), original.getHeight());
		//System.out.printf("THUMBNAIL -- WIDTH: %f -- HEIGHT: %f\n", thumbnail.getWidth(), thumbnail.getHeight());
		//Write thumbnail to HDD
		//TODO: bug, cannot write GIFS back to HDD, should probably use output stream and write bytes
		BufferedImage bImage = SwingFXUtils.fromFXImage(thumbnail, null);
		String fName = String.format("imagefile_%d.%s", nextID, extension);
		ImageIO.write(bImage, extension, new File(path + fName));

		//Setup values
		fileDataValues[0][0] = "." + extension;
		fileDataValues[0][1] = Double.toString(original.getWidth());
		fileDataValues[0][2] = Double.toString(original.getHeight());
		fileDataValues[0][3] = Double.toString(results.getLat());
		fileDataValues[0][4] = Double.toString(results.getLon());
		tagValues[0][0] = Integer.toString(nextID);
		subCollectionValues[0][0] = Integer.toString(nextID);
		subCollectionValues[0][1] = results.getCollectionName();
		subCollectionValues[0][2] = location;

		//Update DB
		//Insert main file info into tblFileData
		db.insert("tblFileData", fileDataColumns, fileDataValues);
		
		//Insert tblSubCollection data (if user selected it)
		//if(!results.getCollectionName().equals("NULL") && results.getLat() != 9999)
		//{
		    db.insert("tblSubCollections", subCollectionColumns, subCollectionValues);
		//}

		//Insert tags into tblTags
		System.out.println("---TAGS---");
		System.out.println("Inserting tags for image: " + f.getName());
		for (String s : tags) {
		    System.out.println(s);
		    tagValues[0][1] = s;
		    db.insert("tblTags", tagColumns, tagValues);

		}

		//Upload original to server
		bImage = SwingFXUtils.fromFXImage(original, null);
		InputStream originalInputStream = getInputStream(bImage, extension);
		cd.uploadFile(originalInputStream, fName, false);

		//Upload thumbnail to server
		bImage = SwingFXUtils.fromFXImage(thumbnail, null);
		InputStream thumbnailInputStream = getInputStream(bImage, extension);
		cd.uploadFile(thumbnailInputStream, fName, true);
	    }
	    stop = System.currentTimeMillis();
	    System.out.printf("Total Time: %f\n", stop - start);
	} else {
	    //System.out.println("images - NULL");

//	    sftp.close();
	    throw new NullPointerException("Images returned null when expecting results");
	}
//	sftp.close();
    }

    //pass full path, return everything after last .
    //TODO: make regex pattern if it seems necessary
    private String getExtension(String path) {
	String extension = "";

	int i = path.lastIndexOf('.');
	if (i > 0) {
	    extension = path.substring(i + 1);
	}
	return extension;
    }

    //resizes picture to desiredSize iff desiredSize < originalSize
    private Image reduceSize(Path p, double width, double height, double desiredSize) throws IOException {
	InputStream original = Files.newInputStream(p);

	if (width >= height && desiredSize < width) {
	    return new Image(original, desiredSize, 0, true, false);
	} else if (height >= width && desiredSize < height) {
	    return new Image(original, 0, desiredSize, true, false);
	} else {
	    return new Image(original, width, height, false, false); //if original picture is already small enough
	}
    }

    private static InputStream getInputStream(BufferedImage img, String extension) throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write(img, extension, baos);
	InputStream is = new ByteArrayInputStream(baos.toByteArray());
	return is;
    }

    private DialogResult collectTags() {
	try {
	    DialogController controller = new DialogController();

	    Optional<ButtonType> result = controller.showAndWait();
	    if (result.isPresent()) {
		if (result.get().getButtonData().equals(ButtonBar.ButtonData.OK_DONE)) {
		    return controller.getResults();
		}
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
	return null; //should never happen but check for anyways
    }

}