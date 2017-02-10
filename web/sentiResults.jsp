<%-- 
    Document   : sentiResults
    Created on : Jun 9, 2014, 4:01:26 PM
    Author     : Administrator
--%>

<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
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
            google.load("visualization", "1", {packages: ["corechart"]});
            google.setOnLoadCallback(drawChart);
            function drawChart() {
                var data = google.visualization.arrayToDataTable(<%
                    String[] output = (String[]) request.getAttribute("array");
                    out.println(output[1]);

            %>);

                var options = {
                    title: 'Positive-Negative-Neutral tweets Pie', backgroundColor: 'transparent'
                };

                var chart = new google.visualization.PieChart(document.getElementById('piechart'));
                chart.draw(data, options);
            }
        </script>
        <script type="text/javascript">
            google.load("visualization", "1", {packages: ["corechart"]});
            google.setOnLoadCallback(drawChart);
            function drawChart() {
                var data = google.visualization.arrayToDataTable(<%            out.println(output[0]);

            %>);

                var options = {
                    title: 'Positive-Negative-Neutral tweets',
                    backgroundColor: 'transparent',
                };

                var chart = new google.visualization.LineChart(document.getElementById('chart_div'));
                chart.draw(data, options);
            }
        </script>
        <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
        <script>

            var markers = <%out.println(output[2]);%>;
            function initializeMaps() {
                var latlng = new google.maps.LatLng(0, 0);
                var myOptions = {
                    zoom: 2,
                    center: latlng,
                    mapTypeId: google.maps.MapTypeId.ROADMAP,
                    mapTypeControl: true
                };
                var map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);
                var infowindow = new google.maps.InfoWindow(), marker, i;
                for (i = 0; i < markers.length; i++) {
                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(markers[i][1], markers[i][2]),
                        map: map,
                        icon: getMarkerImage(markers[i][3])
                    });
                    google.maps.event.addListener(marker, 'click', (function(marker, i) {
                        return function() {
                            infowindow.setContent(markers[i][0]);
                            infowindow.open(map, marker);
                        }
                    })(marker, i));
                }
            }

            function getMarkerImage(iconColor) {
                icon= {
                    url: "http://labs.google.com/ridefinder/images/mm_20_" + iconColor + ".png",
                    // This marker is 20 pixels wide by 32 pixels tall.
                    size: new google.maps.Size(12, 20),
                    // The origin for this image is 0,0.
                    origin: new google.maps.Point(0, 0),
                    // The anchor for this image is the base of the flagpole at 0,32.
                    anchor: new google.maps.Point(6, 20)};
                return icon;
            }

        </script>
    </head>
    <body onload="initializeMaps()"><div class="container">
            <div class="row">
                <div class="page-header">
                    <h1>
                        <p/>Topic Detection & Sentiment Analysis 
                        <p/><small><em>From twitter data for Champions League </em></small>
                    </h1>
                </div>
            </div>


            <p></p>
            <div class="row "><div class="col-md-8 col-md-offset-2"><h3><span class="label label-primary">Map with Coordinated Tweets</span></h3></div></div>
            <p/><div class="row "><div class="col-md-8 col-md-offset-2"><div id="map_canvas" style="width: auto; height: 500px;"></div></div>  </div>
            <div class="row "><div class="col-md-8 col-md-offset-2"><h3><span class="label label-primary">Chronological Line-Chart of Sentiment Analysis</span></h3></div></div>
            <p/><div class="row "><div class="col-md-8 col-md-offset-2"><div id="chart_div" style="width: 900px; height: 500px;"></div></div></div>
            <div class="row "><div class="col-md-8 col-md-offset-2"><h3><span class="label label-primary">Summery of Sentiment Analysis at a PieChart</span></h3></div></div>
            <div class="row "><div class="col-md-8 col-md-offset-2"><div id="piechart" style="width: 900px; height: 500px;"></div></div></div>
            

            <div class="row "><div class="col-md-2 col-md-offset-2">
                    <a class="btn btn-default" href="http://localhost:8084/WebApplication1/second.html" role="button">
                        <span class="glyphicon glyphicon-arrow-left"></span>Go back
                    </a>

                </div>
            </div>

        </div>
    </body>
</html>
