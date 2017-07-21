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

### Requirements

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