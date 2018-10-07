package ideExtensions.customProject; 

import com.bea.ide.document.DefaultDocumentHandler;
import com.bea.ide.document.IDocument;
import com.bea.ide.lang.java.JavaDocument;
import com.bea.ide.lang.java.JavaDocumentFactory;

import java.net.URI;

/**
 * A simple handler for a PHP document. A document handler
 * is responsible for relatively high-level functions, such as
 * validating the name of a file that corresponds to the document,
 * providing the file's extension, and so on. In contrast, the document 
 * implementation (represented in this sample by PhpDocumentImpl)
 * is responsible for lower-level functions, such as providing 
 * views of the document's contents.
 * 
 * Note that a "document" (represented by an IDocument instance)
 * is an abstraction that the IDE uses to provide special 
 * handling of certain files. For example, a JWS file provides
 * its graphical Design View because its IDocument provides one.
 */
public class PhpDocument extends DefaultDocumentHandler
{ 
    /**
     * Constructs a new instance of this handler by calling
     * its super with the extension to use for files 
     * corresponding to the document type: .php
     */
	public PhpDocument()
	{
		super("php");	
	}

    /**
     * Called by the IDE to retrieve the IDocument instance that will 
     * be used to represent a PHP document. This method returns an instance
     * of PhpDocumentImpl, which extends TextDocument, which in turn 
     * implements IDocument. That instance is responsible for low-level
     * document tasks such as rendering a view of its contents, managing
     * edits, and so on.
     * 
     * @param uriFile A URI pointing at the file that corresponds to this
     * document.
     * @return A PhpDocumentImpl instance representing the document.
     */
	public IDocument createDocumentObject(URI uriFile) throws java.io.IOException
	{
        IDocument ret = new PhpDocumentImpl(this.getClass(), uriFile);
        return ret;
	}
} 

