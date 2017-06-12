package image.explorer.model;

public class Tester {
//
//	public static void main(String[] args) {
//		
//		//insertTest();
//		//updateTest();
//		//deleteTest();
//		//selectTest();
//		//countTest();
//		//bashBackupTest();
//		//insertTest();
//		selectTest();
//		//bashRestoreTest();
//		
//	}
//	/*
//	private static void bashBackupTest() {
//		DBCommands command = new DBCommands();
//		String path = "./";
//		int res = command.bashBackup(path);
//		System.out.println("bashBackupTest() = " + res);
//	}
//	
//	private static void bashRestoreTest() {
//		DBCommands command = new DBCommands();
//		String path = "./";
//		int res = command.bashRestore(path);
//		System.out.println("bashRestoreTest() = " + res);
//	}
//	*/
//	private static void insertTest() {
//		String tableName = "tblFileData";
//		String[] columns = {"FileType", "ResWidth", "ResHeight", "Latitude", "Longitude"};
//		String[][] values = {{".ico", "123", "123", "1111.111111", "9876.543201"},
//							 {".mpeg", "123", "123", "3333.333333", "4444.444444"}};
//		
//		DBCommands command = new DBCommands();
//		
//		command.insert(tableName, columns, values);
//	}
//	
//	public static void updateTest() {
//		DBCommands db = new DBCommands();
//		
//		String tableName = "tblFileData";
//		String[] setColumns = {"FileType", "ResHeight"};
//		String[] setValues = {".bm", "0"};
//		String[] whereColumns = {"ResWidth", "ResWidth"};
//		String[] whereValues = {"987", "321"};
//		String[] whereBooleans = {"OR"};
//		
//		db.update(tableName, setColumns, setValues, whereColumns, whereValues, whereBooleans);
//	}
//	
//	public static void deleteTest() {
//		DBCommands db = new DBCommands();
//		
//		String tableName = "tblFileData";
//		String[] whereColumns = {"ResWidth", "FileType"};
//		String[] whereValues = {"444", ".bm"};
//		String[] whereBooleans = {"OR"};
//		
//		db.delete(tableName, whereColumns, whereValues, whereBooleans);
//	}
//	
//	private static void selectTest() {
//		DBCommands command = new DBCommands();
//		
//		String tableName = "tblFileData LEFT JOIN tblSubCollections on tblFileData.FileID = tblSubCollections.FileID LEFT JOIN tblTags on tblFileData.FileID = tblTags.FileID ORDER BY tblFileData.FileID;";
//		String[] selectColumns = {"*"};
//		String[] whereColumns = null; //{"Latitude"};
//		String[] whereValues = null; //{"1111.111111"};
//		String[] whereBooleans = null;
//		
//		String[][] res = command.select(tableName, selectColumns, whereColumns, whereValues, whereBooleans);
//		
//		System.out.println("");
//		for(int i = 0; i < res.length; i++) {
//			for(int j = 0; j < res[i].length; j++) {
//				System.out.println(res[i][j]);
//			}
//			System.out.println("");
//		}
//	}
//	
//	private static void countTest() {
//		DBCommands db = new DBCommands();
//		
//		String tableName = "tblFileData";
//		String[] whereColumns = {"ResWidth", "ResWidth"};
//		String[] whereValues = {"765", "444"};
//		String[] whereBooleans = {"OR"};
//		
//		db.count(tableName, whereColumns, whereValues, whereBooleans);
//	}

}
