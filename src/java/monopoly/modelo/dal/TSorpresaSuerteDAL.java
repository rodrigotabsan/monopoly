/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monopoly.modelo.dal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import monopoly.modelo.entidades.TSorpresaSuerte;
import monopoly.modelo.ITSorpresaSuerteDAL;
import monopoly.util.UtilesXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilizado para acceder al fichero XML que contiene los datos de las tarjetas de caja de comunidad
 * y suerte.
 * @author Rodrigo
 */
public class TSorpresaSuerteDAL implements ITSorpresaSuerteDAL{

    /**
     *
     */
    public TSorpresaSuerteDAL() {
    }
    
    /**
     * Obtiene el valor del nodo. Básicamente coge del tag o pestaña que se le pase,
     * en el parámetro strTag, el valor que le corresponde en el fichero XML.
     * @param strTag La etiqueta a la que se quiere acceder
     * @param eTSorpresaSuerte La TSorpresaSuerte a la que se quiere acceder
     * @return 
     */     
    private static String obtenerNodoValor(String strTag, Element eTSorpresaSuerte){
         Node nValor=(Node)eTSorpresaSuerte.getElementsByTagName(strTag).item(0).getFirstChild();
         return nValor.getNodeValue();
    }  
    
    /**
     * Obtiene un listado de todas las tarjetas de Caja de Comunidad y Suerte.
     * @return El listado de TSorpresaSuerte 
     * @see TSorpresaSuerte
     */
    @Override
    public List<TSorpresaSuerte> obtenerTodasTsSorpresaSuerte(){
        List<TSorpresaSuerte> listaTsSorpresaSuerte= new ArrayList<TSorpresaSuerte>();
        UtilesXML util = new UtilesXML(new File("xml/tsorpresasuerte.xml"));
           
         NodeList nodosTsSorpresaSuerte = util.accesoAXML("tarjeta");
         
         for(int i = 0; i < nodosTsSorpresaSuerte.getLength(); i++){
             Node tsorepsasuerte = nodosTsSorpresaSuerte.item(i);
             if(tsorepsasuerte.getNodeType()== Node.ELEMENT_NODE){
                 Element unElemento = (Element) tsorepsasuerte;                 
                 TSorpresaSuerte objTSorpresaSuerte = new TSorpresaSuerte();
                 objTSorpresaSuerte.setId(Integer.parseInt(obtenerNodoValor("id", unElemento)));
                 objTSorpresaSuerte.setBonus(Integer.parseInt(obtenerNodoValor("bonus", unElemento)));
                 objTSorpresaSuerte.setDescripcion(obtenerNodoValor("descripcion", unElemento));
                 objTSorpresaSuerte.setTipo(obtenerNodoValor("tipo", unElemento));
                 
                 listaTsSorpresaSuerte.add(objTSorpresaSuerte);
             }
         }  
        return listaTsSorpresaSuerte;
    }
    
    /**
     * Obtiene un listado de todas las tarjetas de Caja de Comunidad y Suerte.
     * @param partida String de una partida guardada
     * @return El listado de TSorpresaSuerte 
     * @see TSorpresaSuerte
     */
    @Override
    public List<TSorpresaSuerte> obtenerTodasTsSorpresaSuerte(String partida){
        List<TSorpresaSuerte> listaTsSorpresaSuerte= new ArrayList<TSorpresaSuerte>();
        UtilesXML util = new UtilesXML(new File("xml/"+partida+"/tsorpresasuerte.xml"));
           
         NodeList nodosTsSorpresaSuerte = util.accesoAXML("tarjeta");
         
         for(int i = 0; i < nodosTsSorpresaSuerte.getLength(); i++){
             Node tsorepsasuerte = nodosTsSorpresaSuerte.item(i);
             if(tsorepsasuerte.getNodeType()== Node.ELEMENT_NODE){
                 Element unElemento = (Element) tsorepsasuerte;                 
                 TSorpresaSuerte objTSorpresaSuerte = new TSorpresaSuerte();
                 objTSorpresaSuerte.setId(Integer.parseInt(obtenerNodoValor("id", unElemento)));
                 objTSorpresaSuerte.setBonus(Integer.parseInt(obtenerNodoValor("bonus", unElemento)));
                 objTSorpresaSuerte.setDescripcion(obtenerNodoValor("descripcion", unElemento));
                 objTSorpresaSuerte.setTipo(obtenerNodoValor("tipo", unElemento));
                 
                 listaTsSorpresaSuerte.add(objTSorpresaSuerte);
             }
         }  
        return listaTsSorpresaSuerte;
    }
    
    /**
     * Guardo las tarjetasSorpresaSuerte
     * @param fichero fichero donde lo guardo
     * @param tarjetasSorpresaSuerte lista de tarjetasSorpresaSuerte
     */
    @Override
    public void guardarITSorpresaSuerte(String fichero, List<TSorpresaSuerte> tarjetasSorpresaSuerte) {
        UtilesXML util = new UtilesXML(new File(fichero));
                
             Document doc =util.accesoAlDocument();

             Node nodoRaiz = util.accesoANodoXML();          
             
             for(TSorpresaSuerte tSorpresaSuerte:tarjetasSorpresaSuerte){
                 
                Element nuevaTarjeta = doc.createElement("tarjeta");
                
                Element nuevoId = doc.createElement("id"); 
                nuevoId.setTextContent(String.valueOf(tSorpresaSuerte.getId()));
                 
                Element nuevoTipo = doc.createElement("tipo"); 
                nuevoTipo.setTextContent(String.valueOf(tSorpresaSuerte.getTipo()));

                Element nuevoDescripcion = doc.createElement("descripcion");
                nuevoDescripcion.setTextContent(String.valueOf(tSorpresaSuerte.getDescripcion()));
                
                Element nuevoBonus = doc.createElement("bonus");
                nuevoBonus.setTextContent(String.valueOf(tSorpresaSuerte.getBonus()));
                
                nuevaTarjeta.appendChild(nuevoId);
                nuevaTarjeta.appendChild(nuevoTipo);
                nuevaTarjeta.appendChild(nuevoDescripcion);
                nuevaTarjeta.appendChild(nuevoBonus);
                
                nodoRaiz.appendChild(nuevaTarjeta);     

                util.modificarOEliminarElementoXML(new File(fichero), doc);    
             }
    }
}
