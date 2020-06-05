var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);
var MiUsuario = "";

function ViewModel() {
    var self = this;
    self.usuarios = ko.observableArray([]);
    self.cartas = ko.observableArray([]);
    self.cartasEnMesa = ko.observableArray([]);
    var idMatch = sessionStorage.idMatch;
    self.mensaje = ko.observable("Esperando oponente para la partida " + idMatch);
    
    var finished = false;
    
    self.ponerEnMesa = function (carta) {
        var msg = {
            type: "carta a la mesa",
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
                self.cartasEnMesa.push(table[i]);
            }
            var cartas = data.startData.data;
            for (i = 0; i < cartas.length; i++)
                self.cartas.push(cartas[i]);
            console.log(data);
        }

        if(data.type === "matchChangeTurn")
            self.mensaje("Turno de " + data.turn);

        if(data.type === "matchIlegalPlay")
            alert(data.result);

        if(data.type === "matchFinished"){
            self.mensaje(data.result);
            finished = true;
        }

        if(data.type === "cardRobbed"){
            var option = document.createElement("option");
            option.value = data.fichaN1 + ' | ' + data.fichaN2;
            option.text = data.fichaN1 + ' | ' + data.fichaN2;
            var fichasOps = document.getElementById("fichas");
            fichasOps.add(option)
        }

    }
    
}

var vm = new ViewModel();
ko.applyBindings(vm);