package storeClient;

import javax.swing.*;

public class OnlineStore {
	public OnlineStore() {
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} 
			catch (Exception e) { 
			}
			StoreFrame frame = new StoreFrame();
			frame.initComponents();
			frame.setVisible(true);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Main entry point
	static public void main(String[] args) {
		new OnlineStore();
	}
	
}
