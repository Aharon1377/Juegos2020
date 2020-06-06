var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);
var MiUsuario = "";

function ViewModel() {
    var self = this;
    self.usuarios = ko.observableArray([]);
    self.cartas = ko.observableArray([]);
    self.cartasEnMesa = ko.observableArray([]);
    self.pregunta = ko.observableArray([]);
    var idMatch = sessionStorage.idMatch;
    self.mensaje = ko.observable("Esperando oponente para la partida " + idMatch);
    
    var finished = false;
    
    self.votar = function (carta) {
        var msg = {
            type: "votar",
            idMatch: sessionStorage.idMatch,
            carta: carta
        };
        ws.send(JSON.stringify(msg));
    };
    
    self.ponerEnMesa = function (carta) {
        var msg = {
            type: "responder",
            idMatch: sessionStorage.idMatch,
            carta: carta
        };
        ws.send(JSON.stringify(msg));
    };

    ws.onopen = function (event) {
        var msg = {
            type: "ready",
            idMatch: sessionStorage.idMatch
        };
        ws.send(JSON.stringify(msg));
    };
    
    ws.onmessage = function (event) {
        var data = event.data;
        data = JSON.parse(data);

        if (data.type == "matchStarted") {
            self.mensaje("La partida ha empezado");
            var players = data.players;
            var i;
            for (i = 0; i < players.length; i++) {
                var player = players[i];
                self.usuarios.push(player.userName);
            }
            var table = data.startData.table;
            for (i = 0; i < table.length; i++) {
                self.pregunta.push(table[i]);
            }
            var cartas = data.startData.data;
            for (i = 0; i < cartas.length; i++)
                self.cartas.push(cartas[i]);
            
		    var table = document.getElementById("rank-table");
			var row;
			var rank=1;
			var rank_cell;
			var username_cell;
			var wins_cell;
			
			$(document).ready(function() {
				getRankedPoints();
			});	
			
			function getRankedPoints() {
				$.get("getRankedPoints", function(datos) {
			        
			        for (let i = 0; i < datos.length; i+=2) {
			        	
			        	var number = i/2 +1;
			        	console.log(number)
			        	row = table.insertRow(number);
			        	rank_cell = row.insertCell(0);
			        	username_cell = row.insertCell(1);
			        	wins_cell = row.insertCell(2);
			        	rank_cell.innerHTML = number;
			        	username_cell.innerHTML = datos[i];
			        	wins_cell.innerHTML = datos[i+1];
			        	  
			        }
				});
			}
            
        }

        if(data.type === "matchChangeTurn")
            self.mensaje("Turno de " + data.turn);

        if(data.type === "matchIlegalPlay")
            alert(data.result);

        if(data.type === "nuevaRespuesta"){
        	
			self.cartas.remove( function(item){
				return item.text == data.carta.text;
			});
            self.cartasEnMesa.push(data.carta);
        }
        
        if(data.type === "nuevaPregunta"){
        	
            self.cartasEnMesa.removeAll();
            self.pregunta.removeAll();
            self.pregunta.push(data.carta);
        }
        
        if(data.type === "nuevaCarta"){
        	
            self.cartas.push(data.carta);
        }
        
        if(data.type === "winner"){
        
        	var table = document.getElementById("rank-table");
			var row;
			var rank=1;
			var rank_cell;
			var username_cell;
			var wins_cell;
			var x = document.getElementById("rank-table").rows.length;
			var i;
			
			for (i = 1; i < x; i++) {
				document.getElementById("rank-table").deleteRow(1); 
			} 
			
        	$.get("getRankedPoints", function(datos) {
			        
			        for (let i = 0; i < datos.length; i+=2) {
			        	
			        	var number = i/2 +1;
			        	console.log(number)
			        	row = table.insertRow(number);
			        	rank_cell = row.insertCell(0);
			        	username_cell = row.insertCell(1);
			        	wins_cell = row.insertCell(2);
			        	rank_cell.innerHTML = number;
			        	username_cell.innerHTML = datos[i];
			        	wins_cell.innerHTML = datos[i+1];
			        	  
			        }
				});
        }

    }
    
}

var vm = new ViewModel();
ko.applyBindings(vm);