Bus Times is a small app intended to display live Bus Time information, with support for displaying the results on a smartwatch.

It is currently focused on the UK (with initial support for London), and only the MetaWatch is supported.

Note: This project now relies on the following GitHub projects:
* AndroidUtils project, at https://github.com/logicalChimp/AndroidUtils for a number of common MetaWatch related interfaces, definitions, etc, and for the LocationTracker utility class that wraps up a lot of crufty GPS code
* NewQuickAction project, at https://github.com/lorensiuswlt/NewQuickAction (See http://www.londatiga.net/it/how-to-create-quickaction-dialog-in-android/ for more information.)

To do:
* Finish initial implementation (use GPS to locate closest preferred stop)
* Make it look pretty
* Add support for more smart watches (Sony Livewatch, Sony Smartwatch, Pebble, etc)
* Add more sources of bus times, and a map view showing which areas of the country are covered
* Spit/Polish

