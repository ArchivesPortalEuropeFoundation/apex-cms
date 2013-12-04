package eu.archivesportaleurope.portal.servlets;

import eu.apenet.persistence.factory.DAOFactory;
import eu.apenet.persistence.vo.DptUpdate;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * User: Yoann Moranville
 * Date: 23/05/2012
 *
 * @author Yoann Moranville
 */
public class DptUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 7217524011135048220L;
    private static final Logger LOG = Logger.getLogger(DptUpdateServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String versionNb = request.getParameter("versionNb");
            DptUpdate dptUpdate = DAOFactory.instance().getDptUpdateDAO().doesVersionExist(versionNb);
            if(StringUtils.isEmpty(versionNb) || dptUpdate == null) {
                LOG.info("No update for " + versionNb);
                response.sendError(404);
            } else {
                LOG.info("Need update for " + versionNb + " with version " + dptUpdate.getNewVersion());
                response.setHeader("Content-Type", "text/plain");
                response.setHeader("success", "yes");
                PrintWriter writer = response.getWriter();
                writer.write(dptUpdate.getNewVersion());
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            LOG.error("error...", e);
        }
    }
}