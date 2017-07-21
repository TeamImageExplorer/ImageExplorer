package image.explorer.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.OutputStream;

public class SSHConnect {

	// JSch version: jsch-0.1.54.jar
	private JSch jsch; // SSH object.

	// SSH credentials.
	private String servUser;
	private String servPass;

	private String host; // Server IP.
	private int port; // SSH port.

	private Session session = null; // SSH connection.

	private Channel channel;
	private ChannelSftp sftpChannel;

	private String remoteHost; // Server: IP to forward.
	private int remotePort; // Server: Port to forward.
	private int localPort; // Client: Port which talks to server.

	private boolean connected = false;
	
	private String origDir;
	private String thumbDir;

	protected SSHConnect() {
		initialize();
	}

	// Initialize variables, establish SSH connection, and forward MySQL port to
	// the client's port.
	private void initialize() {
		try {

			this.jsch = new JSch();

			this.servUser = "user"; // Host account username.
			this.servPass = "pass"; // Host account password.

			this.host = "ip.address"; // Host IP address for SSH.
			this.port = 22;

			// Get SSH session.
			this.session = this.jsch.getSession(this.servUser, this.host, this.port);
			this.session.setPassword(this.servPass);

			// Never automatically add new host keys to the host file.
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			this.session.setConfig(config);

			// Connect to remote server.
			this.session.connect();
			this.connected = true;

			this.channel = this.session.openChannel("sftp");
			this.channel.connect();
			this.sftpChannel = (ChannelSftp) this.channel;

			this.remoteHost = "localhost";
			this.remotePort = 3306;
			this.localPort = 3307;

			// Forward the MySQL port (3306) to the client's port (3307).
			this.session.setPortForwardingL(this.localPort, this.remoteHost, this.remotePort);
			
			this.origDir = "/home/" + this.servUser + "/ImageExplorer/originals";
			this.thumbDir = "/home/" + this.servUser + "/ImageExplorer/thumbnails";

		} catch (JSchException e) {
			if (!this.connected) {
				System.out.println("\nERROR: Can't establish SSH connection.");
			} else {
				System.out.println("\nERROR: Can't forward port.");
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Image Explorer");
				alert.setHeaderText("Error");
				alert.setContentText("There is already an instance of Image Explorer running and communicating with your server. Please close said instance and try again.");
				alert.showAndWait();
				System.exit(1);
			}
		}
	}

	// Close connection to database.
	protected void closeConnections() {
		System.out.println("\nClosing SSH connection...");
		try {
			this.session.delPortForwardingR(this.remotePort);
			this.session.delPortForwardingL(this.localPort);
		} catch (JSchException e) {
			System.out.println("\nERROR: Closing ports over SSH failed.");
		}

		this.sftpChannel.disconnect();
		this.channel.disconnect();

		// if (this.session != null && this.session.isConnected())
		this.session.disconnect();

		System.out.println("...SSH connection closed");
	}

	protected void uploadFile(InputStream is, String fileName, boolean isThumbnail) {
		try {
			if (isThumbnail) {
				sftpChannel.cd(this.thumbDir);
			} else {
				sftpChannel.cd(this.origDir);
			}

			sftpChannel.put(is, fileName);
			is.close();
			// sftpChannel.cd("../..");
		} catch (SftpException | IOException e) {
			System.out.println("Exception caught in sftp upload\n");
			e.printStackTrace();
		}
	}

	protected InputStream downloadFile(int fileNum, String ext, boolean isThumbnail) {
		try {
			String fileName = "imagefile_" + fileNum + ext;
			System.out.println(fileName);
			System.out.println(sftpChannel.pwd());
			if (isThumbnail) {
				sftpChannel.cd(this.thumbDir);
			} else {
				sftpChannel.cd(this.origDir);
			}
			System.out.println(sftpChannel.pwd());
			return sftpChannel.get(fileName);
			// sftpChannel.cd("../..");
		} catch (SftpException e) {
			System.out.println("Exception caught in sftp download\n");
			e.printStackTrace();
			return null;
		}
	}

	protected void downloadFile(OutputStream os, int fileNum, boolean isThumbnail) {
		try {
			String fileName = "imagefile_" + fileNum;
			if (isThumbnail) {
				sftpChannel.cd(this.thumbDir);
			} else {
				sftpChannel.cd(this.origDir);
			}
			sftpChannel.get(fileName, os);
			os.close();
			// sftpChannel.cd("../..");
		} catch (SftpException | IOException e) {
			System.out.println("Exception caught in sftp download\n");
			e.printStackTrace();
		}
	}

	protected void downloadFile(OutputStream os, String fileName, boolean isThumbnail) {
		try {
			if (isThumbnail) {
				sftpChannel.cd(this.thumbDir);
			} else {
				sftpChannel.cd(this.origDir);
			}

			sftpChannel.get(fileName, os);
			os.close();
			// sftpChannel.cd("../..");
		} catch (SftpException | IOException e) {
			System.out.println("Exception caught in sftp download\n");
			e.printStackTrace();
		}

	}

	protected void deleteFile(String fileName) {
		try {
			// Delete from thumbnails.
			sftpChannel.cd(this.thumbDir);
			sftpChannel.rm(fileName);

			// Delete from originals.
			sftpChannel.cd(this.origDir);
			sftpChannel.rm(fileName);

		} catch (SftpException e) {
			System.out.println("Exception caught in sftp delete\n");
			e.printStackTrace();
		}
	}
}
