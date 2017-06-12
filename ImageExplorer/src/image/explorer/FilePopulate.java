package image.explorer;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

import image.explorer.model.CustomImage;
import image.explorer.model.HttpsClient;
import image.explorer.view.ImageExplorerController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class FilePopulate implements Runnable {
	private MainApp mainApp;
	private int numThreads, threadNum, total;
	private ArrayList<ObservableList<String>> imageTagList;
	private String[][] res;
	private DateTimeFormatter formatter;
	private HttpsClient https;
	private ObservableList<CustomImage> imageData;
	private ProgressCount count;
	private ImageExplorerController controller;
	
	public FilePopulate(MainApp mainApp, int numThreads, int threadNum, int total, ProgressCount count, ArrayList<ObservableList<String>> imageTagList, String[][] res, DateTimeFormatter formatter, ObservableList<CustomImage> imageData, ImageExplorerController controller, HttpsClient https) {
		this.mainApp = mainApp;
		this.numThreads = numThreads;
		this.threadNum = threadNum;
		this.total = total;
		this.count = count;
		this.imageTagList = imageTagList;
		this.res = res;
		this.formatter = formatter;
		this.imageData = imageData;
		this.controller = controller;
		this.https = https;
	}

	@Override
	public void run() {
		String date = null;
		LocalDateTime localDateTime = null;
		String fileName = null, route = null, location = null;
		Image img = null;
		CustomImage custImg = null;
		//for(int j = 0; j < 3000 && !Thread.interrupted(); j++) {
		for(int i = 0; i < res.length && !Thread.interrupted(); i++) {
			if(i%numThreads == threadNum) {
				ObservableList<String> fileTagList;
				synchronized(imageTagList) {
					fileTagList = imageTagList.get(Integer.parseInt(res[i][0])-1);
				}
				if(fileTagList == null) {
					date = res[i][2].substring(0, 19);
					localDateTime = LocalDateTime.parse(date, formatter);
					fileName = res[i][0] + res[i][1];
					route = res[i][9];
					location = res[i][10];
					InputStream in;
					synchronized(mainApp) {
						in = mainApp.getConnectionDriver().downloadFile(Integer.parseInt(res[i][0]), res[i][1], true);
						img = new Image(in, 75, 0, true, false);
						try { in.close(); } catch(Exception e) { System.out.println("Could not close file input stream."); }
					}
					synchronized(imageTagList) {
						imageTagList.set(Integer.parseInt(res[i][0])-1, FXCollections.observableArrayList());
						if(res[i][13] != null)
							imageTagList.get(Integer.parseInt(res[i][0])-1).add(res[i][13]);
						custImg = new CustomImage(img, fileName, route, location, res[i][5], res[i][6], localDateTime, imageTagList.get(Integer.parseInt(res[i][0])-1));
					}
					synchronized(imageData) {
						imageData.add(custImg);
					}
				}
				else {
					synchronized(imageTagList) {
						fileTagList.add(res[i][13]);
						fileTagList.sort(new Comparator<String>() {

							@Override
							public int compare(String arg0, String arg1) {
								return arg0.compareTo(arg1);
							}
							
						});
					}
				}
				int c = count.increment();
				//if(c%(total/200) == 0)
				controller.setProgress(c, total);
			}
		}
		//}
	}
}
