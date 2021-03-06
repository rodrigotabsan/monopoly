/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.controlador.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import monopoly.util.UtilesServlets;

/**
 * Ejecuta el Servlet de Inicio de la aplicación.
 * @author Rodrigo
 */
public class InicioServlet extends HttpServlet{

    /**
     * Procesa la peticion
     * @param request peticion de la pagina
     * @param response respuesta de la pagina
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) { 
        try{
         response.setContentType("text/html;charset=UTF-8");
         UtilesServlets utilServlet = new UtilesServlets();
         utilServlet.eliminarMensajesDeError(request, response); 
         String comenzarPartida=request.getParameter("comenzarPartida");         
         if(comenzarPartida!=null){                
            utilServlet.mostrarVista("./jsp/seleccionarNumJugadores.jsp", request, response);             
         }
         String cargarPartida=request.getParameter("cargarPartida");         
         if(cargarPartida!=null){
             utilServlet.mostrarVista("./jsp/cargarPartida.jsp", request, response);
         }
        }catch(ServletException | IOException ex){
            System.out.println("Error: "+ex);
        }
         
     }
       
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
            throws ServletException, IOException {
        processRequest(request, response);
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
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
