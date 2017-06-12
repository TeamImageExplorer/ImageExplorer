---------------------------------------------------------------------------------------------------------------

# Image Explorer  

Eastern Washington University, Senior Project, 2017

---------------------------------------------------------------------------------------------------------------

## Team:
Jacob Hittinger  
Adam Holzer  
Adam Scott

---------------------------------------------------------------------------------------------------------------

This software package is an image browsing solution, which implements intuitive tagging functionalities
so that its user can quickly and efficiently locate any specific image in their library.

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

Aiding the user in managing their library of images is the primary function of this software. Therefore,
convenience and efficiency are key. This is evident in the application itself being cross-platform, but more
importantly, in that the library of images and a database containing all of the tagging data live on a server
(provided by the user), which enables access to the collection from any computer.

---------------------------------------------------------------------------------------------------------------
