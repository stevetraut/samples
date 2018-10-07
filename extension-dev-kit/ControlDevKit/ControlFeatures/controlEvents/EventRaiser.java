package controlEvents; 

import com.bea.control.*;

public interface EventRaiser extends Control
{
 
    public void startTimer();

    interface Callback 
    {
        void backEndTimer_onTimeout(long arg0);
    }
} 
