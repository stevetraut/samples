package IdeExtensions.SearchResults; 

import com.bea.help.util.HelpUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.net.URL;
import javax.swing.JPanel;
import com.bea.help.search.SearchHit;
import com.bea.help.search.SearchHtml;
import com.bea.ide.Application;
import com.bea.ide.core.LookAndFeelConstants;
import com.bea.ide.core.ResourceSvc;
import com.bea.ide.ui.help.HelpSvc;
import java.awt.Label;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ColorModel;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Provides a dockable frame through which users can search WebLogic Workshop
 * documentation. Notice that this class implements IFrameView, including the getView
 * method that returns the component to show in the frame. This class is 
 * specified in the extension.xml file for this extension.  
 */
public class SearchResultsView extends JPanel implements com.bea.ide.ui.frame.IFrameView
{ 
    private javax.swing.JButton btnSubmit;
    private javax.swing.JTextField txtInput;
    private javax.swing.JScrollPane pnlResults;
    private javax.swing.JTable tblResults;
    private String m_indexUriString = null;

    Object[][] itemsList = null;

    /**
     * Constructs the search results view, assembling the components
     * of its user interface.
     */
	public SearchResultsView()
	{
        super(new GridBagLayout());
        initComponents();
	}

    /**
     * Called by the IDE to retrieve the user interface component
     * to show in the dockable frame.
     */
	public Component getView(String id)
	{
		return this;
	}
	
    /**
     * Called by the IDE to find out if this component may be shown in the 
     * user interface.
     */
    public boolean isAvailable()
    {
        return true;
    }

    /**
     * Executes the search when the user presses Enter while the
     * cursor is in the search terms box.
     */
    private void txtInputActionPerformed(java.awt.event.ActionEvent evt) 
    {
        showHits();
    }
    
    /**
     * Executes the search when the user click the Search button.
     */
    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) 
    {
        showHits();
    }
    
    /**
     * Displays a topic from the list of search hits.
     */
    private void showTopic(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            String summary = HelpSvc.get().getHelpSummary(url);     
            HelpSvc.get().displayHelp(url);
        }
        catch(Exception e) {}
    }
    
    /**
     * Calls the search function and displays the hits.
     */
    private void showHits()
    {
        SearchHit[] hits = searchDocs(txtInput.getText());
        tblResults.setModel(generateHitsTable(hits));
        formatHitsTable();
        pnlResults.repaint();
    }

    /**
     * Formats the list of search hits, setting column widths.
     */    
    private void formatHitsTable()
    {
        tblResults.getColumnModel().getColumn(0).setMaxWidth(50);        
        tblResults.getColumnModel().getColumn(0).setCellRenderer(new ScoreRenderer());
        tblResults.getColumnModel().getColumn(1).sizeWidthToFit();
    }
    
    /**
     * Searches the documentation, returning the hits in an array.
     * Note that the SearchHit and SearchHtml objects are not part
     * of the public extension API, and may change in future releases.
     */
    private SearchHit[] searchDocs(String queryString)
    {
        SearchHit[] hits = null;
        SearchHtml search = new SearchHtml();
        try 
        {
            String workshopHome = System.getProperty("workshop.home");
            String indexPath = workshopHome + "/help/index";
            File pathFile = new File(indexPath);
            URI indexUri = pathFile.toURI();
            hits = search.query(indexUri, queryString);
        }
        catch (URISyntaxException use)
        {
            use.printStackTrace();
        }
        catch (MalformedURLException mue)
        {
            mue.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return hits;
    }

    /**
     * Receives an array of search hits and uses its
     * members to populate a table model for display to the user.
     */
	public TableModel generateHitsTable(SearchHit[] hits) 
    {
        /*
         * If there are any hits to show, convert the hit list
         * to an array that can be used in the hit list table.
         * If not, show an empty table.
         */
        if ((hits != null) && (hits.length > 0))
        {
            itemsList = convertHits(hits);
        }
        else
        {
            itemsList = new Object[0][3];
        }
        
        TableModel catalogTable = new AbstractTableModel() 
        {
            Object[][] data = itemsList;

            String[] headers = {"Score", "Title"};
            
            public int getRowCount() 
            { 
                return itemsList.length; 
            }
            public int getColumnCount() 
            { 
                return headers.length; 
            }
            public Object getValueAt( int row, int column ) 
            { 
                return data[row][column]; 
            }
            public String getColumnName( int column ) 
            { 
                return headers[column]; 
            }
        };
        return catalogTable;   
    }

    /**
     * Converts an array of search hits to an Object array
     * that can be used to construct a table model.
     */
    public Object[][] convertHits (SearchHit[] hits) 
    {
        Object[][] itemsArray = new Object[hits.length][3];
    
        for (int i = 0; i < hits.length; i++) 
        {
            Object[] itemArray = new Object[3];            
            Float scoreFloat = new Float(hits[i].getScore());
            
            itemArray[0] = new Float(hits[i].getScore()).toString();
            itemArray[1] = hits[i].getTitle();
            itemArray[2] = hits[i].getUrl();                
            itemsArray[i] = itemArray;
        }
        return itemsArray;
    }

    /**
     * A renderer to turn the values returned as hit scores 
     * into static progress bars. 
     */
    private class ScoreRenderer implements TableCellRenderer
    {
        public Component getTableCellRendererComponent(JTable arg0, Object arg1, 
            boolean arg2, boolean arg3, int arg4, int arg5)
        {
            int labelInt = 0;
            String labelValue = (String)arg1;
            if(labelValue.equals("1.0"))
            {
                labelInt = 100;
            }
            else 
            {
                float floatPrim = new Float(labelValue).floatValue() * 100;
                labelInt = new Float(floatPrim).intValue();
            }
            JProgressBar scoreBar = new JProgressBar(0,100);
            scoreBar.setValue(labelInt);
            scoreBar.setBorderPainted(false);
            scoreBar.setForeground(UIManager.getColor(LookAndFeelConstants.MINI_FRAME_INACTIVE_BORDER_FILL));
            return scoreBar;
        }        
    }

    /**
     * Initializes the user interface components of the search view. These
     * include an input box for entering search terms, a button to click
     * for executing the search, and a table in which to display the results.
     */
    private void initComponents() 
    {
        this.setBackground(Color.WHITE);

        java.awt.GridBagConstraints gridBagConstraints;

        txtInput = new javax.swing.JTextField();
        txtInput.setText("Enter search text here.");

        btnSubmit = new javax.swing.JButton();
        btnSubmit.setText("Search");

        pnlResults = new javax.swing.JScrollPane();
        pnlResults.setBackground(Color.WHITE);
        pnlResults.setBorder(null);
        tblResults = new javax.swing.JTable();
        tblResults.setModel(new javax.swing.table.DefaultTableModel
        (
            new Object [][] {},
            new String [] {"Score", "Title"}
        ));
        formatHitsTable();
        pnlResults.setViewportView(tblResults);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 63);
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.add(txtInput, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 3);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        this.add(btnSubmit, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(28, 3, 3, 3);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        this.add(pnlResults, gridBagConstraints);

        btnSubmit.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) 
            {
                btnSubmitActionPerformed(evt);
            }
        });

        txtInput.addActionListener(new java.awt.event.ActionListener() 
        {
            public void actionPerformed(java.awt.event.ActionEvent evt) 
            {
                txtInputActionPerformed(evt);
            }
        });
        
        /*
         * Handles the user's double-click on a row of the results table.
         * Collect the URL for the topic they double-clicked and
         * call a function that displays the topic in a browser.
         */
        tblResults.addMouseListener(new MouseListener()
        {
            public void mouseClicked(MouseEvent arg0)
            {
                if (arg0.getClickCount() > 1)
                {
                    String url = (String)tblResults.getModel().getValueAt(tblResults.getSelectedRow(), 2);
                    String language = HelpUtil.getHelpLanguage();
                    /* 
                     * Get an absolute path to workshop home (the directory containing
                     * the WebLogic Workshop executable).
                     */
                    String workshopHome = Application.get().newFile("").getDisplayPath();
                    /*
                     * Builds a URL that points to the topic to display in a browser.
                     * This is an absolute URL; in WebLogic Workshop 8.1 SP2 and later,
                     * relative URLs are supported.
                     */ 
                    url = "file:///" + workshopHome + "help/doc" + "/" + language 
                        + "/" + url.replaceFirst("../","");
                    showTopic(url);
                }
            }
            
            public void mouseEntered(MouseEvent arg0)
            {}

            public void mouseExited(MouseEvent arg0)
            {}

            public void mousePressed(MouseEvent arg0)
            {}

            public void mouseReleased(MouseEvent arg0)
            {}
        });
    }
} 
