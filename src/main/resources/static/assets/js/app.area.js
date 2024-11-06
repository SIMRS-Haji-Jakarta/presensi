$(document).ready(function () {
    var textSwitch = $("#labelAkif");
    $('#switchAktif').click(function () {
        !$(this).is(':checked') ? textSwitch.html("Tidak Aktif") : textSwitch.html("Aktif");
    });
    $('.modal').click(function (event) {
        $(event.target).modal('hide');
    });
});

function replaceToNewLine(i) {
    //console.log(edt);
    return i.replaceAll(";", "\r\n");
}

function drawThisArea() {
    var polygons = [];
    var bounds = new google.maps.LatLngBounds();

    var myOptions = {
        zoom: 17,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    // Draw the map
    var mapObject = new google.maps.Map(document.getElementById("map"), myOptions);

    //draw polygons
    drawPolygon(polygons, bounds, mapObject);

}

function drawPolygon(polygons, bounds, mapObject) {
    var stringCoord = $("#koordinat").val();
    var edt = stringCoord.replace(/(?:\r\n|\r|\n)/g, ';');
    //stringCoord.replace(/\s/g, ''); //remove spaces
    //console.log(edt);
    polygons = convertStringToPolygon(edt, bounds, mapObject);
    mapObject.fitBounds(bounds);
    document.getElementById("current").innerHTML = "";
}

function convertStringToPolygon(coords, bounds, mapObject) {
    var polygons = [];
    var coordsArray = [];
    coordsArray = coords.split(';');
    coordsArray = coordsArray.filter(function (e) {
        return e
    });
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

    return polygons;
}

function viewModal(id) {
    $.ajax({
        url: "/pusat/area/modal/" + id,
        success: function (data) {
            //alert(data);
            //console.log(data);
            $("#view-area-modal-holder").html(data);
            $("#view-area-modal").modal("show");
        }
    });
}

function deleteArea(id) {
    $.ajax({
        url: "/pusat/area/delete/" + id,
        success: function (data) {
            reload();
        }
    });
}

function reload() {
    $("#table-content").load(window.location.href + " #table-content");
}
