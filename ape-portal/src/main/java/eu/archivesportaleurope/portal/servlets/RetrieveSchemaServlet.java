package eu.archivesportaleurope.portal.servlets;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * User: Yoann Moranville
 * Date: 23/05/2012
 *
 * @author Yoann Moranville
 */
public class RetrieveSchemaServlet extends HttpServlet {

    private static final long serialVersionUID = -3519450891987005765L;

    private static final Logger LOG = Logger.getLogger(RetrieveSchemaServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        Writer writer = null;
        try {
            String requestURI = request.getRequestURI();
            String schema = requestURI.substring(requestURI.lastIndexOf("/"));
            if(schema != null) {
                InputStream inputStream = RetrieveSchemaServlet.class.getResourceAsStream(schema);
                if(inputStream == null) {
                    response.sendError(404);
                } else {
                    writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
                    Reader reader = new InputStreamReader(inputStream);
                    IOUtils.copy(reader, writer);
                    reader.close();
                    writer.close();
                }
            }
        } catch (Exception e) {
            if(writer != null)
                try {
                    writer.close();
                } catch (IOException e1) {}
            LOG.error("error...", e);
        }
    }
}
