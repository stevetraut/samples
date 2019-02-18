package com.accounts.creditcheck.ch10;

import java.io.StringReader;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import weblogic.apache.xerces.parsers.DOMParser;

public class CreditCheck 
	implements javax.ejb.MessageDrivenBean, javax.jms.MessageListener {

	// A constant to hold the name of the queue for sending a response.
	public final static String REPLY_QUEUE = "ch10.credit.ratingQ";

	// The connection factory installed with WebLogic Workshop.
	public final static String QUEUE_CONNECTION_FACTORY = "weblogic.jws.jms.QueueConnectionFactory";

	MessageDrivenContext ejbContext;
	Context jndiContext;

	public void setMessageDrivenContext(MessageDrivenContext mdctx) {
        	ejbContext = mdctx;
	}

	public void ejbCreate(){}

	public void onMessage(Message message) {
		try {
			System.out.println("CreditCheck: Request message received.");

			// Get the message and parse its contents as XML.
			TextMessage requestMsg = (TextMessage)message;
			StringReader readerXML = new StringReader(requestMsg.getText());
			System.out.println("CreditCheck: Request message extracted.");
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(readerXML));
			org.w3c.dom.Document xmlDoc = parser.getDocument();
			System.out.println("CreditCheck: Request XML parsed.");

			// Extract the available credit value from the message.
			NodeList creditNodes = xmlDoc.getElementsByTagName("purchase_total");
			Node creditNode = creditNodes.item(0).getFirstChild();
			String totalPurchaseString = creditNode.getNodeValue();
			double totalPurchase = Double.parseDouble(totalPurchaseString);
			System.out.println("CreditCheck: Total purchase extracted: " + totalPurchase);

			// Extract the applicant ID to send back with the response message.
			String id = requestMsg.getStringProperty("applicantID");
			System.out.println("CreditCheck: Request message prop: id: " + id);

			/* 
			 * Find out whether the applicant is approved, and put the approval
			 * value into a variable for use in the response message.
			 */
			boolean isApproved = true;
			if ( totalPurchase > 20.0 ) {
				isApproved = false;
			}

			// Set up a JMS resource for sending a response message.
			jndiContext = new InitialContext();
			Queue replyQueue = (Queue)jndiContext.lookup(REPLY_QUEUE);
			QueueConnectionFactory factory = (QueueConnectionFactory)jndiContext.lookup
				(QUEUE_CONNECTION_FACTORY);
			QueueConnection connect = factory.createQueueConnection();
			QueueSession session = connect.createQueueSession(true, 0);
			QueueSender sender = session.createSender(replyQueue);		

			/*
			  * Create a string whose value is XML containing values to return to
			  * the web service. The XML here must be in the form expected by the
			  * XML map on the JMS control.
			  */
			String messageXML = "<rating_response>" +
				"<is_approved>" + isApproved + "</is_approved>" +
				"</rating_response>";
				
			// Create the response message, giving it the XML as a payload.
			TextMessage replyMsg = session.createTextMessage(messageXML);

			/* 
			  * Set the property that will be extracted by the JMS control's 
			  * property XML map.
			  */
			replyMsg.setStringProperty("applicantID", id);

			/* 
			 * Set the response message's JMSCorrelationID to that of the received
			 * message.
			 */
			replyMsg.setJMSCorrelationID(message.getJMSCorrelationID());
			sender.send(replyMsg);

			System.out.println("CreditCheck: Response message sent: " + replyMsg.getText());

			// Release JMS resources.
			session.commit();
			connect.close();

        	} catch(Exception e) {
        		System.out.println("CreditCheck: Exception reading message: " + e);
            		throw new EJBException(e);
        	}
	}

	public void ejbRemove(){
        	try {
        		jndiContext.close();
			ejbContext = null;
		} catch(NamingException ne) { /* nothing here */ }
	}
}
