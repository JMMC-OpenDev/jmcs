<%-- 
Document   : index
Created on : 11 janv. 2010, 23:08:48
Author     : mella
--%>
<%@page contentType="text" session="false" pageEncoding="UTF-8"%>
<%@page import="fr.jmmc.mcs.astro.ALX"%>
# Read the http://www.jmmc.fr/doc/index.php?search=JMMC-MEM-2610-0001
# reference document to get more details on diameters conversions
<%
try{
	// collect arguments value
	String sptype = request.getParameter("sptype");
	String ldStr = request.getParameter("ld");
	if (ldStr != null && ldStr.length() >= 1) {
		double ld = Double.parseDouble(ldStr);
		if (sptype != null && sptype.length() >= 1) {
			out.println("# Diameters for spectral type=" + sptype+
					" and limb darkened diameter=" + ldStr);
			String res = ALX.ld2ud(ld, sptype).toString();
			out.println(res);
			return;
		}
	}
}catch(java.text.ParseException pe){
	out.println("# ERROR "+pe.getMessage());
	return;
}
out.println("# Please give ld and sptype arguments");
%>
