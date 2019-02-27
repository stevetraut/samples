package storeClient;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.*;
import weblogic.jws.proxies.*;
import org.openuri.www.Item;
import org.openuri.www.x2002.x04.soap.conversation.*;

/* 
 * This class is designed merely to display a table with data from the web
 * service. But it demonstrates how proxy and conversation information
 * might look in a Swing context.
 * 
 * Note that the weblogic.jws.proxies package is required to support the proxy. 
 * The org.openuri.www.x2002.x04.soap.conversation package provides header 
 * classes that will be used to send conversation information with requests 
 * to the web service.
 */
public class StoreFrame extends javax.swing.JFrame {

	javax.swing.JLabel lblCatalog = new javax.swing.JLabel();
	javax.swing.JButton btnAddItem = new javax.swing.JButton();
	javax.swing.JLabel lblWidgetsLogo = new javax.swing.JLabel();

	String userName = "Joe";
	String customerNumber = "123465";

	/*
	 * Create a variable to hold the web service proxy. This code follows a
	 * predictable format. For example, the generated proxy name is the name of the
	 * service with "_Impl" appended. The name of the SOAP proxy is the name of the
	 * service with "Soap" appended (because it communicate via SOAP over HTTP).
	 * This is the component that does the actual work of translating requests and
	 * responses to and from SOAP-style XML messages.
	 */
	OnlineStorePoll_Impl storeServiceProxy;
	OnlineStorePollSoap storeProxy;

	/*
	 * Variables needed when starting and maintaining a conversation.
	 */
	String convID;
	String serverURL = "http://localhost:7001/BookProject/Chapter10/OnlineStorePoll.jws?WSDL";
	String greeting;
	StartHeader sh;
	ContinueHeader ch;

	/*
	 * Variables to hold the Item array returned by the viewCatalog method, and to
	 * represent the JTable and JScrollPane to be used in the user interface.
	 */
	Item[] catalogItems;
	javax.swing.JTable tblCatalog;
	javax.swing.JScrollPane scrCatalog;

	public StoreFrame() {

		WindowListener wndCloser = new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				try {
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					System.exit(0);
				}
			}

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		addWindowListener(wndCloser);
		setVisible(true);
	}

	/**
	 * Initializes user interface components.
	 */
	public void initComponents() throws Exception {
		/*
		 * Call the createProxy and generateConvID methods to initialize the proxy
		 * classes and conversation ID value.
		 */
		createProxy();
		generateConvID();

		/*
		 * Initialize variable for the start header and continue values. These use the
		 * conversation ID assigned to a member variable from generatedConvID.
		 */
		sh = new StartHeader(convID, "http://localhost:7001/OnlineStorePoll.jws");
		ch = new ContinueHeader(convID);

		/*
		 * Initialize the catalog table using the TableModel object returned by the
		 * generateCatalogTable method. Insert the new table into a JScrollPane.
		 */
		tblCatalog = new javax.swing.JTable(generateCatalogTable());
		scrCatalog = new javax.swing.JScrollPane(tblCatalog);

		/*
		 * The remaining code in this method sets up other user interface elements used
		 * by the application, the pull it all together in the window.
		 */
		tblCatalog.setVisible(true);
		tblCatalog.setSize(new java.awt.Dimension(450, 103));
		tblCatalog.setLocation(new java.awt.Point(20, 90));

		scrCatalog.setSize(new java.awt.Dimension(450, 103));
		scrCatalog.setLocation(new java.awt.Point(20, 90));
		scrCatalog.setVisible(true);

		getContentPane().add(scrCatalog);

		lblCatalog.setSize(new java.awt.Dimension(240, 20));
		lblCatalog.setLocation(new java.awt.Point(20, 60));
		lblCatalog.setVisible(true);
		lblCatalog.setText("Please choose from our catalog:");
		lblCatalog.setForeground(java.awt.Color.black);
		lblCatalog.setFont(new java.awt.Font("SansSerif", 0, 12));

		btnAddItem.setSize(new java.awt.Dimension(90, 30));
		btnAddItem.setLocation(new java.awt.Point(20, 210));
		btnAddItem.setVisible(true);
		btnAddItem.setText("Add Item");

		lblWidgetsLogo.setSize(new java.awt.Dimension(220, 50));
		lblWidgetsLogo.setLocation(new java.awt.Point(20, 10));
		lblWidgetsLogo.setVisible(true);
		lblWidgetsLogo.setText("Widgets R Us");
		lblWidgetsLogo.setForeground(java.awt.Color.black);
		lblWidgetsLogo.setFont(new java.awt.Font("Dialog", 1, 30));

		setLocation(new java.awt.Point(0, 0));
		setSize(new java.awt.Dimension(497, 285));
		getContentPane().setLayout(null);
		setTitle("Widgets R Us Online Store");
		getContentPane().add(lblCatalog);
		getContentPane().add(btnAddItem);
		getContentPane().add(lblWidgetsLogo);

		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				thisWindowClosing(e);
			}
		});

	}

	/*
	 * Creates the proxy object that will be used to communicate with the web
	 * service.
	 */
	private void createProxy() {
		try {
			storeServiceProxy = new OnlineStorePoll_Impl(serverURL);
			storeProxy = storeServiceProxy.getOnlineStorePollSoap();
		} catch (IOException ex) {
			System.out.println("Error creating proxy");
		}
	}

	/* A method to generate a GUID for use as a conversation ID. */
	public void generateConvID() {
		java.rmi.server.UID uid = new java.rmi.server.UID();
		String iastr = null;
		try {
			java.net.InetAddress ia = java.net.InetAddress.getLocalHost();
			iastr = ia.getHostAddress();
		} catch (java.net.UnknownHostException uhe) {
			iastr = "uknownhost";
		}

		String guid = iastr + "-" + uid.toString();

		guid = guid.replace(':', '.');
		guid = guid.replace('[', '(');
		guid = guid.replace(']', ')');
		convID = guid;
	}

	public TableModel generateCatalogTable() {
		try {
			/*
			 * Call the startShopping method with a user name and and customer number passed
			 * from a login dialog box (that code is not shown here). This starts the
			 * conversation by passing a StartHeader (initialized in code later in this
			 * section).
			 */
			greeting = storeProxy.startShopping(userName, customerNumber, sh);

			/*
			 * Call the viewCatalog method to retrieve the list of items in the catalog.
			 * Notice that a ContinueHeader is passed with the method call.
			 */
			catalogItems = storeProxy.viewCatalog(ch);
		} catch (java.rmi.RemoteException re) {
			/*
			 * Code to retrieve the contents of the error detail and display an error
			 * message the user.
			 */
		}

		/*
		 * Declare an array of Object arrays to use for filling the table with the list
		 * of items. We�ll assign a value from a method that converts the Item array
		 * returned by the web service to the type we need here. Code for the method
		 * follows.
		 */
		final Object[][] itemsList = convertItems(catalogItems);

		/*
		 * Create a new TableModel object that will be used to create a JTable.
		 */
		TableModel catalogTable = new AbstractTableModel() {
			/*
			 * Set the table�s values to the contents of the array of Object arrays.
			 */
			Object[][] data = itemsList;

			/* Set the table�s headers. */
			String[] headers = { "Item Code", "Item Name", "Item Price" };

			/*
			 * Implement methods of the AbstractTableModel interface using the data and
			 * headers variables.
			 */
			public int getRowCount() {
				return itemsList.length;
			}

			public int getColumnCount() {
				return headers.length;
			}

			public Object getValueAt(int row, int column) {
				return data[row][column];
			}

			public String getColumnName(int column) {
				return headers[column];
			}
		};
		return catalogTable;
	}

	public Object[][] convertItems(Item[] items) {
		/*
		 * Declare an array of Object arrays, giving it values corresponding to the
		 * number of items in the list (for table rows) and the number of fields in the
		 * Item class for table columns.
		 */
		Object[][] itemsArray = new Object[items.length][3];
		/*
		 * Loop through the array of items, turning each one into an array whose members
		 * are its code, name, and price fields.
		 */
		for (int i = 0; i < items.length; i++) {
			Object[] itemArray = new Object[3];
			itemArray[0] = new Integer(items[i].getCode());
			itemArray[1] = items[i].getName();
			itemArray[2] = new Double(items[i].getPrice());
			itemsArray[i] = itemArray;
		}
		/* Return the new array of Object arrays. */
		return itemsArray;
	}

	void thisWindowClosing(java.awt.event.WindowEvent e) {
		setVisible(false);
		dispose();
		System.exit(0);
	}

}
