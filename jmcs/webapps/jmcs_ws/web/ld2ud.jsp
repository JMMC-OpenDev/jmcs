<%@page contentType="text/plain" session="false" pageEncoding="UTF-8" buffer="1kb" autoFlush="false"%>
<%@page import="fr.jmmc.mcs.astro.ALX"%>
# Read the http://www.jmmc.fr/doc/index.php?search=JMMC-MEM-2610-0001
# reference document to get more details on diameters conversions
<%
            try {
                // collect arguments value
                String sptype = request.getParameter("sptype");
                String ldStr = request.getParameter("ld");
                if (ldStr != null && ldStr.length() >= 1) {
                    double ld = Double.parseDouble(ldStr);
                    if (sptype != null && sptype.length() >= 1) {
                        out.println("# Diameters for spectral type=" + sptype
                                + " and limb darkened diameter=" + ldStr);
                        String res = ALX.ld2ud(ld, sptype).toString();
                        out.println(res);
                        return;
                    }
                }
            } catch (java.text.ParseException pe) {
                // Alx C code use ERROR line to check properly the response
                out.println("ERROR - " + pe.getMessage());
                return;
            }
            out.println("# Please give ld and sptype arguments");
%>
