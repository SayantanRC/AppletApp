
import java.applet.Applet ;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
This applet shows time for different timezones.
Initially GMT is shown.
*/
 
public class AppletApp extends Applet {

    JComboBox<String> zones;            // drop down box for showing different zones
    JTextField displayTimeTextField;    // text field to show the time
    JTextField gmtMention;              // another text field
    JPanel panel;                       // panel to add all components
    
    /*
    Double dimension array is used to store timezones
    1st col -> timezone name (Example: "India Standard Time")
    2nd col -> deviation from GMT (Example: "+5:30")
    */
    String timezones[][];
    
    String displayTimeString;           // String to hold time to be displayed
    
    @Override
    public void init() {
        super.init();
        
        //parse timezone data from file
        parseData();
        
        displayTimeTextField = new JTextField();
        
        /*
        We get GMT by using DateFormat and setting time zone to GMT
        Format specifier for SimpleDateFormat: "hh:mm a" -> Gives 12 hr. time with AM/PM
        */
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        /*
        We set the time in JtextField
        */
        displayTimeTextField.setText(displayTimeString = df.format(date));
           //adjust font size
        displayTimeTextField.setFont(displayTimeTextField.getFont().deriveFont(30f));
            //prevent be edit from keyboard or selection from pointing device
        displayTimeTextField.setEditable(false);
        displayTimeTextField.setHighlighter(null);
            //set gravity and size
        displayTimeTextField.setHorizontalAlignment(JTextField.CENTER);
        displayTimeTextField.setPreferredSize(new Dimension(400, 100));
        
        //Adding only the name part from timezones[][] to drop down box
        //first element should be GMT
        zones = new JComboBox<>();
        zones.addItem("GMT");
        for (int i = 0; i < timezones.length; i++){
            zones.addItem(timezones[i][0]);
        }
        
        //add listener
        zones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                
                int id = zones.getSelectedIndex();      //id of selected item, starts from 0
                
                displayTimeString = df.format(date);    //getting GMT in 12 hr. format
                
                if (id == 0){                           // 0 is GMT, so no changes
                    displayTimeTextField.setText(displayTimeString);
                }
                else {
                    try {
                        
                        /*
                        We get GMT in int, the deviation in int using getTimeInInt()
                        Then we add them and convert back to string using getTimeInString()
                        */
                        
                        int sysClock = getTimeInInt(displayTimeString);
                        int change = getTimeInInt(timezones[id-1][1]);  //-1 as first element is GMT
                        sysClock = sysClock + change;
                        if (sysClock < 0)                       // in case of negative deviation
                            sysClock = sysClock + 24*60;
                        else if (sysClock >= 1440)              // in case of positive deviation -> 1400 is 24:00
                            sysClock = sysClock - 1440;
                        displayTimeTextField.setText(getTimeInString(sysClock));
                    }
                    catch (Exception e){
                        //Display a message box if there was any error
                        JOptionPane.showMessageDialog(null, "Error in combo box:\n" + e.getMessage() + "\n\nPlease close and retry.", "Error", JOptionPane.OK_OPTION);
                    }
                }
            }
        });
        zones.setPreferredSize(new Dimension(400, 50));
        
        // setting up another textfield
        gmtMention = new JTextField("All times are with respect to Greenwich Mean Time (GMT)");
        gmtMention.setHorizontalAlignment(JTextField.CENTER);
        gmtMention.setPreferredSize(new Dimension(400, 50));
        
        // creating panel and adding components
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setSize(new Dimension(400, 215));
        panel.add(zones);
        panel.add(displayTimeTextField);
        panel.add(gmtMention);
        
        // setting layout and adding panel
        setLayout(new GridBagLayout());
        add(panel);
    }
    
    /*
    This method reads a text file and converts it to array timezones[][]
    timezones[][] contain two column
        first has timezon names
        second has adjustments wrt GMT
    */
    public void parseData(){
        
        Vector<String> tempTempzones = new Vector<>(1);
        String line;
        
        try {
            
            //reading each line and adding it to a vector
            BufferedReader br = new BufferedReader(new FileReader("countries.txt"));
            while ((line = br.readLine()) != null){
                tempTempzones.addElement(line);
            }
            
            //converting the vector to timezones[][] array
            timezones = new String[tempTempzones.size()][2];
            for (int i = 0; i < tempTempzones.size(); i++){
                line = tempTempzones.elementAt(i);
                timezones[i][0] = line.substring(0, line.lastIndexOf(' '));     //first part -> name
                timezones[i][1] = line.substring(line.lastIndexOf(' ') + 1);    //second part -> deviation
            }
        }
        catch (Exception e){
            //Display a message box if there was any error
            JOptionPane.showMessageDialog(null, "Error reading file:\n" + e.getMessage() + "\n\nPlease close and retry.", "Error", JOptionPane.OK_OPTION);
        }
    }
    
    /*
    Converts a string representing 12 hr. time to int
    Also used to convert deviations from second column of timezones[][] to int
    */
    int getTimeInInt(String t){
        int h = 0;
        int m;
        try {
            
            /*
            If String t is a GMT deviation (eg: "-7:00") then it won't have any blank space.
            But if it is a 12 hr. time (eg: "7:30 PM") then it will have a blank space.
            */
            if (t.indexOf(' ') != -1){
                
                //For a 12 hr. time, 12 is to be added to hour if it is in "PM"
                h = t.substring(t.indexOf(' ') + 1).equals("PM")? 12 : 0;
                t = t.substring(0, t.indexOf(' '));
            }
            
            //getting hour and minutes
            h = h + Integer.parseInt(t.substring(0, t.indexOf(':')));
            m = Integer.parseInt(t.substring(t.indexOf(':') + 1));
            
            return h*60 + m;
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error in getTimeInInt(" + t + "):\n" + e.getMessage() + "\n\nPlease close and retry.", "Error", JOptionPane.OK_OPTION);
            return 0;
        }
    }
    
    /*
    Converts an int to 12 hr. time String
    */
    String getTimeInString(int t){
        
        String sTime = "";
        
        int h, m;
        h = t / 60;
        m = t - h*60;
        
        //check if "PM"
        if (h > 12){
            sTime = "PM";
            h = h - 12;
        }
        else {
            sTime = "AM";
        }
        
        //formatting with zero padding
        sTime = String.format("%02d", h) + ":" + String.format("%02d", m) + " " + sTime;
        
        return sTime;
    }
}
/*
<applet code="AppletApp.class" width = 400 height = 215></applet>
*/
