## Purpose

This app is intended to be a convenient, user-friendly tool for viewing and downloading content published by the NASA Astronomy Picture of the Day (APOD) service. 

I've enjoyed the APOD content for several years, but I still find it relatively inconvenient to use with a general-purpose internet browser&mdash;especially when downloading content for use as wallpaper, etc. My aim in designing and building this app is not only to provide a simplified (but still effective) user interface to the NASA APOD service, but also to illustrate a number of features, components, and techniques that are (in my opinion) useful to new Android developers, as they learn to develop apps for that platform:

* Consuming web services with Retrofit &amp; OkHttp.
* Deserializing JSON data with Gson & the Retrofit Gson converter.
* Basic use of the Room ORM components.
* Basic use of the `BottomNavigationView` component.
* Basic use of the `RecyclerView` component, with adapters &amp; holders.
* Simple management of fragments in a navigation view-oriented app.
* Definition &amp; use of different types of menus: navigation, option, and context.
* Basic operations on internal &amp; external files.
* Explicit permission requests.

## Current state

The app is fully operational for testing/pre-release use: 

* Images and videos for the current and other selected dates can be viewed.
* History of APOD accesses is maintained in local database.
* Standard-resolution image content is cached in local storage for fast retrieval.
* High-resolution image content can be downloaded on demand.
* Local data records and cached standard-resolution images can be deleted on demand. 

Note that the Stetho inspection features are currently still enabled; for a production release, these should be disabled &amp; removed from the app. Also, building and running the app still requires that the user obtain a NASA web service API key. 

## Testing

### Operations

* Load APOD for current &amp; selected dates.
* Change orientation.
* Download APOD image.
* Delete APOD for current &amp; selected dates.
* Modify the most-recently-used (MRU) file cache size.

### Environments

#### Physical devices

##### Amazon Fire HD 8 7<sup>th</sup> Generation (Fire OS 5.3.6.4), Android 5.1.1 (API 22)

* No issues observed.

##### LG Q7+, Android 8.1 (API 27)

* When in portrait orientation, scaling of images to the display width (performed automatically by the `WebView` component) is unreliable: The first time any given image is loaded &amp; displayed (whether via using the date picker in the image view, or by selecting an item in the history view), it is displayed zoomed-in, using a width suitable to landscape, rather than fitting the width of the display; usually (but not always) subsequent displays of the same image are scaled correctly.
* No other issues observed.

#### Emulators

##### Pixel 2 XL (API 28)

* Orientation changes don't appear to be detected in emulator (not specific to this app).

##### Nexus 5X (API 24)

* Orientation changes detected unreliably (not specific to this app).
* Video doesn't play (not specific to this app).  

## 3<sup>rd</sup>-party resources

### Libraries

* [Google Gson](https://github.com/google/gson)

    Used for deserializing JSON content received from NASA APOD web service.
    
* [Retrofit 2](https://square.github.io/retrofit/) (incl. OkHttp &amp; Retrofit converter for Gson)

    Used to define &amp; execute requests to NASA APOD web service.  
    
* [Facebook Stetho](https://github.com/facebook/stetho) 

    Primarily used in development, to allow inspection of local display resources and database contents. 
    
* [Deep Dive Coding Date Utilities](https://github.com/deep-dive-coding-java/date-utilities)

    Provides a date-only datatype&mdash;i.e. a lightweight replacement for the `LocalDate` classes in Java 8 (not available in Android prior to API 26) and Joda-Time. This datatype supports the implementation of timezone independence in the app, in retrieving &amp; storing APOD content.    

* [Deep Dive Coding Android Utilities](https://github.com/deep-dive-coding-java/android-utilities) 

    This library provides the date picker dialog used in the app, as well as a non-UI component that simplifies definition &amp; use of some types of coordination between foreground &amp; background tasks. 

### External services

The only external service currently used in the app is (of course) the [NASA APOD web service](https://api.nasa.gov/api.html#apod).  

## Potential improvements

### Functional stretch goals

* Either embed the NASA API key in the app (subject to NASA licensing terms), or implement an automatic key request/install feature on installation. (If the latter, this might be integrated with Google Sign In, which was included for demonstration purposes in an earlier version of this app, but is not in the current version.)

* Provide a "share" feature, supporting sending APOD links (at least) via SMS, email, etc.

* Automatically retrieve APOD information (w/o caching media content) for the most recent 30 days.

* Modify deletion feature so that only cached media content&mdash;not title &amp; other metadata&mdash;is deleted.

* Add filter for limiting the range of dates displayed in the history view.

* Implement reactive/live connection between database and history view.

### Cosmetic improvements

* Modify history display to indicate the existence of cached media content.  

* User confirmation of delete operation.

## Design documentation

* ### [User stories](docs/user-stories.md)

* ### [Wireframes](docs/wireframes.md)

* ### [Data model](docs/data-model.md)

## Copyright &amp; license

Copyright 2019 Nicholas Bennett & Deep Dive Coding

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

> <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

* ### [Full (including 3<sup>rd</sup>-party) copyright &amp; license notices](docs/notice.md) 

## Instructions

* ### [Building the app](docs/build-instructions.md)

* ### [Using the app](docs/user-instructions.md)
