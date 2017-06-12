package image.explorer.model;

import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionDriver {

    private SSHConnect ssh;
    private DBConnect db;

    public ConnectionDriver() {
	this.ssh = new SSHConnect();
	this.db = new DBConnect();
    }

    public void closeConnections() {
	this.db.closeConnections();
	this.ssh.closeConnections();
    }

    public SSHConnect getSSH() {
	return this.ssh;
    }

    public DBConnect getDB() {
	return this.db;
    }
    
    public void deleteFile(String fileName) {
    	this.ssh.deleteFile(fileName);
    }
    
    public void uploadFile(InputStream is, String fileName, boolean isThumbnail) {
	this.ssh.uploadFile(is, fileName, isThumbnail);
    }

    public InputStream downloadFile(int fileNum, String ext, boolean isThumbnail) {
	 return this.ssh.downloadFile(fileNum, ext, isThumbnail);
    }

    public void downloadFile(OutputStream os, String fileName, boolean isThumbnail) {
	this.ssh.downloadFile(os, fileName, isThumbnail);
    }

    public void downloadFile(OutputStream os, int fileNum, boolean isThumbnail) {
	this.ssh.downloadFile(os, fileNum, isThumbnail);
    }

}
