/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.controlador.servlet;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;
import monopoly.modelo.dal.CasillaDAL;
import monopoly.modelo.dal.EspecialDAL;
import monopoly.modelo.dal.JugadorDAL;
import monopoly.modelo.dal.PartidaDAL;
import monopoly.modelo.dal.PropiedadDAL;
import monopoly.modelo.dal.TSorpresaSuerteDAL;
import monopoly.modelo.dal.TableroDAL;
import monopoly.modelo.entidades.Casilla;
import monopoly.modelo.entidades.Especial;
import monopoly.modelo.entidades.Jugador;
import monopoly.modelo.entidades.Partida;
import monopoly.modelo.entidades.Propiedad;
import monopoly.modelo.entidades.TSorpresaSuerte;
import monopoly.modelo.entidades.Tablero;
import monopoly.modelo.ICasillaDAL;
import monopoly.modelo.IEspecialDAL;
import monopoly.modelo.IJugadorDAL;
import monopoly.modelo.IPartidaDAL;
import monopoly.modelo.IPropiedadDAL;
import monopoly.modelo.ITSorpresaSuerteDAL;
import monopoly.modelo.ITableroDAL;
import monopoly.util.UtilesServlets;
import monopoly.util.UtilesXML;
/**
 * Utilizado para definir qué haran cada uno de los botones utilizados para crear
 * los jugadores-
 * @author Rodrigo
 */
public class SeleccionarJugadorServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
          throws ServletException, IOException {
        
         response.setContentType("text/html;charset=UTF-8");
        // Recoger el parametro ficha y su numero correspondiente
        String jugadoresHumanos=request.getParameter("jugadoresHumanos");
        if(jugadoresHumanos!=null){
            if(jugadoresHumanos.equals("jugadoresHumanos")){
                enviarJugadoresHumanos(request,response);
            }
        }
        String jugadoresCPU=request.getParameter("CPUName");
        if(jugadoresCPU!=null && !jugadoresCPU.equals("null")){
            if(!jugadoresCPU.isEmpty()){
                crearPartida(request, response);
            }
        } 
        
     }
    
    /**
     * Contiene los objetos necesarios para crear la partida.
     * Además se reenvía a la ventana partida los objetos.
     */
    private void crearPartida(HttpServletRequest request, HttpServletResponse response){
        UtilesXML utilXML= new UtilesXML();                
        try {
            IEspecialDAL casillasEspeciales = new EspecialDAL();
            IPropiedadDAL casillasPropiedades = new PropiedadDAL();
            ICasillaDAL casillas = new CasillaDAL();
            ITSorpresaSuerteDAL tarjetaCCySuerte= new TSorpresaSuerteDAL();
            Tablero tableroNuevo = new Tablero();
            Partida partidaNueva=new Partida();
            
            List <Especial> listaCasillasEspeciales = casillasEspeciales.obtenerTodasEspeciales();
            List <Propiedad> listaCasillasPropiedades = casillasPropiedades.obtenerTodasPropiedades();
            List <Casilla> listaCasillas = casillas.obtenerTodasCasillas();
            List <TSorpresaSuerte> listaTarjetaCCySuerte= tarjetaCCySuerte.obtenerTodasTsSorpresaSuerte();
            
            if(utilXML.crearXML("tableros.xml")){
                tableroNuevo.setId(1);                
            }else{
                ITableroDAL tableros= new TableroDAL();
                
                List<Tablero> listaTableros = tableros.obtenerTodosTableros();
                int idUltimoTablero = 0;
                if(listaTableros.size()>0){
                    tableroNuevo.setId(listaTableros.size());
                    tableroNuevo.setTurno(1);
                }else{
                    tableroNuevo.setId(1);
                    tableroNuevo.setTurno(1);
                    
                }                
            }
            
            if(utilXML.crearXML("partidas.xml")){
                partidaNueva.setId(0);                            
                partidaNueva.setIdTablero(tableroNuevo.getId());
            }else{
                IPartidaDAL partidas= new PartidaDAL();
                
                List<Partida> listaPartidas = partidas.obtenerTodasPartidas();
                
                if(listaPartidas.size()>0){
                    partidaNueva.setId(listaPartidas.size());                    
                    partidaNueva.setIdTablero(tableroNuevo.getId());
                            
                }else{
                    Date fechaHoy=new Date();
                    partidaNueva.setId(1);
                    partidaNueva.setIdTablero(tableroNuevo.getId());
                    partidaNueva.setNombre(fechaHoy.toString());
                }                
            }
            
            //Aquí envío las listas, el tablero y la partida a partida.jsp
                request.getSession().setAttribute("listaEspeciales",listaCasillasEspeciales);
                request.getSession().setAttribute("listaPropiedades",listaCasillasPropiedades);
                request.getSession().setAttribute("listaCasillas",listaCasillas);
                request.getSession().setAttribute("listaTarjetaCCySuerte",listaTarjetaCCySuerte);
                request.getSession().setAttribute("tableroNuevo",tableroNuevo);
                request.getSession().setAttribute("partidaNueva",partidaNueva);
            
            //Aquí termino de crear todos los jugadores, tanto humanos como CPU.
            enviarJugadoresCPU(request,response,partidaNueva);
        } catch (ServletException | IOException ex) {
            Logger.getLogger(SeleccionarJugadorServlet.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
    
    /**
     * Envía los jugadores de tipo CPU a la partida. Si el número de jugadores
     * es superior a 8 devuelve un mensaje y muestra de nuevo la pantalla de 
     * seleccionar jugadores CPU
     * @param request
     * @param response
     * @see enviarJugadoresHumanos
     * @throws ServletException
     * @throws IOException 
     */
    private void enviarJugadoresCPU(HttpServletRequest request,
            HttpServletResponse response, Partida partidaNueva) throws ServletException, IOException {
        UtilesServlets utilServlet= new UtilesServlets();
        utilServlet.eliminarMensajesDeError(request, response);
        String jugadoresHumanosTotales=String.valueOf(request.getSession().getAttribute("numeroTotalJugadoresHumanos"));
        String jugadoresCPUTotales=request.getParameter("CPUName").substring(0, 1);
        
        int intJugadoresHumanosTotales=Integer.valueOf(jugadoresHumanosTotales);
        int intJugadorCPUTotales=Integer.valueOf(jugadoresCPUTotales);
        UtilesXML utilXML = new UtilesXML();
        int totales = intJugadoresHumanosTotales+intJugadorCPUTotales;
        //Si el numero total de jugadores es mayor que 8 devuelvo un mensaje. Si no, continua.
        if(totales>8){
            utilServlet.mensajeErrorNumTotalJugadores(request, response,intJugadorCPUTotales);
            utilServlet.mostrarVista("./jsp/seleccionarNumCPU.jsp", request, response);
        }else{
            /* Obtengo el listado de jugadores humanos que envié al pasar a esta ventana
            con el método setAttribute (ver función enviarJugadoresHumanos) */
            List <Jugador> jugadores=(ArrayList<Jugador>)request.getSession().getAttribute("jugadores");
            int numeroJugadores = 0;
            
            List <String> figuras = new ArrayList<>();
            figuras.add("barco"); figuras.add("sombrero"); figuras.add("dedal"); figuras.add("zapato"); figuras.add("perro");
            figuras.add("coche"); figuras.add("plancha"); figuras.add("carretilla");
            int contadorJugador = 0;
            
            /*recorro la lista de jugadores para conocer la figura que ha escogido
            cada jugador, de tal manera que elimine del listado de figuras,
            aquellas figuras que ya han sido elegidas. De este modo
            el ordenador podrá coger el resto de figuras libres.
            Además me aseguro de que cada jugador corresponde al mismo id de la partida.
            */
            for(int x = 0; x<jugadores.size();x++) {
                Jugador jugador = jugadores.get(x);
                for(int i = 0; i<figuras.size();i++){
                    if(figuras.get(i).equals(jugador.getFigura())){
                        figuras.remove(i);
                    }
                }
                jugador.setIdPartida(partidaNueva.getId());
                jugadores.set(x, jugador);
            }            
            
            /*Almaceno los jugadores de la CPU en la lista*/
            for (int i=1; i<=intJugadorCPUTotales;i++){
                Jugador jugador= new Jugador();
                jugador.setId(intJugadoresHumanosTotales+i);
                jugador.setNombre("CPU "+i);
                jugador.setFigura(figuras.get(i-1));
                jugador.setDinero(1500);
                jugador.setIdCasilla(0);
                jugador.setIdPartida(partidaNueva.getId());
                jugador.setTurno(1);
                jugador.setEstadoTurno(0);
                jugadores.add(jugador);
            }
            IJugadorDAL jugadoresDAL = new JugadorDAL();
            if(!utilXML.crearXML("usuarios.xml")){                                            
                List <Jugador> listaJugadores = jugadoresDAL.obtenerTodosUsuarios();
                for(int i = 0; i<jugadores.size();i++){
                    listaJugadores.add(jugadores.get(i));
                }
                request.getSession().setAttribute("listaJugadoresTotales", jugadores);
            }
            
            request.getSession().setAttribute("listaJugadoresPartida", jugadores);
            utilServlet.mostrarVista("./jsp/partida.jsp", request, response);
        }     
             
    }
    
   /**
     * Envío un listado de los jugadores humanos a seleccionarNumCPU, de tal
     * manera que sepa cuántos jugadores van a jugar.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */ 
    private void enviarJugadoresHumanos(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        UtilesServlets utilServlet= new UtilesServlets();
        utilServlet.eliminarMensajesDeError(request, response);  
        //Según se avance el desarrollo, hay que tener en cuenta que ese ArrayList
        //tiene que cambiar por jugadorDAL.obtenerTodosUsuarios 
        IJugadorDAL jugadorDAL = new JugadorDAL();
        List <Jugador> jugadores = new ArrayList<>();
        int dineroInicial=1500;
        int casillaInicial=0;
        String opcion = request.getParameter("op");        
        int numJugadores=Integer.valueOf(opcion);
        for(int i = 1; i <=numJugadores; i++){
            /*
            Si hay algun dato nulo le devolvemos a la pantalla de elección de jugadores.
            Si no, continúa almacenando los jugadores en la lista.
            */
            if((request.getParameter("ficha"+i)!=null && !request.getParameter("ficha"+i).isEmpty())
                    && (request.getParameter("nombre"+i)!=null && !request.getParameter("nombre"+i).isEmpty())){
                Jugador jugador = new Jugador();
                String ficha = request.getParameter("ficha"+i);
                String nombre = request.getParameter("nombre"+i);
                jugador.setId(i);
                jugador.setFigura(ficha);
                jugador.setNombre(nombre);
                jugador.setIdCasilla(casillaInicial);
                jugador.setDinero(dineroInicial);
                jugador.setEstadoTurno(0);
                jugador.setTurno(1);                
                jugadores.add(jugador);
            }else{
                jugadores.clear();
                utilServlet.mensajeErrorAnhadirJugadoresNulos(request, response);
                utilServlet.mostrarVista("./jsp/seleccionarNumJugadores.jsp", request, response);
            }
        }
        List <String> figuras = new ArrayList<>();
        figuras.add("barco"); figuras.add("sombrero"); figuras.add("dedal"); figuras.add("zapato"); figuras.add("perro");
        figuras.add("coche"); figuras.add("plancha"); figuras.add("carretilla");
        List <String> figurasJugadores= new ArrayList<>();
        //recorro la lista de jugadores y agrego las figuras de los jugadores a un listado
        jugadores.forEach((jugador) -> {
            figurasJugadores.add(jugador.getFigura());
        });
        //comparo las listas de figuras, si una se repite salta un error y me 
        //devuelve otra vez a la ventana de eleccion de jugadores humanos.        
        for(int i = 0; i<figuras.size();i++){
            int figuraRepetida=0;
            for(int j = 0; j<figurasJugadores.size();j++ ){
                if(figurasJugadores.get(j).equals(figuras.get(i))){
                    figuraRepetida++;
                    //Los jugadores empiezan en 1, pero j empieza en 0, con lo cual,
                    //para saber qué jugador es el que ha duplicado ficha, se pone +1
                    //Además limpiamos la lista de jugadores y volvemos a reenviar la dirección
                    //a nuestra página actual.
                    if(figuraRepetida==2){
                        int idJugador=j+1;
                        jugadores.clear();
                        utilServlet.mensajeErrorFigurasJugadoresDuplicadas(request, response, idJugador);                    
                        utilServlet.mostrarVista("./jsp/seleccionarNumJugadores.jsp", request, response);
                                                                
                    }
                }
            }
        }
        //si jugadores humanos es igual a 8 no tenemos que ir a la pantalla de elegir
        //CPU. Sino, vamos a seleccionarNumCPU        
            if(numJugadores==8){
                request.getSession().setAttribute("jugadoresTotales", jugadores);
                utilServlet.mostrarVista("./jsp/partida.jsp", request, response);
            }else{        
                request.getSession().setAttribute("jugadores", jugadores);
                utilServlet.mostrarVista("./jsp/seleccionarNumCPU.jsp", request, response);
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
