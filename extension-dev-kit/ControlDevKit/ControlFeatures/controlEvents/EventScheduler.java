package controlEvents; 

import com.bea.control.*;

public interface EventScheduler extends Control
{ 
    public void setAlarm(long delayInMsecs);
    public void cancelAlarm();
       /*
     * The onAlarmRinging callback notifies clients when the interval they
     * specified has passed.
     */
    public interface Callback 
    {
        void onAlarmRinging(String message, long time);
    }
  
} 
