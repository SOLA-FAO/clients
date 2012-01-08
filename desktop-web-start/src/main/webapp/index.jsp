<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="Css/style.css" rel="stylesheet" type="text/css" />
        <title>SOLA Desktop and SOLA Admin Download Page</title>
    </head>
    <body>

        <table width="80%" cellspacing="0" cellpadding="3">
            <tr>
                <td colspan="2">  
                    <h1><img src="Images/SolaLogo.png"/>Welcome to the download for the SOLA Web Start Alpha Release applications.</h1>
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td> <br><br>
                    Solutions for Open Land Administration (SOLA) is an open source software system that aims to make computerized cadastre and registration systems more affordable and more sustainable in developing countries.
                    <br><br>
                    This page will guide you through the installation of the SOLA Web Start Alpha Release applications which includes the SOLA Desktop Web Start and SOLA Admin Web Start. The installation package for both applications is approximately 70Mb.
                    <br><br> 
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td>     <h2>Installation Pre-requisites</h2>
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td>     
                    The  Java SE Runtime Environment 7 is required for the SOLA Web Start applications. Java 7 can be downloaded from <a href="http://www.oracle.com/technetwork/java/javase/downloads/java-se-jre-7-download-432155.html" target="_blank" >here</a>.
                    <ol>
                        <li>    You will need to accept the Oracle Binary Code License Agreement for Java SE before downloading Java 7.</li>
                        <li>    Download the product appropriate for your operating system. Note that the Windows x86 products are for Windows 32bit operating systems and the Windows x64 product is for Windows 64bit operating systems.</li>
                        <li>    You will require local administration privileges to install Java 7.</li>
                    </ol>
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td>    <h2>Installation</h2>
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td>      
                    <ol>
                        <script src="http://www.java.com/js/deployJava.js"></script>  
                        <script type="text/javascript">
                            var SOLAPATH="webstart/sola-desktop.jnlp";   
                            var URL=window.location.href;
                            var SOLADESKTOP=URL+SOLAPATH;
                            document.write("<li>To install the SOLA Desktop web start application, right click this link <a href="+SOLADESKTOP+" target=_blank>SOLA Desktop Web Start</a> and choose Save link as... and save the sola-desktop.jnlp file to a known location on your local file system.</li>");
                        </script>
                        <script type="text/javascript">
                            var SOLAPATH="webstart/sola-admin.jnlp";   
                            var URL=window.location.href;
                            var SOLAADMIN=URL+SOLAPATH;
                            document.write("<li>To install the SOLA Admin web start, right click this link <a href="+SOLAADMIN+" target=_blank>SOLA Admin Web Start</a> and choose Save link as... and save the sola-admin.jnlp file to a known location on your local file system.</li>");
                        </script>
                        <li>	Once one or both of the files have been saved on your file system, locate the appropriate jnlp file using Windows Explorer or equivalent and double click the file. You should see a Java 7 splash displayed followed by the Starting application... dialog. </li>
                        <li>	When prompted with the digital signature security warning, tick Always trust content from this publisher and choose Run. </li>
                        <li>	The web start application you have selected will start automatically. At the login screen, enter User <i><b>test</b></i> and password <i><b>test</b></i>. </li>
                    </ol>
                </td>
            </tr> 
            <tr>
                <td width="20%"></td>   
                <td>
                    <h2>Additional Information</h2>
                    <ol>
                        <li>	The SOLA Alpha Release User Guide provides details on installation troubleshooting. The User Guide is available from <a href="http://www.flossola.org/documents/development-snapshot" target="_blank" >http://www.flossola.org/documents/development-snapshot</a>.</li> 
                        <li>	The installation will install a shortcut to the SOLA Desktop on your computer desktop. This shortcut may require further configuration. Refer to the User Guide for details. </li> 
                        <li>        Refer to the User Guide or the SOLA Desktop Online Help for licensing details.</li>
                        <li>	The Database used for the SOLA Desktop will be reverted daily at 0100 GMT. Any data changes you make will be lost.</li> 
                        <li>	The SOLA Desktop Development Snapshot is an in-progress view of our work to date and includes partially complete functionality as well as some defects. For a list of known
                            issues, refer to the Known Issues section in the User Guide or the SOLA Desktop Online Help. If you encounter an issue or bug that is not listed as a known issue, you can 
                            notify the SOLA Development Team by sending an email to <a href="mailto:Neil.Pullar@fao.org">Neil.Pullar@fao.org</a>.</li>
                    </ol>
                </td>
            </tr> 
        </table>  
    </body>
</html>
