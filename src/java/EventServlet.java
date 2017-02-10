import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ProcessFeatures.SentimentClassifier;
import ProcessFeatures.SerializationUtil;
import ProcessFeatures.PeakFinding_SentiAnalysis;
import twitter4j.TwitterException;

/**
 *
 * 
 * @author Moses
 */
@WebServlet(urlPatterns = {"/TestServlet"})
public class EventServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private HashMap<String, Double> idf;
    private HashMap<Long, HashMap<String, Integer>> postingMap;
    private SentimentClassifier classifier;
    private PeakFinding_SentiAnalysis help;

    @Override
    public void init() throws ServletException {
        help = new PeakFinding_SentiAnalysis();
        try {
            classifier = new SentimentClassifier();
            idf = (HashMap<String, Double>) SerializationUtil.deserialize("idf.dat");
            postingMap = (HashMap<Long, HashMap<String, Integer>>) SerializationUtil.deserialize("postings.dat");
        } catch (IOException ex) {
            Logger.getLogger(EventServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EventServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnknownHostException, TwitterException, ParseException, Exception {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            Date startDate = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH).parse(request.getParameter("startDate") + " " + request.getParameter("startTime"));
            Date endDate = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.ENGLISH).parse(request.getParameter("endDate") + " " + request.getParameter("endTime"));
            if (startDate.getTime() - endDate.getTime() >= 0) {

                String redirectURL = "http://localhost:8084/Tweeter_application/errorPage.html";
                response.sendRedirect(redirectURL);
                return;
            }
            if (((String) request.getParameter("type")).equals("event")) {
                String[] array = help.stringOfPeaks(startDate, endDate, Integer.parseInt(request.getParameter("topKWords")), Integer.parseInt(request.getParameter("topKUrls")), Integer.parseInt(request.getParameter("topKTweets")),
                        Integer.parseInt(request.getParameter("Duration")), idf, postingMap);

                request.setAttribute("array", array);
                request.getRequestDispatcher("/eventResults.jsp").forward(request, response);
                array = null;
            } else {

                String[] array = help.sentiStream(startDate, endDate,
                        Integer.parseInt(request.getParameter("Duration")),
                        classifier);

                request.setAttribute("array", array);
                request.getRequestDispatcher("/sentiResults.jsp").forward(request, response);

            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnknownHostException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(EventServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnknownHostException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(EventServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
