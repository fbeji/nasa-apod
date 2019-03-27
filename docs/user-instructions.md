## User instructions

1. On installation, the app name appears as "NASA APOD". The launcher icon will use the rounded style on some devices, and the rectangular (or rounded rectangular) on others.

    ![Launcher icon](img/launcher-icon.png)

2. When the app is first launched on a device running Android 8.0 (API 26) or higher, permission for access to photos, media, and other files is requested. (Prior to API 26, this permission was only required to be granted on installation, not when the app was first launched.) This permission is required for the app to download images that will be available to Gallery and other Android apps.

    ![Permission request](img/permission-request.png)
    
3. After granting or denying the permission mentioned above, the APOD media for the current day is displayed.

    ![Media display](img/media-display.png)
    
4. In the image view, a number of options are available in the action bar, and in the overflow menu (under the 3 vertically aligned dots):

    1. **Download** (shown as a downward arrow)
    
        When the APOD media for the selected day is an image, and if the user has granted the permission described above, the download option will appear. Clicking on it downloads the highest resolution version available of the currently displayed image; the file will be saved in the `Pictures` folder.
        
    2. **Show info** (block letter "i" in a circle)
    
        When this option is clicked, an information dialog is displayed, containing the APOD title, descriptive text, and links to the standard-resolution media content, and (if available) to the high-resolution media content.
        
    3. **Pick date** (desk calendar icon)
    
        This option presents a date picker dialog, allowing you to select a date for which the APOD will be retrieved. After selecting a date and clicking **OK**, the app will make a request to the NASA APOD web service. If the request succeeds, the media content will be displayed; otherwise, an error message is displayed in a "toast" at the bottom of the screen. 
        
    4. **Delete** (in the overflow menu)
    
        Selecting this deletes the currently displayed APOD entry from the local database, and any locally cached media for that APOD (not including images downloaded to the `Pictures` folder). Then, the APOD for the current day is displayed. (One possibly unexpected consequence of this is that if the current day's APOD is deleted in this fashion, it will be re-requested and displayed immediately.)
        
    5. **Settings** (in the overflow menu)
    
    