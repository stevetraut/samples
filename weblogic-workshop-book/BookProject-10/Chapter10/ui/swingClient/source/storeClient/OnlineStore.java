package storeClient;

import javax.swing.*;

/**
 * Represents a user interface for the online store. This class is the client
 * app, calling methods of the StoreFrame class to initialize UI components.
 * After initialization, StoreFrame does the heavy lifting of managing
 * interactions with the OnlineStore web service.
 */
public class OnlineStore {
	public OnlineStore() {
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
			StoreFrame frame = new StoreFrame();
			frame.initComponents();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Main entry point
	static public void main(String[] args) {
		new OnlineStore();
	}

}
