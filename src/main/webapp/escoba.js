var url = "ws://localhost:8600/juegos";
var ws = new WebSocket(url);

function ViewModel() {
    var self = this;
    self.usuarios = ko.observableArray([]);
    self.cartas = ko.observableArray([]);
    self.cartasEnMesa = ko.observableArray([]);

    var idMatch = sessionStorage.idMatch;
    
    self.mensaje = ko.observable("");
    self.mensaje("La partida " + idMatch + " ha comenzado");
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
    }
}

var vm = new ViewModel();
ko.applyBindings(vm);