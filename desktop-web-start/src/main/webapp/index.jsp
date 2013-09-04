<%@page contentType="text/html" pageEncoding="UTF-8" language="java"%>
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
                    <h1><img src="Images/SolaLogo.png"/>Welcome to the download for the SOLA Web Start applications.</h1>
                </td>
            </tr>
            <tr>
                <td width="20%"></td>   
                <td> <br><br>
                    Solutions for Open Land Administration (SOLA) is an open source software system that aims to make computerized cadastre and registration systems more affordable and more sustainable in developing countries.
                    <br><br>
                    This page will guide you through the installation of the SOLA Web Start applications which includes the SOLA Desktop Web Start and SOLA Admin Web Start. The installation package for both applications is approximately 100Mb.
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
                    The  Java SE Runtime Environment 7 is required for the SOLA Web Start applications. Java 7 can be downloaded from <a href="http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html#jre-7u21-oth-JPR" target="_blank" >here</a>.
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
                        <%
                            // Check the server name to determine which jnlp
                            // files should be made available to the user. If they
                            // are on the production server, allow them to
                            // download the (LIVE) jnlp files, otherwise use the
                            // test jnlp files
                            String hostName = request.getServerName();

                            String desktopJnlp = "/webstart/sola-desktop-test.jnlp";
                            String adminJnlp = "/webstart/sola-admin-test.jnlp";
                            if (hostName.equalsIgnoreCase("localhost")) { // Update hostname as required
                                // This is the production host so use the production
                                // JNLP files
                                desktopJnlp = "/webstart/sola-desktop.jnlp";
                                adminJnlp = "/webstart/sola-admin.jnlp";
                            }
                            String desktopLocation = request.getContextPath() + desktopJnlp;
                            String adminLocation = request.getContextPath() + adminJnlp;
                        %> 
                        <li>To install the <b>SOLA Desktop</b> web start application, right click this link <a href=" <%=desktopLocation%> " target=_blank>SOLA Desktop</a> and choose Save link as... and save the sola-desktop.jnlp file to a known location on your local file system.</li>
                        <li>To install the <b>SOLA Admin</b> web start, right click this link <a href=" <%=adminLocation%> " target=_blank>SOLA Admin</a> and choose Save link as... and save the sola-admin.jnlp file to a known location on your local file system.</li>
                        <li>Once one or both of the files have been saved on your file system, locate the appropriate jnlp file using Windows Explorer or equivalent and double click the file. You should see a Java 7 splash displayed followed by the Starting application... dialog. </li>
                        <li>When prompted with the digital signature security warning, tick Always trust content from this publisher and choose Run. </li>
                        <li>The web start application you have selected will start automatically. At the login screen, enter your SOLA username and password. </li>
                    </ol>
                </td>
            </tr> 
            <tr>
                <td width="20%"></td>   
                <td>
                    <h2>User Guide</h2>
                    <br>
                    The SOLA User Guide provides detailed information on the installation and use of the SOLA Desktop. The User Guide is available from <a href="help/SOLAUserGuide.pdf" target="_blank" >here</a>.
                </td>
            </tr> 
        </table>  
    </body>
</html>
