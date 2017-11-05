<%@page contentType="text/plain"%>
<%@page import="java.net.InetAddress"%>
ads.version=${version}
ads.hostaddress=<%=InetAddress.getLocalHost().getHostAddress() %>
ads.canonicalhostname=<%=InetAddress.getLocalHost().getCanonicalHostName() %>
ads.hostname=<%=InetAddress.getLocalHost().getHostName() %>
ads.tomcat.version=<%= application.getServerInfo() %>
ads.tomcat.catalina_base=<%= System.getProperty("catalina.base") %>
<% 
HttpSession theSession = request.getSession( false );

// print out the session id
if( theSession != null ) {
  //pw.println( "<BR>Session Id: " + theSession.getId() );
  synchronized( theSession ) {
    // invalidating a session destroys it
    theSession.invalidate();
    //pw.println( "<BR>Session destroyed" );
  }
}
%>
