package arius.pdv.base;

import java.util.Date;
import java.util.List;

import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;
import arius.pdv.db.AndroidUtils;

public class PdvService {
	
	private Pdv pdv;
	private Venda vendaAtiva;
	private Usuario operadorAtual;
	private static PdvService pdvService;
	private static String msg_padrao = "Operação não permitida. \n" ;
	private boolean fechandoCaixa = false;
	
	//Daos
	private UsuarioDao usuarioDao;
	private PdvDao pdvDao;
	//private UnidadeMedidaDao unidadeMedidaDao;
	//private ProdutoDao produtoDao;
	//private FinalizadoraDao finalizadoraDao;
	private VendaDao vendaDao;
	private VendaItemDao vendaItemDao;
	private VendaFinalizadoraDao vendaFinalizadoraDao;
	private PdvValorDao pdvValorDao;	
	
	PdvService(){
		AppContext app = AppContext.get();
		
		//Carga dos Daos com cache
 		this.pdvDao = app.getDao(PdvDao.class);
		this.usuarioDao = app.getDao(UsuarioDao.class);
		app.getDao(UnidadeMedidaDao.class);
		this.vendaDao = app.getDao(VendaDao.class);
		app.getDao(ProdutoDao.class);
		app.getDao(FinalizadoraDao.class);
		
		//Carga dos Daos sem cache
		this.vendaItemDao = app.getDao(VendaItemDao.class);
		this.vendaFinalizadoraDao = app.getDao(VendaFinalizadoraDao.class);
		this.pdvValorDao = app.getDao(PdvValorDao.class);		
	}
	
	public static PdvService get(){
		if (pdvService == null)
			pdvService = new PdvService();
		return pdvService;
	}

	public Pdv getPdv() {
		if (this.pdv != null)
			this.vendaAtiva = this.pdv.getVendaAtiva();
		if (this.pdv != null && this.operadorAtual != this.pdv.getOperador())
			this.operadorAtual = this.pdv.getOperador();
		return this.pdv;
	}

	public void setPdv(Pdv pdv) {
		this.pdv = pdv;
	}

	public Venda getVendaAtiva() {
		return vendaAtiva;
	}

	public void setOperadorAtual(Usuario operadorAtual) {
		this.operadorAtual = operadorAtual;
	}

	public Usuario getOperadorAtual() {
		return operadorAtual;
	}

	public UsuarioDao getUsuarioDao() {
		return usuarioDao;
	}

	private void verificaPdvAberto(Pdv pdv){
		if (!pdv.isAberto())
			throw new UserException(msg_padrao + "O caixa não está aberto!");
		
		if (!fechandoCaixa && PdvUtil.comparar_Datas(pdv.getDataAbertura(), new Date())) {
			throw new UserException(msg_padrao + "O caixa do dia " + PdvUtil.converteData_texto(pdv.getDataAbertura()) +
					" está aberto, fechar o caixa e abrir novamente com a data atual!");
		}
	}

	private void verificaVendaAberta(Venda venda){
		if (venda.getSituacao() != VendaSituacao.ABERTA)
			throw new UserException(msg_padrao + "Venda já " + venda.getSituacao().name());
	}
	
	private Finalizadora finalizadorDinheiro(){
		
		FuncionaisFilters<Finalizadora> filter = new FuncionaisFilters<Finalizadora>() {

			@Override
			public boolean test(Finalizadora p) {				
				return p.getTipo() == FinalizadoraTipo.DINHEIRO;
			}
		};

		int v_tot_fin = AppContext.get().getDao(FinalizadoraDao.class).listCache(filter).size();

		if (v_tot_fin > 1)
			throw new UserException(msg_padrao + "Existe mais de uma finalizadora para o caixa com o tipo DINHEIRO!");

		if (v_tot_fin == 0)
			throw new UserException(msg_padrao + "Nenhuma finalizadora para o caixa com o tipo DINHEIRO!");


		return AppContext.get().getDao(FinalizadoraDao.class).listCache(filter).get(0);
				
	}
	
	//************* Operações sobre Venda ************//
	
	public void insereVenda(Venda venda) {
		//REGRA 1: verificar status pdv
		//REGRA 2: setar situação para aberta
		//REGRA 3: setar pdv - iremos setar o pdv aqui ou no encerrar a venda?
		//REGRA 4: setar operador
		//REGRA 5: setar a data hora na venda - verificar s irá fazer aqui mesmo.
		//REGRA 6: persitir venda
		//REGRA 7: setar venda ativa no pdv e persistir pdv

		//REGRA 1
		verificaPdvAberto(this.pdv);
		//REGRA 2
		venda.setSituacao(VendaSituacao.ABERTA);
		//REGRA 3
		venda.setPdv(this.pdv);
		//REGRA 4
		venda.setOperador(this.operadorAtual);
		//REGRA 5
		venda.setDataHora(new Date());
		//REGRA 6
		this.vendaAtiva = venda;
		this.vendaDao.insert(venda);
		//REGRA 7
		this.pdv.setVendaAtiva(this.vendaAtiva);
		this.pdvDao.update(this.pdv);
	}
	
	public void alteraVenda(Venda venda) {
		//REGRA 1: só pode ser alterada venda aberta
		//REGRA 2: alterar situação para aberta
		//REGRA 3: persitir venda
		//REGRA 4: alterar venda ativa no pdv e persistir pdv
		
		//REGRA 1
		verificaVendaAberta(venda);
		
		//REGRA 2
		venda.setSituacao(VendaSituacao.ABERTA);
		
		//REGRA 3
		this.vendaDao.update(venda);
		
		//REGRA 4
		this.pdv.setVendaAtiva(null);
		this.pdvDao.update(pdv);
	}
	
	public void removeVenda(Venda venda) {
		//REGRA 1: só pode ser removida venda aberta
		//REGRA 2: remover venda
		//REGRA 3: alterar venda ativa no pdv e persistir pdv
		
		//REGRA 1
		if (venda.getSituacao() != VendaSituacao.FECHADA)
			throw new UserException(msg_padrao + "Somente é possível excluír uma venda aberta!");
		
		//REGRA 2
		vendaDao.delete(venda);
		
		//REGRA 3
		pdv.setVendaAtiva(null);
		pdvDao.update(pdv);
	}
	
	/**
	 * Faz o encerramento da venda executando todas validações
	 * @param venda
	 */
	public void encerraVenda(Venda venda) {
		//REGRA 1: pdv deve estar aberto
		//REGRA 2: venda deve estar aberta
		//REGRA 3: valor restante deve ser igual a zero
		//REGRA 4: definir pdv atual
		//REGRA 5: definir operador
		//REGRA 6: alterar situação para encerrada
		//REGRA 7: coloca o troco na venda
		//REGRA 8: atualiza o movimento do dia nos valores pdv
		//REGRA 9: persitir venda
		//REGRA 10: passar venda ativa do pdv para null e persistir pdv
		
		//REGRA 1
		verificaPdvAberto(venda.getPdv());
		
		//REGRA 2
		verificaVendaAberta(venda);
		
		//REGRA 3
		if (this.vendaAtiva.getValorRestante() > 0)
			throw new UserException(msg_padrao + "O valor total da venda não foi informado \n" +
									"Valor restante " + AndroidUtils.FormatarValor_Monetario(this.vendaAtiva.getValorRestante()));
		
		//REGRA 4
		this.vendaAtiva.setPdv(this.pdv);
		
		//REGRA 5
		this.vendaAtiva.setOperador(operadorAtual);
		
		//REGRA 6
		this.vendaAtiva.setSituacao(VendaSituacao.FECHADA);

		//REGRA 7
		this.vendaAtiva.setValorTroco(this.vendaAtiva.getValorPago() - this.vendaAtiva.getValorLiquido());

		//REGRA 8
		for(VendaFinalizadora lfinalizadora : this.vendaAtiva.getFinalizadoras()) {
			geraValoresMovimentoDiario(lfinalizadora.getFinalizadora(), lfinalizadora.getValor(), 1);
		}

		//REGRA 9
		vendaDao.update(this.vendaAtiva);
		
		//REGRA 10
		this.pdv.setVendaAtiva(null);
		this.pdvDao.update(this.pdv);

	}
	
	/**
	 * Faz o cancelamento da venda mantendo a mesma em banco de dados
	 * @param venda
	 */
	public void cancelaVenda(Venda venda) {
		//REGRA 1: venda deve estar encerrada
		//REGRA 2: alterar situação para cancelada
		//REGRA 3: estorna valores diários da venda
		//REGRA 4: persitir venda
		//REGRA 5: setar venda ativa pdv e persistir pdv
		
		//REGRA 1
		if (venda.getSituacao() != VendaSituacao.FECHADA)
			throw new UserException(msg_padrao + "Somente é possível cancelar uma venda fechada!");
				
		//REGRA 2
		venda.setSituacao(VendaSituacao.CANCELADA);

		//REGRA 3
		//REGRA 8
		for(VendaFinalizadora lfinalizadora : this.vendaAtiva.getFinalizadoras()) {
			geraValoresMovimentoDiario(lfinalizadora.getFinalizadora(), lfinalizadora.getValor(), 3);
		}

		//REGRA 4
		vendaDao.update(venda);
		
		//REGRA 5
		pdv.setVendaAtiva(null);
		pdvDao.update(pdv);
	}

	//************* Operações sobre VendaItem ************//
	
	private void validaVendaItem(VendaItem item) {
		//REGRA 1: venda deve estar aberta
		//REGRA 2: produto deve ser informado
		//REGRA 3: se qtde for fracionada verificar se unidade de medida permite fracionamento
		//REGRA 4: qtde deve ser maior que zero
		//REGRA 5: desconto, acréscimo deve ser maior ou igual a zero
		//REGRA 6: valor líquido deve ser maior ou igual a zero

		//REGRA 1
		verificaVendaAberta(item.getVenda());
		//REGRA 2
		if (item.getProduto() == null)
			throw new UserException(msg_padrao + "Produto não informado!");
		//REGRA 3
		if (Math.floor(item.getQtde()) > item.getQtde() && !item.getProduto().getUnidadeMedida().isFracionada())
			throw new UserException(msg_padrao + "O unidade de medido deste produto não aceira quantidade fracionada!");
		//REGRA 4
		if (item.getQtde() < 0)
			throw new UserException(msg_padrao + "A quantidade digitada não pode ser menor que zero!");
		//REGRA 5
		if (item.getDesconto() < 0)
			throw new UserException(msg_padrao + "O desconto informado não pode ser menor que zero!");
		if (item.getAcrescimo() < 0)
			throw new UserException(msg_padrao + "O acrésimo informado não pode ser menor que zero!");
		//REGRA 6
		if (item.getValorLiquido() < 0)
			throw new UserException(msg_padrao + "O valor liquído do produto não pode ser menor que zero!");
	}
	
	public void insereVendaItem(VendaItem item) {
		//REGRA 1: verifica venda aberta
		//REGRA 2: validar item {validaVendaItem}
		//REGRA 3: inserir a venda no item
		
		//REGRA 1
		verificaVendaAberta(item.getVenda());
		//REGRA 2
		validaVendaItem(item);
		//REGRA 3
		item.setVenda(this.vendaAtiva);	
		
		vendaItemDao.insert(item);
		
		vendaDao.atualizaItens(item.getVenda());		
	}
	
	public void alteraVendaItem(VendaItem item) {
		//REGRA 1: validar item {validaVendaItem}
		//REGRA 2: persistir dados do item
		
		//REGRA 1
		validaVendaItem(item);
		//REGRA 2
		vendaItemDao.update(item);
		
		vendaDao.atualizaItens(item.getVenda());
	}
	
	public void removeVendaItem(VendaItem item) {
		//REGRA 1: venda deve estar aberta
		//REGRA 2: persistir dados do item
		
		//REGRA 1		
		verificaVendaAberta(item.getVenda());
		//REGRA 2
		vendaItemDao.delete(item);
		
		vendaDao.atualizaItens(item.getVenda());
	}
	
	//************* Operações sobre VendaFinalizadora ************//	
	
	private void validaVendaFinalizadora(VendaFinalizadora vendaFinalizadora){
		//REGRA 1: verificar se a venda foi informada
		//REGRA 2: verificar se a venda está aberta
		//REGRA 3: validar se a finalizadora foi informada
        //REGRA 4: validar se aceita troco a venda
		
		//Regra 1
		if (vendaFinalizadora.getVenda() == null)
			throw new UserException(msg_padrao + "Venda não informada na finalizadora!");
		
		//REGRA 2
		verificaVendaAberta(vendaFinalizadora.getVenda());
		
		//REGRA 3
		if (vendaFinalizadora.getFinalizadora() == null)
			throw new UserException(msg_padrao + "Finalizadora não informada!");

        //REGRA 4
        if ((vendaFinalizadora.getVenda().getValorPago() + vendaFinalizadora.getValor()) - vendaFinalizadora.getVenda().getValorLiquido()  > 0 ){
            if (!vendaFinalizadora.getFinalizadora().isPermiteTroco() && !verificaAceitaTroco(vendaFinalizadora.getVenda()))
                throw new UserException("A venda não possuí uma finalizadora que aceite troco!");
        }
	}	
	
	private boolean verificaAceitaTroco(Venda venda){
		for (VendaFinalizadora lvVendaFinalizadora : venda.getFinalizadoras()){
			if (lvVendaFinalizadora.getFinalizadora().isPermiteTroco())
				return true;
		}
		return false;
	}
	
	public void insereVendaFinalizadora(VendaFinalizadora vendaFinalizadora){
		//REGRA 1: validar finalizadora{validaVendaFinalizadora}
		//REGRA 2: insere a venda ativa na finalziadora
		//REGRA 3: persistir finalizadora da venda

		//REGRA 1
		validaVendaFinalizadora(vendaFinalizadora);

		//REGRA 2
		vendaFinalizadora.setVenda(this.vendaAtiva);
		
		//REGRA 3
		vendaFinalizadoraDao.insert(vendaFinalizadora);

		vendaDao.atualizaFinalizadora(this.vendaAtiva);
	}
	
	public void alteraVendaFinalizadora(VendaFinalizadora vendaFinalizadora){
		//REGRA 1: validar finalizadora{validaVendaFinalizadora}
		//REGRA: persistir finalizadora da venda
					
		//REGRA 1
		validaVendaFinalizadora(vendaFinalizadora);
		
		//REGRA
		vendaFinalizadoraDao.update(vendaFinalizadora);	
		
		vendaFinalizadora.setVenda(this.vendaAtiva);		
		
		vendaDao.atualizaFinalizadora(this.vendaAtiva);		
	}
	
	public void removeVendaFinalizadora(VendaFinalizadora vendaFinalizadora){
		//REGRA 1: validar finalizadora{validaVendaFinalizadora}
		//REGRA 2: persistir finalizadora da venda
		//REGRA 3: atualizar variavel com a venda atual
		//REGRA 4: remove o valor do movimento do dia dos valores do pdv
					
		//REGRA 1
		validaVendaFinalizadora(vendaFinalizadora);
		
		//REGRA 2
		vendaFinalizadoraDao.delete(vendaFinalizadora);
		
		//REGRA 3
		vendaDao.atualizaFinalizadora(this.vendaAtiva);		
		
		//REGRA 4
		geraValoresMovimentoDiario(vendaFinalizadora.getFinalizadora(), vendaFinalizadora.getValor(),3);
	}

	//************* Operações sobre PDV ************//
	
	public void abreCaixa(Usuario operador, double saldoInicial) {
		//REGRA 1: verificar se foi carregado o pdv
		//REGRA 2: caixa deve estar fechado
		//REGRA 3: saldo inicial deve ser maior ou igual a zero
		//REGRA 4: verificar se o operador foi carregado
		//REGRA 5: setar o caixa para aberto, operador e saldoInicial;
		//REGRA 6: gerar um reforco no caixa com o valor do saldo de dinheiro
		//REGRA 7: persistir dados do pdv
		
		//REGRA 1
		if (pdv == null){
			throw new UserException(msg_padrao + "Pdv não encontrado ou não configurado!");
		}
		//REGRA 2
		if (pdv.isAberto()){
			throw new UserException(msg_padrao + "O caixa já está aberto!");
		}
		//REGRA 3
		if (saldoInicial <= 0){
			throw new UserException(msg_padrao + "O saldo Iniciao não pode ser menor ou igual a zero!");			
		}
		//REGRA 4 
		if (operador == null || operador.getId() == 0){
			throw new UserException(msg_padrao + "Informar um operador para abrir o caixa");
		}
		//REGRA 5
		pdv.setAberto(true);
		pdv.setOperador(operador);
		pdv.setSaldoDinheiro(saldoInicial);
		pdv.setDataAbertura(new Date());

		//REGRA 6
		if (saldoInicial > 0)
			reforcoCaixa(saldoInicial);

		//REGRA 7
		pdvDao.update(pdv);
	}
	
	/**
	 * Retorna o saldo da finalizadora seguindo a regra: movimentos da finalizadora -
	 * valores já sangrados
	 * @param finalizadora
	 * @return
	 */
	public double calculaSaldoFinalizadora(Finalizadora finalizadora) {
		//REGRA 1: listar o saldo da finalizadora do movimento do dia 
		
		double saldo = 0;
		for (PdvValor lpdvValor : pdvValorDao.listDatabase("pdv_id = " + this.pdv.getId() +
														   " and strftime('%d/%m/%Y',datetime(substr(data_hora,0, length(data_hora)-2),  'unixepoch', 'localtime')) = '" +
														   PdvUtil.converteData_texto(this.pdv.getDataAbertura())+
														   "' and finalizadora_id = " + finalizadora.getId())) {
			if (lpdvValor.getTipo() == PdvValorTipo.SANGRIA)
				saldo -= lpdvValor.getValor();
			else
				saldo += lpdvValor.getValor();
		}
		
		return saldo;
	}
	
	public void sangraCaixa(Finalizadora finalizadora, double valor, Usuario sangrador) {
		//REGRA 1: validar se finalizadora foi informada
		//REGRA 2: verificar se usuario da que esta efetuando a sangria foi informado
		//REGRA 3: valor da sangria não pode ser maior que o saldo da finalizadora
		//REGRA 4: gerar pdv valores com a sangria e persistir os dados
		
		//REGRA 1
		if (finalizadora == null)
			throw new UserException(msg_padrao + "Finalizadora não informada!");
		//REGRA 2
		if (sangrador == null)
			throw new UserException(msg_padrao + "Operado não informado!");
		//REGRA 3
		if (calculaSaldoFinalizadora(finalizadora) < valor)
			throw new UserException(msg_padrao + "Finalizador sem saldo para efetuar a sangria!");
		
		//REGRA 4
		PdvValor pdvValor = new PdvValor();
		pdvValor.setPdv(pdv);
		pdvValor.setTipo(PdvValorTipo.SANGRIA);
		pdvValor.setDataHora(new Date());
		pdvValor.setFinalizadora(finalizadora);
		pdvValor.setUsuario1(operadorAtual);
		pdvValor.setUsuario2(sangrador);
		pdvValor.setValor(valor);
		this.pdvValorDao.insert(pdvValor);
	}
	
	private void sangriaTotal(Usuario sangrador) {
		//REGRA 1: verificar pdv aberto
		//REGRA 2: executa a sangria total para o fechamento do caixa
		
		//REGRA 1
		verificaPdvAberto(pdv);
		
		//REGRA 2
		for (PdvValor lpdvValor : pdvValorDao.listDatabase("strftime('%d/%m/%Y',datetime(substr(data_hora,0, length(data_hora)-2),  'unixepoch', 'localtime')) = '"
															+ PdvUtil.converteData_texto(pdv.getDataAbertura()) +
														   "' and pdv_id = " + pdv.getId() +
														   " and tipo <> " + PdvValorTipo.SANGRIA.ordinal())) {
			Double v_sandria = calculaSaldoFinalizadora(lpdvValor.getFinalizadora());
			sangraCaixa(lpdvValor.getFinalizadora(), v_sandria, sangrador);
		}
	}

	public void fechaCaixa(Usuario fechador) {
		//REGRA 1: verificar o usuario que está fechando o caixa
		//REGRA 2: pdv deve estar aberto
		//REGRA 3: fazer sangria total (verificar configuração)
		//REGRA 4: alterar os dados no pdv e persistir os dados

		this.fechandoCaixa = true;

		//REGRA 1
		if (fechador == null || fechador.getId() == 0){
			throw new UserException(msg_padrao + "Informar um operador para fechar o caixa");
		}		
		//REGRA 2
		verificaPdvAberto(pdv);
		
		//REGRA 3
		sangriaTotal(fechador);
		
		//REGRA 4
		this.pdv.setSaldoDinheiro(0);
		this.pdv.setAberto(false);
		this.pdv.setOperador(null);
		this.pdv.setVendaAtiva(null);
		this.pdv.setDataAbertura(null);
		this.pdvDao.update(this.pdv);

		this.fechandoCaixa = false;
	}
	
	public void reforcoCaixa(double valor) {
		//REGRA 1: pdv deve estar aberto
		//REGRA 2: o valor deve ser maior que zero
		//REGRA 3: alimentar a variavel com as informações dos valores e persistir no banco
		
		//REGRA 1
		verificaPdvAberto(pdv);
		//REGRA 2
		if (valor <= 0)
			throw new UserException(msg_padrao + "O valor do reforçao precisa ser maior que zero!");
		
		PdvValor pdvValor = new PdvValor();
		pdvValor.setPdv(pdv);
		pdvValor.setTipo(PdvValorTipo.REFORCO);
		pdvValor.setDataHora(new Date());
		//verificar como iremos pegar a finalizara;
		pdvValor.setFinalizadora(finalizadorDinheiro());
		pdvValor.setUsuario1(operadorAtual);
		//verificar como iremos pegar o segundo operador
		pdvValor.setUsuario2(null);
		pdvValor.setValor(valor);
		pdvValorDao.insert(pdvValor);
	}
	
	public void trocaOperador(Usuario novoOperador) {
		//REGRA 1: pdv deve estar aberto
		//REGRA 2: alterar operador pdv e persistir
		
		//REGRA 1
		verificaPdvAberto(pdv);
		
		//REGRA 2
		pdv.setOperador(novoOperador);
		pdvDao.update(pdv);
	}
	
	/**
	 * Utilizar somente em modo venda varejo (lojas / supermercados)
	 * @param venda
	 */
	public void defineVendaAtiva(Venda venda) {
		//REGRA 1: não pode ter venda definida (campo deve estar null) em pdv
		//REGRA 2: definir pdv atual
		//REGRA 3: definir operador na 
		//REGRA 4: persistir venda ativa no pdv;
		
		//REGRA 1
		if (pdv.getVendaAtiva() != null)
			throw new UserException(msg_padrao + "O pdv já está com venda ativa!");
		
		//REGRA 2
		venda.setPdv(pdv);
		
		//REGRA 3
		venda.setOperador(operadorAtual);
		this.vendaAtiva = venda;
		
		//REGRA 4
		pdv.setVendaAtiva(this.vendaAtiva);
		pdvDao.update(pdv);
	}

	public void geraValoresMovimentoDiario(Finalizadora finalizadora, double valor, int tipoLanc){
		//para o parametro tipolanc iremos utilizar, 1 = gerar lancamento(insert), 2 atualiza lancamento(update) e 3 atualiza lancamento(delete)
		PdvValor pdvValor = null;
		List<PdvValor> lpdvValor = pdvValorDao.listDatabase("pdv_id = " + this.pdv.getId() +
				" and finalizadora_id = " + finalizadora.getId() +
				" and strftime('%d/%m/%Y',datetime(substr(data_hora,0, length(data_hora)-2),  'unixepoch', 'localtime')) = '" +
				PdvUtil.converteData_texto(this.pdv.getDataAbertura()) + "'" +
				" and tipo = " + PdvValorTipo.MOVDIARIO.ordinal());

		if (lpdvValor.size() == 0 && tipoLanc != 3) {
			pdvValor = new PdvValor();
			pdvValor.setDataHora(this.pdv.getDataAbertura());
			pdvValor.setTipo(PdvValorTipo.MOVDIARIO);
			pdvValor.setFinalizadora(finalizadora);
			pdvValor.setPdv(this.pdv);
			pdvValor.setUsuario1(operadorAtual);
			pdvValor.setUsuario2(null);
			pdvValor.setValor(valor);

			this.pdvValorDao.insert(pdvValor);
		} else {
			if (lpdvValor.size() == 0)
				return;

			pdvValor = lpdvValor.get(0);
			if (tipoLanc == 3)
				pdvValor.setValor(pdvValor.getValor() - valor);
			else
				pdvValor.setValor(pdvValor.getValor() + valor);
			pdvValorDao.update(pdvValor);
		}

	}
	
}
