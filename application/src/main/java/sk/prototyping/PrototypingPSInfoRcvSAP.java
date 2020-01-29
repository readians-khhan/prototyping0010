package sk.prototyping;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/prototypingpsinforcvsap")
public class PrototypingPSInfoRcvSAP extends HttpServlet {

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().write("PSInfo-SAP");
    }
}