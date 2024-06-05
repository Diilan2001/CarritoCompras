package org.dilan.sesiones.carritocompras;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        // creamos la sesion para enviar productos
        HttpSession session = req.getSession();
        //creamos una lista para el articulo y el valor
        List<String> articulo = (List<String>) session.getAttribute("articulo");
        List<Double> valores = (List<Double>) session.getAttribute("valor");
        List<Double> valorUni = (List<Double>) session.getAttribute("cantidad");

        //creamos una condicion para saber si la lista esta vacia
        if (articulo == null) {
            //inicializamos la lista
            articulo = new ArrayList<>();
            session.setAttribute("articulo", articulo);
        }

        // creamos una segunda codicon para llenar la lista
        if (valores == null) {
            // inicializamos el valor
            valores = new ArrayList<>();
            session.setAttribute("valor", valores);
        }

        //creamos una tercera condicion para saber si el valor unitario
        if (valorUni == null) {
            //inicializamos el valorunitario
            valorUni = new ArrayList<>();
            session.setAttribute("cantidad", valorUni);
        }

        //procesamos el nuevo articulo
        String articuloNuevo = req.getParameter("articulo");
        double valorNuevo = 0;
        double cantidadNueva = 0;

        try {
            valorNuevo = Double.parseDouble(req.getParameter("valor"));
            cantidadNueva = Double.parseDouble(req.getParameter("cantidad"));
        } catch (NumberFormatException e) {
            // Manejar el caso de valores inválidos
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valor o cantidad no son números válidos.");
            return;
        }

        //realizaremos una nueva condicion para verificar si el nuevo producto esta vacio y que el articulo no tenga espacio
        if (articuloNuevo != null && !articuloNuevo.trim().equals("") && valorNuevo != 0) {
            valores.add(valorNuevo);
            articulo.add(articuloNuevo);
            valorUni.add(cantidadNueva);
        }

        //calculamos el subtotal y el iva al 15%
        double subtotal = 0;
        double total = 0;
        double acusub = 0;
        double iva = 0.15;

        //imprimimos la lista utilizamos el try para el control de errores
        try (PrintWriter out = resp.getWriter()) {
            out.print("<!DOCTYPE html>");
            out.print("<html>");
            out.print("<head>");
            out.print("<title>Factura</title>");
            out.print("<style>");
            out.print("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }");
            out.print(".container { background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); width: 80%; margin: auto; }");
            out.print("h1 { text-align: center; color: #800020; }"); // Color burdeos
            out.print("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.print("th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.print("th { background-color: #800020; color: white; }"); // Fondo burdeos y texto blanco
            out.print("td:last-child { text-align: right; }");
            out.print(".total-row { font-weight: bold; }");
            out.print(".total-row td { background-color: #f9f9f9; }");
            out.print(".back-link { display: block; text-align: center; margin-top: 20px; text-decoration: none; color: #800020; font-weight: bold; }");
            out.print("</style>");
            out.print("</head>");
            out.print("<body>");
            out.print("<div class=\"container\">");
            out.print("<h1>FACTURA DE COMPRA</h1>");
            out.print("<table>");
            out.print("<tr><th>Articulo</th><th>PRECIO UNITARIO</th><th>CANTIDAD</th><th>SUBTOTAL</th></tr>");

            for (int i = 0; i < articulo.size(); i++) {
                subtotal = valores.get(i) * valorUni.get(i);
                out.print("<tr>");
                out.print("<td>" + articulo.get(i) + "</td>");
                out.print("<td>$" + valores.get(i) + "</td>");
                out.print("<td>" + valorUni.get(i) + "</td>");
                out.print("<td>$" + subtotal + "</td>");
                out.print("</tr>");
                acusub += subtotal;
            }

            double ivafin = acusub * iva;
            total = ivafin + acusub;

            out.print("<tr class=\"total-row\"><td colspan=\"3\">Subtotal:</td><td>$" + acusub + "</td></tr>");
            out.print("<tr class=\"total-row\"><td colspan=\"3\">IVA 15%:</td><td>$" + ivafin + "</td></tr>");
            out.print("<tr class=\"total-row\"><td colspan=\"3\">Total:</td><td>$" + total + "</td></tr>");
            out.print("</table>");
            out.print("<a class=\"back-link\" href='index.html'>VOLVER AL INICIO</a>");
            out.print("</div>");
            out.print("</body>");
            out.print("</html>");
        }
    }
}