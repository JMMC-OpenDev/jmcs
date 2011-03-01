<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JMCS webservices page</title>
    </head>
    <body>
        <h1>Welcome onto the jmcs web services</h1>
        <p>The following services are deployed as basic HTTP Get/Post webservices.There is </p>

        <h2>Catalog list</h2>
        <p>You can get the <a href="catalogs.jsp">list of catalogs</a>
          used by <a href="http://www.jmmc.fr/searchcal">SearchCal</a></p>

        <h2>Limb darkened to uniform diameter calculator</h2>
        <p>
          Use following form to get the uniform diameters from the given spectral type and limb darkened diameter.
        </p>
        <form action="./ld2ud.jsp" method="GET">
          <label>limb darkening diameter:</label><input type="text" name="ld" value=""/><br/>
          <br/>
          <label>spectral type:</label><input type="text" name="sptype"/><br/>
          <!-- it used to be one service with additional parameters ... or <br/>
          <label>Logg:</label><input type="text" name="logg" value=""/><br/>
          <label>Teff:</label><input type="text" name="teff"/><br/>
          -->
          <input type="submit"/>
        </form>
        
</body>
</html>
