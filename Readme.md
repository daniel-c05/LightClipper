This is a sample project that eases the use of the Android Clipboard Framework. 

It has a background service that will keep an ArrayList<String> containing the last 20 clipboard items. 
It has a broadcastReceiver that will be awaken via Share Intents, and will allow the user to simply copy that data to the clipboard, making it easy to copy website URLs, tweet contents, facebook statuses, etc. 