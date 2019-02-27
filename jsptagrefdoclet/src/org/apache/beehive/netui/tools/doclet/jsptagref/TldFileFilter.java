package org.apache.beehive.netui.tools.doclet.jsptagref;

import java.io.File;
import java.io.FileFilter;

/**
 * A filter to narrow file loading to files with a .tld extension.
 */
final class TldFileFilter implements FileFilter
{
    /**
     * Determines whether <em>file</em> has a .tld extension.
     *
     * @param file The file's name.
     * @return <code>true</code> if the file has a .tld extension; otherwise,
     *         <code>false</code>.
     */
    public boolean accept(File file)
    {
        if (file.isDirectory())
        {
            return false;
        }
        String name = file.getName().toLowerCase();
        return name.endsWith(".tld");
    }
}
