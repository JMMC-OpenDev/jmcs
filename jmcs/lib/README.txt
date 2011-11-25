This folder contains all java libraries used by the JMCS module.

All of them should be listed in the modules's ApplicationData.xml


You should be able to use the following xmlstarlet tips to generate your jnlp
xml sel -t -m "//package" -c "./preceding-sibling::comment()[1]"  -e jar -a href -o "@SHARED@/" -v "./@jars" -b -b -n -n fr/jmmc/jmcs/resource/ApplicationData.xml
by this way they will be properly be handled by the jmcsDeployJnlp script.


Runtime Libraries (JNLP) are described in the following files :
../src/fr/jmmc/mcs/gui/ApplicationData.xml 
../src/Jmcs.jnlp

-rw-r--r--. 1 bourgesl laogsite  124332 27 avril  2011 activation.jar
-rwxr-xr-x. 1 bourgesl laogsite    4189 27 avril  2011 AppleJavaExtensions.jar
-rw-r--r--. 1 bourgesl laogsite  102603 27 avril  2011 BrowserLauncher2-1_3.jar
-rw-r--r--. 1 bourgesl laogsite   46725 27 avril  2011 commons-codec-1.3.jar
-rw-r--r--. 1 bourgesl laogsite  305001 27 avril  2011 commons-httpclient-3.1.jar
-rw-r--r--. 1 bourgesl laogsite  284220 27 avril  2011 commons-lang-2.6.jar
-rw-r--r--. 1 bourgesl laogsite   60841 27 avril  2011 commons-logging-1.1.1.jar
-rw-r--r--. 1 bourgesl laogsite   56709 27 avril  2011 java-getopt-1.0.13.jar
-rw-r--r--. 1 bourgesl laogsite  105134 27 avril  2011 jaxb-api.jar
-rw-r--r--. 1 bourgesl laogsite  890168 27 avril  2011 jaxb-impl.jar
-rw-r--r--. 1 bourgesl laogsite  562814 13 sept. 15:08 jhall.jar
-rw-r--r--. 1 bourgesl laogsite  686801 15 sept. 09:15 jsamp-1.3.jar
-rw-r--r--. 1 bourgesl laogsite   45539 27 avril  2011 jsr173_1.0_api.jar
-rw-r--r--. 1 bourgesl laogsite  124741 27 avril  2011 loggui.jar
-rw-r--r--. 1 bourgesl laogsite   12852 27 avril  2011 swing-worker-1.2.jar


Libraries used by shell scripts (build) are defined in :
../src/Makefile

-rw-r--r--. 1 mella mella 448K 15 mai    2008 jhelpdev.jar
-rw-r--r--. 1 mella mella 174K 20 mai    2008 Tidy.jar
-rw-r--r-- 1 mella laogsite   14204 2011-02-15 18:08 xmlenc.jar


Libraries used by ant scripts (JAXB XJC) are :
-rw-r--r--. 1 mella mella 3,0M 29 janv.  2010 jaxb-xjc.jar
-rw-r--r--. 1 mella mella 5,7K 29 janv.  2010 simple-regenerator-1.0.jar


---
