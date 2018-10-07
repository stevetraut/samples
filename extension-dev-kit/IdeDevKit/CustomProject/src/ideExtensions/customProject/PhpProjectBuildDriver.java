package ideExtensions.customProject;

import com.bea.ide.core.ResourceSvc;
import com.bea.ide.util.IOUtil;
import com.bea.ide.workspace.IProject;
import com.bea.ide.workspace.project.DefaultBuildDriver;
import java.io.File;
import java.util.regex.Pattern;

/**
 * A simple build driver for PHP projects. The getAntScript method,
 * which is not implemented in class that this class extends,
 * assembles an Ant build.xml file using a template in this
 * projects and values provided primarily by WebLogic Workshop.
 */
public class PhpProjectBuildDriver extends DefaultBuildDriver
{
    /**
     * Constructs a new driver instance with an instance
     * of the PHP project that will use the driver. This
     * implementation merely calls the super's, which
     * uses the IProject to extract information need to
     * create the build.xml file.
     */
    public PhpProjectBuildDriver(IProject project)
    {
        super(project);
    }

    /**
     * Called by the IDE to retrieve default Ant build
     * file contents when the user clicks "Export to Ant file"
     * in the Build panel of the project properties dialog.
     *
     * Note that the build target in the file created merely
     * zips the projects contents and puts the generated ZIP
     * file at the project's root.
     *
     * @return XML that comprises an Ant build file's contents.
     */
    public String getAntScript()
    {
        /**
         * Create the ZIP file's name from the project name.
         * Note that the _project variable is inherited from
         * DefaultBuildDriver.
         */
        String zipFileName = _project.getName() + ".zip";

        /**
         * Use a utility API to read the contents of the template
         * XML into a string.
         */
        String source = IOUtil.read(
            ResourceSvc.get().getReader("xml/defaultPhpBuild.xml"));

        if (source == null)
            return null;

        /**
         * Call an inherited method to translate the placeholders that
         * correspond to beahome.local.directory, platformhome.local.directory,
         * app.local.directory, and project.local.directory.
         */
        source = expandLocalDirs(source);
        /**
         * Translate the placeholder corresponding to output.filename,
         * replacing it with the name of the project + .zip.
         */
        source = OUTPUT_FILENAME.matcher(source).replaceFirst(zipFileName);

        // Return the template with useful values instead of placeholders.
        return source;
    }
}
