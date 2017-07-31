---------------------------------------------------------------------------------------------------------------

## Image Explorer  

Eastern Washington University, Senior Project, 2017

---------------------------------------------------------------------------------------------------------------

### Team:
Jacob Hittinger (Lead)  
Adam Holzer  
Adam Scott

---------------------------------------------------------------------------------------------------------------

This software package is an image browsing solution, which implements intuitive tagging functionalities
so that its user can quickly and efficiently locate any specific image in their library.

Aiding the user in managing their library of images is the primary function of this software. Therefore,
convenience and efficiency are key. This is evident in the application itself being cross-platform, but more
importantly, in that the library of images and a database containing all of the tagging data live on a server
(provided by the user), which enables access to the collection from any computer.

---------------------------------------------------------------------------------------------------------------

### Types of tags include:

**Custom Tag**  
Any number of user-defined tags may be added to images.

**Collection**  
While custom tags can be used for specific details about each image, a collection can be seen as a more
general tag, intended to group a number of images together with a single name or keyword.

**Location**  
During the importation process, clicking on the map will associate them with that location. The map returns
a latitude and longitude, which we turn into a user-friendly String representation via the Google Maps API.

---------------------------------------------------------------------------------------------------------------

### Requirements:

Although this software is cross-platform, you will need a Linux machine or server with SSH capabilities and an
instance of MySQL. This will act as a server; it will host your database and collections of images/thumbnails.

---------------------------------------------------------------------------------------------------------------

### Configuration:

**SSH** : \ImageExplorer\src\image\explorer\model\SSHConnect.java
+ host = Host machine's IP address.
+ servUser = Username for logging into host machine.
+ servPass = Password for logging into host machine.

**Database** : \ImageExplorer\src\image\explorer\model\DBConnect.java
+ dbUser = MySQL database account username.
+ dbPass = MySQL database account password.

There are some other settings that you may be interested in adjusting, such as the directories in which
the images and thumbnails are stored on the host machine which is located in the 'SSHConnect.java' file, or
the name of the database in your MySQL instance, which is located in the 'DBConnect.java' file.

---------------------------------------------------------------------------------------------------------------

### Functionality:

**Import**
+ Select 1 or more images from the local machine
+ Collect optional geographic location, collection name, and 0 or more tags for import
+ Upload full size and thumbnail image via SFTP
+ Upload metadata to SQL DB via SSH

**Update**
+ Make edits in one or more of the table's text boxes to change the data values
+ Click on the "Apply" button to push the changes to the database

**Delete**
+ Select one or more images from the query pane and delete
+ Removes full size image thumbnails from FTP server and all associated metadata from database

**Preview**
+ Double click on a row in the table to load a full size preview of the image

**Export**
+ Select one or more images from the query pane to export
+ Select destination on local HDD
+ Download images selected in query pane to local HDD via SFTP

**Query**
+ Select category of data you'd like to query from (Tag or Collection Name)
+ Apply conditional query parameters and build SQL query
+ Retrieve images from SQL and FTP server based on user query

---------------------------------------------------------------------------------------------------------------

### Known Bugs:

+ Importing GIFs does not work.
+ Google Maps markers only work some of the time.
+ Google Maps window will stop functioning if dragged outside of bounding window limits.
+ Thread manager creates duplicate entries in query. Limited to a single thread at the moment.
+ Occasionally the query needs to be run twice to populate results.
+ Query by tag shows only the tag queried.

---------------------------------------------------------------------------------------------------------------