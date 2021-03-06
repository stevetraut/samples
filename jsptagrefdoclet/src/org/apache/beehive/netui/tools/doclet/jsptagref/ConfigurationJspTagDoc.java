package org.apache.beehive.netui.tools.doclet.jsptagref;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;
import org.apache.beehive.netui.tools.doclet.taglets.XmlTagletManager;

import java.io.File;
import java.util.*;

/**
 * Represents the options passed to Javadoc when it is run
 * with this doclet. This class is responsible for retrieving the
 * options and preparing them for use by the doclet.
 * <p/>
 * Note that this class handles only those options that are specific to
 * this doclet (although that list includes options that are also handled
 * by the standard doclet). See the Javadoc tool documentation for information
 * about which options are intended for Javadoc itself, and not for doclets.
 */
public final class ConfigurationJspTagDoc
{
    /**
     * The taglet manager keeps track of taglets used during the current
     * Javadoc run. A taglet generates XML for a Javadoc tag.
     */
    private XmlTagletManager m_tagletManager;
    /**
     * The linker keeps track of potential link destinations for the 
     * current Javadoc run.
     */
    private final Linker m_linker = new Linker(this);
    /**
     * The directory to which HTML files will be written.
     * This is specified by the -d or -destdir command-line
     * option.
     */
    private String m_destDir;
    /**
     * The directory in which this doclet will write XML
     * files. This is specified by the -xmldir
     * command-line option.
     */
    private String m_xmlDir;
    /**
     * The directory in which this doclet will look for
     * TLD files. This is specified by the -tlddir
     * command-line option.
     */
    private File[] m_tldFiles;
    /**
     * Whether content from the TLD should be used in HTML
     * output.
     */
    private boolean m_tldContent;
    /**
     * Whether content from @jsptagref: Javadoc tags should be used in HTML
     * output.
     */
    private boolean m_jspTagRefContent;
    /**
     * Whether content from standard Javadoc should be used in HTML
     * output.
     */
    private boolean m_standardContent;
    /**
     * Whether the author information should be included in HTML output.
     */
    private String m_author = "false";
    /**
     * The text to use near the top of the overview summary topic.
     */
    private String m_docTitle = "Tag Library Documentation";
    /**
     * The text to use in the &lt;title> tag of HTML output.
     */
    private String m_windowTitle = "Tag Library Documentation";
    /**
     * The text that should appear at the bottom of topics, beneath the
     * footer.
     */
    private String m_bottom = "Documentation generated by JspTagDoc.";
    /**
     * The text of the HTML footer.
     */
    private String m_footer = "";
    /**
     * The text of the HTML header.
     */
    private String m_header = "";
    /**
     * Currently not supported -- this is a placeholder.
     * <p/>
     * The path to the help file that should accompany generated HTML.
     */
//    private String m_helpFile;
    /**
     * Currently not supported -- this is a placeholder.
     * <p/>
     * The path to a CSS file specified with the -stylesheetfile option.
     */
//    private String m_styleSheetFile;
    /**
     * Whether keywords should be added to topics at HTML META tags. These
     * tags would include tag name and URI, as well as attribute names.
     */
    private String m_keywords = "false";
    /**
     * Whether an index topic should be omitted from HTML output.
     */
    private String m_noIndex = "false";
    /**
     * Whether nav bars should be omitted from HTML output.
     */
    private String m_noNavbar = "false";
    /**
     * Currently not supported -- this is a placeholder.
     * <p/>
     * Whether @since information should be omitted from HTML output.
     */
//    private String m_noSince = "false";
    /**
     * Currently not supported -- this is a placeholder.
     * <p/>
     * Whether a time stamp should be omitted from HTML output.
     */
//    private String m_noTimeStamp = "false";

    /**
     * Whether the Tag Information, Function Information, and Library Information
     * sections should be excluded in topics.
     */
    private String m_noTagInfo = "false";
    /**
     * Whether the doclet should be prevented from converting links to classes
     * into links to JSP tag topics where possible.
     */
    private String m_noLinkConversion = "false";
    /**
     * An error reporter received from Javadoc and used to print
     * error information during a doclet run.
     */
    private static DocErrorReporter m_errorReporter;

    /**
     * Constructs a new instance of this class using the information received
     * from Javadoc. <em>root</em> includes information about
     * classes and interfaces found during the run, as well as options specified
     * by the user.
     *
     * @param root Information collected by Javadoc about handler classes.
     */
    public ConfigurationJspTagDoc(RootDoc root)
    {
        setOptions(root);
    }

    /**
     * Gets an <code>XmlTagletManager</code> instance for the current 
     * Javadoc run. This can be used to retrieve the taglet that should 
     * be used for a given Javadoc tag.
     *
     * @return The taglet manager.
     */
    public XmlTagletManager getTagletManager()
    {
        return m_tagletManager;
    }

    /**
     * Gets an instance of the <code>Linker</code> class for the current 
     * Javadoc run. This instance can be used to retrieve the URLs of class 
     * documentation that are not in the current run, but whose location is 
     * specified by a -link or -linkoffline option.
     *
     * @return A linker instance.
     */
    public Linker getLinker()
    {
        return m_linker;
    }

    /**
     * Gets the error reporter received from Javadoc. This is used to
     * print notices, warnings, and errors to the console.
     *
     * @return The error reporter.
     */
    public DocErrorReporter getErrorReporter()
    {
        return m_errorReporter;
    }

    /**
     * Gets a path to the HTML destination output directory as specified by the
     * -destdir or -d options.
     *
     * @return The destination directory.
     */
    public String getDestDir()
    {
        return m_destDir;
    }

    /**
     * Gets the path to the directory to which XML files will be written; this
     * directory is specified by the -xmldir option.
     *
     * @return The path to the XML directory.
     */
    public String getXmlDir()
    {
        return m_xmlDir;
    }
    
    /**
     * Gets the array of TLD files to use in the current run. The paths to
     * these files is specified by the -tldpath option.
     *
     * @return An array of File objects representing the TLD files.
     */
    public File[] getTldfiles()
    {
        return m_tldFiles;
    }

    /**
     * <code>true</code> if the user specified an -htmldir; otherwise,
     * <code>false</code>.
     */
    public boolean isDestDirSpecified()
    {
        return null != m_destDir;
    }

    /**
     * <code>true</code> if the user specified "tld" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     * <p/>
     * The tld argument specifies that the doclet should include
     * documentation from TLD files in output.
     * 
     * @return <code>true</code> if the user specified "tld" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     */
    public boolean useTldContent()
    {
        return m_tldContent;
    }

    /**
     * <code>true</code> if the user specified "jsptagref" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     * <p/>
     * The jsptagref argument specifies that the doclet should
     * include content from @jsptagref.* Javadoc tags in
     * output.
     * 
     * @return <code>true</code> if the user specified "jsptagref" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     */
    public boolean useJspTagRefContent()
    {
        return m_jspTagRefContent;
    }

    /**
     * <code>true</code> if the user specified "standard" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     * <p/>
     * The standard argument specifies that the doclet should include
     * content from standard Javadoc tags (such as @param) in output.
     * 
     * @return <code>true</code> if the user specified "standard" as an 
     * argument to the -contentsource option; otherwise,
     * <code>false</code>.
     */
    public boolean useStandardContent()
    {
        return m_standardContent;
    }

    /**
     * Gets the doc title string specified with the -doctitle option.
     * <p/>
     * The doc title appears at the top of the libraries summary
     * file included with HTML output.
     * 
     * @return The doc title string.
     */
    public String getDocTitle()
    {
        return m_docTitle;
    }

    /**
     * Get the window title string specified with the -windowtitle option.
     * <p/>
     * The window title appears in the browser's title bar.
     * 
     * @return The window title string.
     */
    public String getWindowTitle()
    {
        return m_windowTitle;
    }

    /**
     * Returns "true" if the text following the @author Javadoc tag
     * should be included in output.
     * 
     * @return "true" if the output should be included; otherwise,
     * "false".
     */
    public String getAuthor()
    {
        return m_author;
    }

    /**
     * Gets the string specified with the -bottom option.
     * <p/>
     * The bottom text appears after all other content
     * in HTML output.
     * 
     * @return The bottom string.
     */
    public String getBottom()
    {
        return m_bottom;
    }

    /**
     * Gets the header string specified with the -header option.
     * <p/>
     * The header string appears at the top of HTML
     * output, after the doc title string (if any).
     * 
     * @return The header string.
     */
    public String getHeader()
    {
        return m_header;
    }

    /**
     * Gets the footer string specified with the -footer option.
     * <p/>
     * The footer string appears at the bottom of the HTML
     * output, before the bottom string (if any).
     * 
     * @return The footer string.
     */
    public String getFooter()
    {
        return m_footer;
    }

//    public String getHelpFile()
//    {
//        return m_helpFile;
//    }

//    public String getStyleSheetFile()
//    {
//        return m_styleSheetFile;
//    }

    /**
     * Returns "true" if the user specified the -nonavbar
     * option.
     * 
     * @return "true" if the user specified -nonavbar; otherwise,
     * "false".
     */
    public String getNoNavbar()
    {
        return m_noNavbar;
    }

//    public String getNoTimeStamp()
//    {
//        return m_noTimeStamp;
//    }

    /**
     * Returns "true" if the user specified the 
     * -noindex option.
     * <p/>
     * Specifying the -noindex option causes the doclet to not
     * generated an index topic that lists all tags and attributes in
     * the run.
     * 
     * @return "true" if the user specified the -noindex
     * option; otherwise, "false".
     */
    public String getNoIndex()
    {
        return m_noIndex;
    }

    /**
     * Returns "true" if the user specified the -keywords
     * option.
     * </p>
     * The -keywords option specifies that HTML meta keyword
     * tags should be added to the HTML output. For example, for
     * a &lt;c:choose> tag, the following &lt;meta> tag might be added:
     * 
     *     &lt;meta name="keywords" content="choose Tag">
     * 
     * @return "true" if the user specified the -keywords
     * option; otherwise, "false".
     */
    public String getKeywords()
    {
        return m_keywords;
    }

    /**
     * Returns "true" if the user specified the -notaginfo
     * option.
     * <p/>
     * The -notaginfo option specifies that the doclet should not
     * include "Tag Information" and "Library Information"
     * sections in tag and library topics, respectively.
     * 
     * @return "true" if the user specified the -notaginfo option;
     * otherwise, "false".
     */
    public String getNoTagInfo()
    {
        return m_noTagInfo;
    }

    /**
     * Returns "true" if the user specified the -nolinkconversion option.
     * <p/>
     * The -nolinkconversion option specifies that the doclet shouldn't
     * convert Javadoc links between classes into links between tags where
     * the class linked to is a tag-class named for the tag in the TLD. This
     * kind of conversion can be convenient when the Javadoc comments around
     * the link refer to the class as a tag, rather than a tag handler class.
     * 
     * @return "true" if the user specified the -nolinkconversion option;
     * otherwise, "false".
     */
    public String getNoLinkConversion()
    {
        return m_noLinkConversion;
    }

    /**
     * Called by Javadoc to retrieve the number of options supported for the specified
     * command-line <em>option</em>. This is the number of option strings that
     * should be accounted for with a given Javadoc option.
     * The number returned should be a sum that includes the option name
     * (such as "-tag") and the number of possible space-separated arguments that
     * follow it.
     *
     * @param option The Javadoc option.
     * @return The sum of options (including option name) for a given
     *         Javadoc option that is specific to this doclet.
     */
    static int optionLength(String option)
    {
        option = option.toLowerCase();
        if (option.equals("-author") ||
                option.equals("-notaginfo") ||
                option.equals("-nolinkconversion") ||
                option.equals("-nocomment") ||
                option.equals("-nodeprecated") ||
                option.equals("-nodeprecatedlist") ||
                option.equals("-nohelp") ||
                option.equals("-noindex") ||
                option.equals("-nonavbar") ||
                option.equals("-noqualifier") ||
                option.equals("-nosince") ||
                option.equals("-notimestamp") ||
                option.equals("-keywords") ||
                option.equals("-notree"))
        {
            return 1;
        }
        if (option.equals("-d") ||
                option.equals("-tagliblink") ||
                option.equals("-tldpath") ||
                option.equals("-windowtitle") ||
                option.equals("-xmldir") ||
                option.equals("-contentsource") ||
                // Options to match the standard doclet.
                option.equals("-destdir") ||
                option.equals("-link") ||
                option.equals("-doctitle") ||
                option.equals("-tag") ||
                option.equals("-windowtitle") ||
                option.equals("-header") ||
                option.equals("-footer") ||
                option.equals("-bottom") ||
                /*
                 * These are the standard doclet options that this doclet doesn't
                 * support. They are handled here in a perfunctory way so that a
                 * message about support can be printed when Javadoc calls the
                 * validOptions method.
                 */
                option.equals("-charset") ||
                option.equals("-docencoding") ||
                option.equals("-docfilessubdirs") ||
                option.equals("-excludedocfilessubdir") ||
                option.equals("-group") ||
                option.equals("-helpfile") ||
                option.equals("-linksource") ||
                option.equals("-serialwarn") ||
                option.equals("-splitindex") ||
                option.equals("-stylesheetfile") ||
                option.equals("-taglet") ||
                option.equals("-tagletpath") ||
                option.equals("-title") ||
                option.equals("-use") ||
                option.equals("-version"))
        {
            return 2;
        } else if (option.equals("-linkoffline") ||
                option.equals("-tagliblinkoffline"))
        {
            return 3;
        } else
        {
            return 0;
        }
    }

    /**
     * Validates the doclet-specific options provided to Javadoc for this run.
     * <p/>
     * Because the user may not know which Javadoc options they've been using
     * are for Javadoc in general and which are specific to the standard doclet,
     * this method prints messages for the standard doclet options that the user
     * specified but that this doclet doesn't support. In some cases, these
     * options are irrelevant here because they are presentation-specific
     * (and so would be handled during transformation from XML output to HTML).
     * Note that this method returns true even for invalid-but-benign options;
     * no sense halting if no harm's done.
     *
     * @param options  The Javadoc options specified by the user.
     * @param reporter An error reporter with which to report messages.
     * @return <code>true</code> if the option is valid; otherwise <code>false</code>.
     */
    static boolean validOptions(String[][] options,
                                DocErrorReporter reporter)
    {
        // A flag to wave if we found an option that should halt the current run.
        boolean allOptionsValid = true;
        // To report errors to the console.
        m_errorReporter = reporter;
        // Flags to ensure that required directories were specified.
        boolean destDirSpecified = false;
        boolean xmlDirSpecified = false;
        boolean tldPathSpecified = false;
        // Go through the options, checking each one.
        for (int i = 0; i < options.length; ++i)
        {
            final String[] optionSet = options[i];
            final String optionName = optionSet[0].toLowerCase();
            if (optionName.equals("-d") || optionName.equals("destdir"))
            {
                final File dirFile = new File(optionSet[1]);
                if (!dirFile.exists())
                {
                    dirFile.mkdir();
                }
                destDirSpecified = true;
            } else if (optionName.equals("-xmldir"))
            {
                final File dirFile = new File(optionSet[1]);
                if (!dirFile.exists())
                {
                    dirFile.mkdir();
                }
                xmlDirSpecified = true;
            } else if (optionName.equals("-tldpath"))
            {
                tldPathSpecified = true;
                final String tldPath = optionSet[1];
                String[] tldFilePaths = tldPath.split(File.pathSeparator);
                for (int j = 0; j < tldFilePaths.length; j++)
                {
                    final File tldFile = new File(tldFilePaths[j]);
                    if (!tldFile.exists() && !tldFile.canRead())
                    {
                        reporter.printError("The file at " + tldFile.getPath() +
                                " could not be read.");
                        allOptionsValid = false;
                        break;
                    }
                }
            } else if (optionName.equals("-tag"))
            {
                continue;
            } else if (optionName.equals("-contentsource"))
            {
                final String[] sourceOptions = optionSet[1].split(";");
                for (int j = 0; j < sourceOptions.length; j++)
                {
                    if (!(sourceOptions[j].equals("tld") || 
                            sourceOptions[j].equals("jsptagref") ||
                            sourceOptions[j].equals("standard")))
                    {
                        reporter.printError("The " + sourceOptions[j] +
                                " is not valid for the " + optionName +
                                " option; valid values include 'jsptagref', 'tld', " +
                                " and/or 'standard'.");
                        allOptionsValid = false;
                        break;
                    }
                }
            } else if (optionName.equals("-author"))
            {
                continue;
            } else if (optionName.equals("-header"))
            {
                continue;
            } else if (optionName.equals("-footer"))
            {
                continue;
            } else if (optionName.equals("-noindex"))
            {
                continue;
            } else if (optionName.equals("-nonavbar"))
            {
                continue;
            } else if (optionName.equals("-keywords"))
            {
                continue;
            } else if (optionName.equals("-nosince"))
            {
                continue;
            } else if (optionName.equals("-notimestamp"))
            {
                continue;
            } else if (optionName.equals("-bottom"))
            {
                continue;
            } else if (optionName.equals("-nolinkconversion"))
            {
                continue;
            } else if (optionName.equals("-notaginfo"))
            {
                continue;
                /*
                 * The standard doclet options below aren't supported. Some may have a
                 * future here; others aren't relevant for a doclet that generates XML
                 * for JSP tag library references.
                 */
            } else if (optionName.equals("-charset"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-docencoding"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-docfilessubdirs"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-excludedocfilessubdir"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-group"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-helpfile"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-linksource"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-nocomment"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-nodeprecated"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-nodeprecatedlist"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-nohelp"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-noqualifier"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-notree"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-serialwarn"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-splitindex"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-stylesheetfile"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-taglet"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-tagletpath"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-title"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-use"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            } else if (optionName.equals("-version"))
            {
                reporter.printNotice("You used the " + optionName + " option, which this " +
                        "doclet does not currently support");
            }
        }
        /*
         * If we get to the end and the required directories aren't specified,
         * return false to stop this Javadoc run.
         */
        if (!tldPathSpecified)
        {
            reporter.printError("You didn't include the -tldpath option " +
                    "to indicate where TLD files could be found.");
            allOptionsValid = false;
        }
        if (!destDirSpecified && !xmlDirSpecified)
        {
            reporter.printError("You must specified the -xmldir option if you want" +
                    "XML, or -d or -destdir option if you want HTML. You did not" +
                    "specify either.");
            allOptionsValid = false;
        }
        return allOptionsValid;
    }

    /**
     * Retrieves from <em>root</em> doclet-specific options specified 
     * by the user and applies them in ways relevant to the doclet.
     *
     * @param root The instance received from Javadoc; used to retrieve
     *             command-line options set by the user.
     */
    private void setOptions(RootDoc root)
    {
        final String[][] options = root.options();
        final LinkedHashSet customTagStrs = new LinkedHashSet();
        for (int i = 0; i < options.length; ++i)
        {
            final String[] option = options[i];
            final String optionName = option[0].toLowerCase();
            if (optionName.equals("-link"))
            {
                final String linkUrl = option[1];
                m_linker.addUrl(linkUrl, linkUrl, false);
            } else if (optionName.equals("-linkoffline"))
            {
                final String linkUrl = option[1];
                final String listUrl = option[2];
                m_linker.addUrl(linkUrl, listUrl, true);
            } else if (optionName.equals("-windowtitle"))
            {
                m_windowTitle = option[1];
            } else if (optionName.equals("-doctitle"))
            {
                m_docTitle = option[1];
            } else if (optionName.equals("-header"))
            {
                m_header = option[1];
            } else if (optionName.equals("-footer"))
            {
                m_footer = option[1];
            } else if (optionName.equals("-bottom"))
            {
                m_bottom = option[1];
            } else if (optionName.equals("-nonavbar"))
            {
                m_noNavbar = "true";
            } else if (optionName.equals("-author"))
            {
                m_author = "true";
            } else if (optionName.equals("-keywords"))
            {
                m_keywords = "true";
            } else if (optionName.equals("-noindex"))
            {
                m_noIndex = "true";
            } else if (optionName.equals("-notaginfo"))
            {
                m_noTagInfo = "true";
            } else if (optionName.equals("-nolinkconversion"))
            {
                m_noLinkConversion = "true";
            } else if (optionName.equals("-tagliblink"))
            {
                final String linkUrl = option[1];
                m_linker.addJspUrl(linkUrl, linkUrl, true);
            } else if (optionName.equals("-tagliblinkoffline"))
            {
                final String linkUrl = option[1];
                final String listUrl = option[2];
                m_linker.addJspUrl(linkUrl, listUrl, true);
            } else if (optionName.equals("-tag"))
            {
                customTagStrs.add(option);
            } else if (optionName.equals("-destdir") || optionName.equals("-d"))
            {
                m_destDir = ensureTrailingSlash(option[1]);
            } else if (optionName.equals("-xmldir"))
            {
                m_xmlDir = ensureTrailingSlash(option[1]);
            } else if (optionName.equals("-tldpath"))
            {
                final String tldPath = option[1];
                String[] tldFilePaths = tldPath.split(File.pathSeparator);
                final ArrayList fileList = new ArrayList();
                for (int j = 0; j < tldFilePaths.length; j++)
                {
                    final File tldFile = new File(tldFilePaths[j]);
                    if (tldFile.isDirectory())
                    {
                        final File[] containedTldFiles = tldFile.listFiles(new TldFileFilter());
                        if (null != containedTldFiles)
                        {
                            for (int k = 0; k < containedTldFiles.length; k++)
                            {
                                fileList.add(containedTldFiles[k]);
                            }
                        }
                    } else
                    {
                        fileList.add(tldFile);
                    }
                }
                m_tldFiles =
                        (File[]) fileList.toArray(new File[fileList.size()]);
            } else if (optionName.equals("-contentsource"))
            {
                final String[] sourceOptions = option[1].split(";");
                for (int j = 0; j < sourceOptions.length; j++)
                {
                    if (sourceOptions[j].equals("tld"))
                    {
                        m_tldContent = true;
                    }
                    if (sourceOptions[j].equals("jsptagref"))
                    {
                        m_jspTagRefContent = true;
                    }
                    if (sourceOptions[j].equals("standard"))
                    {
                        m_standardContent = true;
                    }
                }
            }
        }
        if (!m_tldContent && !m_jspTagRefContent && !m_standardContent)
        {
            m_tldContent = true;
        }
        m_tagletManager = initTagletManager(customTagStrs);
    }

    /**
     * Initializes the taglet manager, which is responsible for keeping
     * track of taglets that generate XML output for Javadoc tags. See
     * {@link org.apache.beehive.netui.tools.doclet.taglets.XmlTagletManager}
     * for more information on the taglet manager.
     * <p/>
     * Note that this doclet does not yet support externally-implemented taglets,
     * nor does it override existing taglets. See
     * {@link org.apache.beehive.netui.tools.doclet.taglets.XmlTagletManager}
     * and {@link org.apache.beehive.netui.tools.doclet.taglets.XmlTaglet} for
     * more information.
     *
     * @param customTagStrs The -tag options and their values.
     */
    private XmlTagletManager initTagletManager(Set customTagStrs)
    {
        final XmlTagletManager tagletManager = new XmlTagletManager(this);
        StringTokenizer tokenizer;
        final Iterator it = customTagStrs.iterator();
        String[] args;
        while (it.hasNext())
        {
            args = (String[]) it.next();
            final String substitute = "slshcln";
            String tagName;
            String tagScope = "";
            String tagHeader = "";
            String argValue = args[1];
            if (0 < argValue.indexOf("\\:"))
            {
                argValue = argValue.replaceFirst("\\\\:", substitute);
                tokenizer = new StringTokenizer(argValue, ":", false);
                tagName = tokenizer.nextToken();
                tagName = tagName.replaceFirst(substitute, ":");
            } else
            {
                tokenizer = new StringTokenizer(argValue, ":", false);
                tagName = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreTokens())
            {
                tagScope = tokenizer.nextToken();
            }
            if (tokenizer.hasMoreTokens())
            {
                tagHeader = tokenizer.nextToken();
            }
            tagletManager.addCustomTag(tagName, tagScope, tagHeader);
        }
        return tagletManager;
    }

    /**
     * Ensures that there's a trailing slash on <em>path</em>. If there isn't
     * one, this method adds one.
     *
     * @param path The path to check for a slash.
     * @return The path with a trailing slash.
     */
    private static String ensureTrailingSlash(String path)
    {
        if (!path.endsWith("/"))
        {
            path += "/";
        }
        return path;
    }
}
