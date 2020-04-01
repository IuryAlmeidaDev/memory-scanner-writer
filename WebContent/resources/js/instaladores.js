var wsDesktop;
var versaoAtualizada;
var versaoInstalada;
var versaoAtual;

getVersaoWebService();
wsDesktopConnect();

function wsDesktopConnect() {
	wsDesktop = new WebSocket(wsUrl);
	
	wsDesktop.onopen = function()
	{
		var obj = { acao: 'validar_leitor', contexto: contexto, contexto2: contexto2 };
		wsDesktop.send(JSON.stringify(obj));
		PF('statusDialog').show();
	};

	wsDesktop.onclose = function(e)
	{
		setStatus('Aplicativo biométrico fechado ou não instalado.');
		showVersao(false);
		PF('statusDialog').hide();
	};

	wsDesktop.onmessage = function(e)
	{
		var obj = JSON.parse(e.data);

		if (obj != null) {
			switch (obj.acao) {
				case "validar_leitor":
					validarLeitor(obj.valor);
					break;
						
				case "versao":
					validarVersao(obj);
					break;
					
				default:
					break;
			}
		}
	};
}

function setStatus(mensagem) {
	var h = document.getElementById("msgStatus");
	
	if (h != null) {
		h.innerHTML = mensagem;
	}
}

function showVersao(param) {
	var h = document.getElementById("panelVersao");
	
	if (h != null) {
		if (param) {
			h.style.display = 'block';
		} else {
			h.style.display = 'none';
		}
	}
}

function validarVersao(obj) {
	if (obj.valor != null) {
		versaoInstalada = obj.valor;
	} else {
		versaoInstalada = obj.versaoInstalada;
	}
	
	if ((versaoInstalada === versaoAtual)) {
		setStatus('Nenhum problema foi encontrado.');
	} else {
		setStatus('Versão do aplicativo desatualizada, por favor atualize.');
		
		var lblVersaoInstalada = document.getElementById("lblVersaoInstalada");
		var lblVersaoAtual = document.getElementById("lblVersaoAtual");
		
		lblVersaoInstalada.innerHTML = versaoInstalada;
		lblVersaoAtual.innerHTML = versaoAtual;
		
		showVersao(true);
	}
	
	PF('statusDialog').hide();
}

function validarLeitor(param) {
	if ((param === 'true')) {
		var obj = { acao: 'versao', contexto: contexto, contexto2: contexto2 };
		wsDesktop.send(JSON.stringify(obj));
	} else {
		setStatus('Leitor biométrico não conectado ou driver não está instalado.');
		PF('statusDialog').hide();
	}
}