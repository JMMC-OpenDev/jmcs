All the data describing your application shall be stored in the XML file named `ApplicationData.xml` (`xsd` schema available in jMCS source tree).

In order to be automatically loaded and used during your application bootstrap, it should be located in a `resource` folder at the same level as your main class deriving from App:
```
yourmodule/src/main/java
`-- com
    `-- company
        `-- pkg1
            `-- pkg2
                |-- YourMainClass.java
                `-- resource
                    |-- AppIcon.jpg
                    `-- ApplicationData.xml
```

`ApplicationData.xml` contains information about :
* your company description for short and long names, logo, homepage, feedback form (mandatory);
* your application name, version number, description and author list (mandatory), used for the generic About Box, and so on;
* Your application release notes and change log (mandatory);
* Your application [external dependencies](#ExternalDependencies) if any (optional);
* Your application [acknowledgement note](#AcknowledgementNote) (optional, as a generic acknowledgment will be synthesized if none provided).
* Your application [MenuBar menu bar hierarchy](Menu Bar Description) (optional as a standard hierarchy that you can extend is automatically provided, but strongly recommended);

Here is a minimal `ApplicationData.xml` template :
```
<?xml version="1.0" encoding="UTF-8"?>
<ApplicationData
    link="http://www.jmmc.fr/myprogram"
    faqlink="http://www.jmmc.fr/myprogram/faq/"
    rsslink="http://www.jmmc.fr/myprogram/feed.rss">
    <company>
        <short_name>JMMC</short_name>
        <legal_name>Jean-Marie Mariotti Center</legal_name>
        <logo_resource>fr/jmmc/jmcs/resource/image/jmmc_logo.png</logo_resource>
        <homepage_url>http://www.jmmc.fr/</homepage_url>
        <feedback_form_url>http://jmmc.fr/feedback/smprun.php</feedback_form_url>
    </company>
    <program name="MyProgram" version="1.1.02"/>
    <compilation date="11/02/2013" compiler="JDK 1.6"/>
    <text>This description of MyProgram will be shown in the About box.</text> <!-- can be empty -->
    <releasenotes>
        <release version="1.0">
            <prerelease version="1.0b2" tag="V1_0b2">
                <change type="BUGFIX" url="http://www.yourcompany.com/yourbugtracker/ticket/11">Fixed nasty bug.</change>
            </prerelease>
            <prerelease version="1.0b1" tag="V1_0b1">
                <change type="CHANGE">Enhanced core algorithm.</change>
                <change type="FEATURE">Added a great new feature.</change>
            </prerelease>
        </release>
    </releasenotes>
</ApplicationData>
```

All those data are available at runtime through the ```ApplicationDescription``` singleton.

### <a name="ExternalDependencies"></a> External Dependencies : how they are displayed in the About box 
Data from `ApplicationData.xml` is used to automatically display dependencies (if any) in your application About box.

Here is an example of how to add detailed dependencies data in `ApplicationData.xml` :
```
<?xml version="1.0" encoding="UTF-8"?>
<ApplicationData link="http://www.myProgram.com/">
    ...
	<dependences>
        <package name="AppleJavaExtensions" description="toolkit to provide better user interface on Mac, provided by Apple, Inc." link="http://developer.apple.com/samplecode/AppleJavaExtensions/index.html"/>
        <package name="SAVOT" description="SearchCal uses source code created at the Centre de Donnees astronomiques de Strasbourg, France."/>
	</dependences>
    ...
</ApplicationData>
```

### <a name="AcknowledgementNote"></a> Acknowledgement Note : how to provide a specific one for your application
If no acknowledgement note is provided in `ApplicationData.xml`, a generic one will be synthesized instead.

Here is an example of how to add an acknowledgement note in `ApplicationData.xml` :
```
<?xml version="1.0" encoding="UTF-8"?>
<ApplicationData link="http://www.myProgram.com/">
    ...
    <acknowledgment>
        <![CDATA[@INPROCEEDINGS{2008SPIE.7013E..44T,
   author = {{Tallon-Bosc}, I. and {Tallon}, M. and {Thi{\'e}baut}, E. and 
	{B{\'e}chet}, C. and {Mella}, G. and {Lafrasse}, S. and {Chesneau}, O. and 
	{Domiciano de Souza}, A. and {Duvert}, G. and {Mourard}, D. and 
	{Petrov}, R. and {Vannier}, M.},
    title = "{LITpro: a model fitting software for optical interferometry}",
booktitle = {Society of Photo-Optical Instrumentation Engineers (SPIE) Conference Series},
     year = 2008,
   series = {Presented at the Society of Photo-Optical Instrumentation Engineers (SPIE) Conference},
   volume = 7013,
    month = jul,
      doi = {10.1117/12.788871},
   adsurl = {http://cdsads.u-strasbg.fr/abs/2008SPIE.7013E..44T},
  adsnote = {Provided by the SAO/NASA Astrophysics Data System}
}]]>
   </acknowledgment>
    ...
</ApplicationData>
```