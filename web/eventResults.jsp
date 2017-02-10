<%-- 
    Document   : results
    Created on : 31 Μαϊ 2014, 12:24:02 πμ
    Author     : Moi
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Date"%>
<%@page import="mainPackage.mongoHelp" %>
<%@page import="java.util.ArrayList"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet" href="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script src="http://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
<style type="text/css">body{background-image:url('https://abs.twimg.com/images/themes/theme1/bg.png') ;background-color: #c0deed !important;background-repeat:repeat-x;background-attachment:fixed;background-position:top center}</style>

        <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
          var data = new google.visualization.DataTable();
data.addColumn('string', 'date-time'); // Implicit domain label col.
data.addColumn('number', 'Tweets'); // Implicit series 1 data col.
  // interval role col.
data.addColumn({type:'boolean', role:'emphasis'}); // annotation role col.
data.addColumn({type:'string', role:'annotation'});
data.addRows(
      
      <%
        String[] output=(String[])request.getAttribute("array");
        out.println(output[0]);
      %>
        );
        var options = {
          title: 'Peaks of Tweets',
          backgroundColor: 'transparent',
          curveType: 'function',
          legend: { position: 'bottom' }
        };

        var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
    </head>
    <body><div class="container">
<div class="row">
		<div class="page-header">
		  <h1>
		  	<p/>Topic Detection & Sentiment Analysis 
		  	<p/><small><em>From twitter data for Champions League </em></small>
		  </h1>
		</div>
	</div>
	
	
	<p></p>
        
        <p/><div class="row "><div class="col-md-8 col-md-offset-2"><div id="chart_div" style="width: 900px; height: 500px;"></div></div></div>
        <div class="row "><div class="col-md-8 col-md-offset-2"><%out.println(output[1]);%></div></div>
        <div class="row "><div class="col-md-2 col-md-offset-2">
                <a class="btn btn-default" href="http://localhost:8084/WebApplication1/second.html" role="button">
                    <span class="glyphicon glyphicon-arrow-left"></span>Go back
                </a>
                
            </div>
        </div>
        </div>
</body>
</html>
