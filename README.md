# AppletApp - A simple applet to see time from different timezones  

## How to run  
1. Download or clone this repository (in /home)  
```
cd ~
git clone https://github.com/SayantanRC/AppletApp.git
```
2. Check if Java is installed  
```
java -version
```
3. If not installed, install using:
```
sudo apt update && sudo apt install default-jdk
```
4. Compile and run the applet
```
cd ~/AppletApp
javac AppletApp.java
appletviewer AppletApp.java
```

## To add timezones
1. Close the applet if running.
2. Open "countries.txt"
```
gedit countries.txt
```
3. Add your timezone. There must be a single space between name and GMT deviation. Here is an example:
> Afghanistan Standard Time +04:30
4. Save the file and run the applet.
