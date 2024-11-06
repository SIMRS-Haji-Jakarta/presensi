function geolocationSuccess(position) {
    var latitude = parseFloat(position.coords.latitude);
    var longitude = parseFloat(position.coords.longitude);
    var userLatLng = new google.maps.LatLng(latitude, longitude);
    var polygons = [];
    var bounds = new google.maps.LatLngBounds();

    document.getElementById('lat').value = latitude;
    document.getElementById('lgn').value = longitude;
    document.getElementById('current').innerHTML = "Latitude=" + latitude + " Longitude=" + longitude;

    var myOptions = {
        zoom: 17,
        center: userLatLng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    // Draw the map
    var mapObject = new google.maps.Map(document.getElementById("map"), myOptions);
    // Place the marker
    var marker = new google.maps.Marker({
        map: mapObject,
        position: userLatLng,
        title: "Posisi anda"
    });

    //mapping by area
    if (statusByArea === true) {
        if (statusByAreaUnitKerja === true)
            geoMappingByAreaByUnitKerja(polygons, bounds, marker, mapObject, pegawai);
        else
            geoMappingByArea(polygons, bounds, marker, mapObject);
    }

}

function geolocationError(positionError) {
    document.getElementById("current").innerHTML = "Error: " + positionError.message + "<br />";
}

function geolocateUser() {
    // If the browser supports the Geolocation API
    if (navigator.geolocation) {
        var positionOptions = {
            enableHighAccuracy: true,
            timeout: 10 * 1000 // 10 seconds
        };
        navigator.geolocation.getCurrentPosition(geolocationSuccess, geolocationError, positionOptions);
    } else
        document.getElementById("current").innerHTML = "Your browser doesn't support the Geolocation API";
}

function checkInPolygon(marker, arrays, map) {

    var infowindow = new google.maps.InfoWindow();
    var html = "Anda berada di luar area UIN Jakarta";
    var ableToPresent = false;

    for (var k = 0; k < arrays.length; k++) {
        if (google.maps.geometry.poly.containsLocation(marker.getPosition(), arrays[k])) {
            html = "Anda berada di dalam area UIN Jakarta";
            ableToPresent = true;
            break;
        }
    }

    buttonKirimStyle(ableToPresent);

    infowindow.setContent(html);
    infowindow.open(map, marker);
}

function convertJsonToPolygons(jsonData, bounds, mapObject) {
    var polygons = [];
    for (var i = 0; i < jsonData.areaunit.length; i++) {
        var lokasi = jsonData.areaunit[i];
        //console.log(jsonData);
        var coords = lokasi.koordinat;
        var coordsArray = [];
        coordsArray = coords.split(';');
        coordsArray = coordsArray.filter(function(e){return e});
        var coordXY;
        var arr = [];
        for (var j = 0; j < coordsArray.length; j++) {
            coordXY = [];
            coordXY = coordsArray[j].split(',');
            //console.log("koord-X: " + coordXY[0] + " dan koord-Y: " + coordXY[1]);
            arr.push(new google.maps.LatLng(
                parseFloat(coordXY[0]), parseFloat(coordXY[1])
            ));
            bounds.extend(arr[arr.length - 1])
        }
        polygons.push(new google.maps.Polygon({
            paths: arr,
            strokeColor: '#FF0000',
            strokeOpacity: 0.8,
            strokeWeight: 2,
            fillColor: '#FF0000',
            fillOpacity: 0.35
        }));
        polygons[polygons.length - 1].setMap(mapObject);
    }
    return polygons;
}

function geoMappingByArea(polygons, bounds, marker, mapObject) {
    //Get json from backend
    var jsonData;
    $.ajax({
        url: window.location.origin + "/api/employees/area-unit",
        type: "GET",
        dataType: "json",
        success: function (data) {
            jsonData = data;
        },
        error: function (e) {
            console.log(e);
        }
    });

    setTimeout(function () {
        polygons = convertJsonToPolygons(jsonData, bounds, mapObject);
        checkInPolygon(marker, polygons, mapObject);
        mapObject.fitBounds(bounds);
    }, 1000);
}

function geoMappingByAreaByUnitKerja(polygons, bounds, marker, mapObject, pegawai) {
    //Get json from backend
    var jsonData;
    $.ajax({
        url: window.location.origin + "/api/employees/area-unit/" + pegawai,
        type: "GET",
        dataType: "json",
        success: function (data) {
            jsonData = data;
        },
        error: function (e) {
            console.log(e);
        }
    });

    setTimeout(function () {
        polygons = convertJsonToPolygons(jsonData, bounds, mapObject);
        checkInPolygon(marker, polygons, mapObject);
        mapObject.fitBounds(bounds);
    }, 1000);
}

function buttonKirimStyle(status) {
    var buttonKirim = $("#kirim");
    var textBtnKirim = $("#kirim strong");
    if (status === false) {
        buttonKirim.attr('disabled', true);
        document.getElementById('kirim').title = 'Anda tidak dapat melakukan presensi di lokasi ini!';
        textBtnKirim.text("Anda tidak dapat melakukan presensi di lokasi ini!");
        //buttonKirim.removeClass("btn btn-success btn-lg btn-block");
        //buttonKirim.addClass("btn btn-danger btn-lg btn-block");
    } else if (status === true) {
        buttonKirim.attr('disabled', false);
        document.getElementById('kirim').title = '';
        //buttonKirim.removeClass("btn btn-danger btn-lg btn-block");
        //buttonKirim.addClass("btn btn-success btn-lg btn-block");
        textBtnKirim.text("Kirim");
    }
}
