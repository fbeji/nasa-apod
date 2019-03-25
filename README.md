## Value

* Basic implementation: 20 points
* Extra credit implementation: 10 points
* Unit tests: None

## Basic task

The NASA APOD v2 app we've been building has most of its final intended functionality. However, there are some features that are available in one screen, and not the other. In particular, the "show info" functionality, available when viewing an image or video, is not available when viewing the list of APODs (i.e. the history view), but probably should be.

### Implementation

Add a "Show Info" option to the pop-up menu that appears on a long press on any item in the history view. When this option is selected, an alert dialog showing title, explanation, and URL(s) should be displayed, just as it is when the user presses the info icon in the action bar in the image view.

For more details on current classes &amp; methods, see [Javadoc](docs/api/). (Of course, you should be prepared to dig through the code itself, to refresh your memory on the relevant details.)

**Important:** To build your project successfully, the `build.gradle` script must be able to find your `nasa.properties` file in the expected location.

### Hints

* Remember that in this app, everything that is displayed in a navigation view or menu&mdash;whether in the bottom navigation view, the options menu in the action bar, or a pop-up menu&mdash;is defined in, and inflated from, a menu resource.

* Currently, the code that's executed when a pop-up menu item is selected is defined as a lambda, in the same method that inflates the pop-up menu itself.

### Solution 

(See [below](#solution-1).)

## Extra credit

To earn the full extra credit points, write your implementation in such a way that the code that invokes `DialogFragment.show` to displays the information alert dialog is not repeated in multiple places. Note that this may require moving some existing code from one controller class to another, and modifying the signature(s) of one or more methods.

### Solution

* [**`res/menu/item_content.xml`**](app/src/main/res/menu/item_context.xml)

    The menu resource that is inflated for the context menu in the history view is `item_content.xml`. A new `item` element has been added to this file.

* [**`res/values/strings.xml`**](app/src/main/res/values/strings.xml)

    The new menu item references a `string` resource, `@string/context_info`; this has been added to `strings.xml`.

* [**`edu/cnm/deepdive/nasaapod/controller/NavActivity.java`**](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/NavActivity.html)

    Since the info display must now be accessible from both `ImageFragment` and `HistoryFragment`, it makes sense not to duplicate the code that displays `InfoFragment`; instead we should move it into `NavActivity`, where it can be accessed by `ImageFragment` and `HistoryFragment`. So a [`showFullInfo(Apod)`](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/NavActivity.html#line.110) method has been added to `NavActivity.java`.

* [**`edu/cnm/deepdive/nasaapod/controller/ImageFragment.java`**](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/ImageFragment.html)

    The changes needed in `ImageFragment` are the removal of the `showFullInfo` method (since that's now a method in `NavActivity`), and a change to the [`onOptionsItemSelected`](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/ImageFragment.html#line.81) method, to invoke the `showFullInfo` method in `NavActivity`, rather than the removed method.

* [**`edu/cnm/deepdive/nasaapod/controller/HistoryFragment.java`**](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/HistoryFragment.html)

    The `HistoryFragment` class has a [`createContextMenu`](docs/api/src-html/edu/cnm/deepdive/nasaapod/controller/HistoryFragment.html#line.106) method (invoked from the `onCreateContextMenu` method of the `HistoryAdapter.Holder` class), that not only inflates the `item_context.xml` menu resource, but also sets click listeners on the menu items. To handle the new menu item, code has been added to this method.
