package controlEvents; 

import com.bea.control.*;

public interface LifeCycle extends Control
{ 
      public interface Callback 
    {
        void onDone(String message);
    }


    /**
     * The startMeUp method provides a place for clients to start using this control.
     * @common:operation
     */
    void startMeUp();
} 
