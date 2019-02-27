package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.DocErrorReporter;
import org.apache.beehive.netui.tools.doclet.taglets.SeeTaglet;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

/**
 * Represents information for linking to topics external to the current Javadoc
 * run, such as those in the JDK at Sun's web site. This class also supports
 * external linking to JSP tag library topics such as those generated by this
 * doclet. Because it supports both, this class maintains a HashMap that maps
 * "namespaces" -- package names or taglib URIs -- to paths at which "topics" (HTML
 * files) can be found.
 * <p/> 
 * During the course of a Javadoc run with this doclet,
 * if a package or URI can not be found among those included in the run, that
 * namespace will be sought using the {@link #getExternalLinkPath(String)}
 * method. Where a match is found, this class returns a full path to the directory
 * containing the namespace directory's topics. External links to JSP tag topics 
 * are supported only through the @jsptagref.taglink Javadoc tag supported through the
 * {@link org.apache.beehive.netui.tools.doclet.taglets.TagLinkTaglet}
 * taglet provided by this doclet.
 * <p/>
 * This class is made aware of external paths by reading list files at locations
 * specified by the user through the following command-line arguments: -link and
 * -linkoffline for locating package-list files that list Javdoc'ed packages;
 * -tagliblink and -tagliblinkoffline for locating uri-list files that list
 * URIs corresponding to doc'ed tag libraries.
 * <p/>
 * Support for the tag library linking mechanism only works when the JSP
 * tag topics follow the output path convention assumed by this doclet. That
 * convention specifies that JSP tag topics will be located at a path corresponding
 * to the tag library's URI, with forward slashes representing path
 * separators. The URI protocol is removed from the path. So for a tag library
 * URI such as http://www.apache.org/customtags/veryspecialtags, the assumed
 * hierarchy containing tags in the library will be:
 * <pre>
 * www.apache.org
 *     customtags
 *         veryspecialtags
 *             tag1.html
 *             tag2.html
 *             ...
 * </pre>
 * If the root of this hierarchy is specified by a -tagliblink option to be at
 * http://apache.org/taglibrarydocs, then the path returned by the
 * {@link #getExternalLinkPath(String)} method will be as follows:
 * <pre>
 * http://apache.org/taglibrarydocs/www.apache.org/customtags/veryspecialttags/
 * </pre>
 */
public final class Linker
{
    private final HashMap m_namespaceToPathMap = new HashMap();
    private final ConfigurationJspTagDoc m_configuration;
    private final DocErrorReporter m_jvdErrorReporter;

    /**
     * Represents a package-to-path or uri-to-path map that can be used to
     * create a link from an item in the current Javadoc run to a topic
     * in an externally-generated Javadoc or JspTagDoc doclet topic.
     */
    private final class LinkItem
    {
        private final String m_namespace;
        private final String m_path;
        private final boolean m_isAbsolute;

        /**
         * Constructs a new LinkItem instance from a namespace (e.g.,
         * Java package or taglib URI), a path to a directory where
         * topics for items in the namespace can be found, and a boolean
         * indicating whether the namespace path is absolute.
         * 
         * @param namespace The namespace whose topics might be linked to.
         * @param path The path at which the topics can be found.
         * @param isAbsolute <code>true</em> to indicate whether the path
         * to the namespace directory is absolute.
         */
        LinkItem(String namespace, String path, boolean isAbsolute)
        {
            this.m_namespace = namespace;
            this.m_path = path;
            this.m_isAbsolute = isAbsolute;
            if (!m_namespaceToPathMap.containsKey(namespace))
            {
                m_namespaceToPathMap.put(namespace, this);
            }
        }
        
        /**
         * Gets the namespace this link item represents.
         * 
         * @return The namespace.
         */
        public String namespace()
        {
            return m_namespace;
        }

        /**
         * Gets the namespace path.
         * 
         * @return The path.
         */
        public String getPath()
        {
            return m_path;
        }

        /**
         * True if the path for the namespace this LinkItem represents is 
         * absolute.
         * 
         * @return <code>true</code> if the path is absolute; otherwise,
         * <code>false</code>.
         */
        public boolean isAbsolute()
        {
            return m_isAbsolute;
        }

        /**
         * Returns the path and name of the item this instance represents.
         *
         * @return A string of the form "[namespace] at [path]".
         */
        public String toString()
        {
            return m_namespace + " at " + m_path;
        }
    }

    /**
     * Constructs a Linker instance with user-specified Javadoc options.
     *
     * @param configuration Configuration information, including few of the
     *                     command-line options specified by the user.
     */
    public Linker(ConfigurationJspTagDoc configuration)
    {
        m_jvdErrorReporter = configuration.getErrorReporter();
        m_configuration = configuration;
    }

    /**
     * Determines whether the <em>namespace</em> (package or taglib URI) 
     * is available for creating an external link. A namespace is externally
     * available if it is listed in a package-list or uri-list file
     * found through an argument of one of the following command-line
     * options: -link, -linkoffline, -tagliblink, -tagliblinkoffline.
     *
     * @param namespace A package name or tag library URI.
     * @return <code>true</code> if external documentation for <em>namespace</em>
     *         is known to be external; otherwise, <code>false</code>.
     */
    public boolean isNamespaceExternal(String namespace)
    {
        return m_namespaceToPathMap.get(namespace) != null;
    }

    /**
     * Determines whether the <em>namespace</em> (package or taglib URI)
     * provides a link path that is absolute (such as an absolute
     * file path or a URL). The doclet appends relative paths with a "../"
     * sequence to create a path to the root of the output before
     * inserting them as a value for linking.
     *
     * @param namespace A package name or tag library URI.
     * @return <code>true</code> if the path is absolute; otherwise,
     *         <code>false</code>.
     */
    public boolean isNamespaceAbsolute(String namespace)
    {
        LinkItem linkItem = (LinkItem) m_namespaceToPathMap.get(namespace);
        return linkItem != null ? linkItem.isAbsolute() : false;
    }

    /**
     * Gets the path to the directory where an external namespace may be found.
     * An external namespace could be the package for a typical Javadoc class or
     * interface topic, or it could be the taglib URI for a JSP tag topic created 
     * by this doclet.
     *
     * @param namespace The package name or URI for which a link path should be
     *                 returned.
     * @return The link path corresponding to <em>namespace</em>.
     */
    public String getExternalLinkPath(String namespace)
    {
        LinkItem item = (LinkItem) m_namespaceToPathMap.get(namespace);
        String externalLink = null;
        if (item != null)
        {
            externalLink = item.getPath();
        }
        return externalLink;
    }

    /**
     * Adds a URL to the list of URLs managed by this class for finding external
     * Javadoc class ref link paths.
     *
     * @param linkUrl     A URL at which Javadoc class reference topics may be found.
     * @param listPath    The path to a directory where a package-list file listing
     *                    the packages included in the class reference may be found.
     * @param linkOffline <code>true</code> to indicate that <em>linkUrl</em> and
     *                    <em>listPath</em> are parts of a -linkoffline command-line option;
     *                    <code>false</code> to indicate that the opiton is -link.
     */
    public void addUrl(String linkUrl, String listPath, boolean linkOffline)
    {
        readListFile(linkUrl, listPath, linkOffline, false);
    }

    /**
     * Adds a URL to the list of URLs managed by this class for finding external
     * JSP tag lib link paths.
     *
     * @param linkUrl     A URL at which JSP tag reference topics may be found.
     * @param listPath    The path to a directory where a uri-list file listing
     *                    the URIs included in the tag reference may be found.
     * @param linkOffline <code>true</code> to indicate that <em>linkUrl</em> and
     *                    <em>listPath</em> are parts of a -tagliblinkoffline command-line option;
     *                    <code>false</code> to indicate that the opiton is -tagliblink.
     */
    public void addJspUrl(String linkUrl, String listPath, boolean linkOffline)
    {
        readListFile(linkUrl, listPath, linkOffline, true);
    }

    /**
     * Reads the package-list or uri-list file from the path provided, passing
     * the contents to {@link #mapPackageNamesToPaths(String,java.io.BufferedReader,boolean)}
     * or {@link #mapUrisToPaths(String, java.io.BufferedReader,boolean)} for
     * mapping.
     *
     * @param linkUrl     The URL to include in paths mapped to namespaces.
     * @param listPath    The path to the directory containing the list file to be
     *                    read.
     * @param linkOffline <code>true</code> to indicate that <em>linkUrl</em> and
     *                    <em>listPath</em> are parts of a an "offline" command-line option;
     *                    <code>false</code> to indicate that the they are not.
     * @param isTagLink   <code>true</code> to indicate that a uri-list file should
     *                    be sought; <code>false</code> to indicate that a package-list file should
     *                    be sought.
     */
    private void readListFile(String linkUrl, String listPath, boolean linkOffline,
                              boolean isTagLink)
    {
        // The URL to use in links.
        linkUrl = ensureSlash(linkUrl);
        // The path to the list file's directory.
        listPath = ensureSlash(listPath);
        // true if the path to the list file is absolute.
        boolean isAbsolute = true;
        // A stream for collecting the list file contents.
        InputStream listStream = null;
        // The name of the file we're looking for.
        String listFileName = isTagLink ? "uri-list" : "package-list";
        // The complete path to the list file.
        listPath += listFileName;
        try
        {
            // A URI pointing at the list file.
            URI listUri = new URI(listPath);
            // If the URI has a scheme, it's a URL.
            if (listUri.getScheme() != null)
            {
                URL listUrl = listUri.toURL();
                if ((listUrl.getProtocol().equals("http")) ||
                        (listUrl.getProtocol().equals("file")))
                {
                    listStream = listUrl.openStream();
                }

            // If the URI doesn't have a scheme, it's a file path.
            } else
            {
                // A File pointing at the list file.
                File listFile = new File(listPath);
                isAbsolute = listFile.isAbsolute();

                // If the file path is relative, then resolve it relative to the HTML output
                // directory.
                if (!isAbsolute)
                {

                    // If the user didn't specify -linkoffline and the path is
                    // relative, then it's relative to a local output directory.
                    if (!linkOffline)
                    {

                        // If the user specified a directory for HTML output, then
                        // make the path relative to that directory.
                        if (m_configuration.isDestDirSpecified())
                        {
                            listFile =
                                    new File(m_configuration.getDestDir() + listPath);
                        }

                        // If the user didn't specify a directory for HTML output, then make
                        // the path to the list file relative to the XML output directory.
                        else
                        {
                            listFile =
                                    new File(m_configuration.getXmlDir() + listPath);
                        }
                    }

                    // If the user specified -linkoffline and the path is relative,
                    // then it's relative to the current Javadoc run location.
                    else
                    {
                        listFile = new File(listPath);
                    }
                }

                // If the path is absolute, go get the file regardless of whether
                // the user specified -linkoffline.
                else
                {
                    listFile = new File(listPath);
                }
                if (listFile.exists())
                {
                    listStream = new FileInputStream(listFile);
                } else
                {
                    m_jvdErrorReporter.printError(listFile.getPath() + 
                            ": The list file was not found.");
                    return;
                }
            }
        } catch (URISyntaxException urise)
        {
            m_jvdErrorReporter.printError(listPath + ": Error while converting this path " +
                    " to a URI. \n" + urise.getLocalizedMessage());
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError("Error while handling a file. \n" +
                    ioe.getLocalizedMessage());
        }
        if (listStream != null)
        {
            InputStreamReader reader = new InputStreamReader(listStream);
            BufferedReader listReader = new BufferedReader(reader);
            if (isTagLink)
            {
                mapUrisToPaths(linkUrl, listReader, isAbsolute);
            } else
            {
                mapPackageNamesToPaths(linkUrl, listReader, isAbsolute);
            }
        }
    }

    /**
     * Maps package names found in a package-list file to the paths at which
     * documentation for the packages may be found. Mappings are added to a single
     * HashMap that contains both package name and URI mappings.
     *
     * @param linkUrl    The URL at which documentation may be found.
     * @param listReader The contents of a package-list file.
     */
    private void mapPackageNamesToPaths(String linkUrl, BufferedReader listReader,
                                        boolean isAbsolute)
    {
        StringBuffer packageNameBuffer = new StringBuffer();
        try
        {
            int counter;
            while ((counter = listReader.read()) >= 0)
            {
                char ch = (char) counter;
                if (ch == '\n' || ch == '\r')
                {
                    if (packageNameBuffer.length() > 0)
                    {
                        String packageName = packageNameBuffer.toString();
                        String packagePath = linkUrl +
                                packageName.replace('.', '/') + '/';
                        new LinkItem(packageName, packagePath, isAbsolute);
                        packageNameBuffer.setLength(0);
                    }
                } else
                {
                    packageNameBuffer.append(ch);
                }
            }
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError(ioe.getLocalizedMessage());
        } finally
        {
            try
            {
                listReader.close();
            } catch (IOException ioe)
            {
                m_jvdErrorReporter.printError(ioe.getLocalizedMessage());
            }
        }
    }

    /**
     * Maps taglib URIs found in a uri-list file to the paths at which
     * documentation for the taglibs may be found. Mappings are added to a single
     * HashMap that contains both package name and URI mappings.
     *
     * @param linkUrl    The URL at which documentation may be found.
     * @param listReader The contents of a uri-list file.
     */
    private void mapUrisToPaths(String linkUrl, BufferedReader listReader,
                                boolean isAbsolute)
    {
        StringBuffer uriNameBuffer = new StringBuffer();
        try
        {
            int counter;
            while ((counter = listReader.read()) >= 0)
            {
                char ch = (char) counter;
                if (ch == '\n' || ch == '\r')
                {
                    if (uriNameBuffer.length() > 0)
                    {
                        String uriName = uriNameBuffer.toString();
                        String uriPath = linkUrl +
                                SeeTaglet.removeProtocolPrefix(uriName) + '/';
                        new LinkItem(uriName, uriPath, isAbsolute);
                        uriNameBuffer.setLength(0);
                    }
                } else
                {
                    uriNameBuffer.append(ch);
                }
            }
        } catch (IOException ioe)
        {
            m_jvdErrorReporter.printError(ioe.getLocalizedMessage());
        } finally
        {
            try
            {
                listReader.close();
            } catch (IOException ioe)
            {
                m_jvdErrorReporter.printError(ioe.getLocalizedMessage());
            }
        }
    }

    /**
     * Ensures that <em>url</em> ends with a trailing slash.
     *
     * @param url The URL to ensure.
     * @return The URL with a trailing slash.
     */
    private static String ensureSlash(String url)
    {
        if (!url.endsWith("/"))
        {
            url += "/";
        }
        return url;
    }
}
