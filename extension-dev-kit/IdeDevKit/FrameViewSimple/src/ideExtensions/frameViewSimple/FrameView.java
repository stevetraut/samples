package ideExtensions.frameViewSimple;

import com.bea.ide.ui.frame.FrameSvc;
import com.bea.ide.ui.frame.IFrameView;
import java.awt.Component;
import javax.swing.JLabel;

/**
 * A frame to display a simple message. This class extends JLabel, a
 * Java Swing user interface component, returning an instance of itself
 * when the IDE requests its UI. It also implements IFrameView, an
 * interface to support this class' role as a frame in the WebLogic Workshop
 * IDE.
 */
public class FrameView extends JLabel implements com.bea.ide.ui.frame.IFrameView
{
    // Declare a flag to use when illustrating frame availability.
	private boolean m_isAvailable = false;

    /**
     * Constructs a new instance of this class with a simple message to
     * display in the frame.
     */
	public FrameView()
	{
        // Call the JLabel class' constructor.
		super("hi mom. yo dad. peace sis.");
	}

    /**
     * Called by the IDE to retrieve the user interface component to
     * display in the frame.
     *
     * @param id The value of the id attribute specified for this <frame-view>
     * in the extension.xml; <code>null</code> if the attribute was not
     * specified.
     * @return The UI component to display in the frame.
     */
	public Component getView(String id)
	{
        // Return a pointer to an instance of this class, which extends JLabel.
		return this;
	}

    // alternately make this frame appear or disappear on document, view, and layout switches.
    /**
     * Called by the IDE to determine whether this frame is "available" --
     * in other words, whether the frame may be displayed in the UI.
     * This example simply toggles availability back and forth.
     *
     * This method will only be called if the extension.xml specifies the
     * askavailable attribute as "true" in the <frame-view> element.
     *
     * @return <code>true</code> if the frame is available for display;
     * <code>false</code> if it is not.
     */
	public boolean isAvailable()
	{
		m_isAvailable = !m_isAvailable;
		return m_isAvailable;
	}
}
