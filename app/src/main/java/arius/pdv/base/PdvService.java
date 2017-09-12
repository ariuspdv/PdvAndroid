package arius.pdv.base;

import java.util.Date;

import arius.pdv.core.AppContext;
import arius.pdv.core.FuncionaisFilters;
import arius.pdv.core.UserException;

public class PdvService {
	
	private Pdv pdv;
	private Venda vendaAtiva;
	private Usuario operadorAtual;
	private static PdvService pdvService;
	private static String msg_padrao = "Operação não permitida. \n" ;
	
	//Daos
	private UsuarioDao usuarioDao;
	private PdvDao pdvDao;
	private UnidadeMedidaDao unidadeMedidaDao;
	private ProdutoDao produtoDao;
	private FinalizadoraDao finalizadoraDao;
	private VendaDao vendaDao;
	private VendaItemDao vendaItemDao;
	private VendaFinalizadoraDao vendaFinalizadoraDao;
	private PdvValorDao pdvValorDao;	
	
	PdvService(){
		Pdv pdv = new Pdv();
		AppContext app = AppContext.get();
		
		//Carga dos Daos com cache
 		this.pdvDao = app.getDao(PdvDao.class);
		this.usuarioDao = app.getDao(UsuarioDao.class);
		this.unidadeMedidaDao = app.getDao(UnidadeMedidaDao.class);
		this.vendaDao = app.getDao(VendaDao.class);
		this.produtoDao = app.getDao(ProdutoDao.class);
		this.finalizadoraDao = app.getDao(FinalizadoraDao.class);
		
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

	public Pdv getPdv(final int id) {

		FuncionaisFilters<Pdv> filters = new FuncionaisFilters<Pdv>() {
			@Override
			public boolean test(Pdv p) {
				return (p.getId() == id); //p.isAberto();
			}
		};

		if (this.pdvDao.listCache(filters).size() != 0) {
			this.pdv = this.pdvDao.listCache(filters).get(0);
		}
		if (this.pdv == null) {
			this.pdv = new Pdv();
		}
		this.vendaAtiva = this.pdv.getVendaAtiva();
		return this.pdv;
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
	}

	private void verificaVendaAberta(Venda venda){
		if (venda.getSituacao() != VendaSituacao.ABERTA)
			throw new UserException(msg_padrao + "Venda já " + venda.getSituacao().name());
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
		verificaPdvAberto(pdv);
		//REGRA 2
		venda.setSituacao(VendaSituacao.ABERTA);
		//REGRA 3
		venda.setPdv(pdv);
		//REGRA 4
		venda.setOperador(operadorAtual);
		//REGRA 5
		venda.setDataHora(new Date());
		//REGRA 6
		vendaAtiva = venda;
		vendaDao.insert(venda);
		//REGRA 7
		pdv.setVendaAtiva(vendaAtiva);
		pdvDao.update(pdv);
	}
	
	public void alteraVenda(Venda venda) {
		//REGRA 1: só pode ser alterada venda aberta
		//REGRA 2: alterar situação para aberta
		//REGRA 3: persitir venda
		//REGRA 4: alterar e persistir pdv
		if (venda.getSituacao() != VendaSituacao.ABERTA)
			throw new UserException(msg_padrao + "Somente é possível alterar uma venda aberta!");
		venda.setSituacao(VendaSituacao.ABERTA);
		vendaDao.update(venda);
		pdv.setVendaAtiva(null);
		pdvDao.update(pdv);
	}
	
	public void removeVenda(Venda venda) {
		//REGRA 1: só pode ser removida venda aberta
		//REGRA 2: remover venda
		//REGRA 3: alterar e persistir pdv
		if (venda.getSituacao() != VendaSituacao.FECHADA)
			throw new UserException(msg_padrao + "Somente é possível excluír uma venda aberta!");
		vendaDao.delete(venda);
		pdv.setVendaAtiva(null);
		pdvDao.update(pdv);
	}
	
	/**
	 * Faz o encerramento da venda executando todas validações
	 * @param venda
	 */
	public void encerraVenda(Venda venda) {
		//REGRA 1: venda deve estar aberta
		//REGRA 2: pdv deve estar aberto
		//REGRA 3: valor restante deve ser igual a zero
		//REGRA 4: definir pdv atual
		//REGRA 5: definir operador
		//REGRA 6: alterar situação para encerrada
		//REGRA 7: persitir venda
		//REGRA 8: passar venda ativa do pdv para null
	}
	
	/**
	 * Faz o cancelamento da venda mantendo a mesma em banco de dados
	 * @param venda
	 */
	public void cancelaVenda(Venda venda) {
		//REGRA 1: venda deve estar encerrada
		//REGRA 2: alterar situação para cancelada
		//REGRA 3: persitir venda
		//REGRA 4: setar venda ativa pdv e persistir pdv
		//REGRA 1
		if (venda.getSituacao() != VendaSituacao.FECHADA)
			throw new UserException(msg_padrao + "Somente é possível cancelar uma venda fechada!");
		//REGRA 2
		venda.setSituacao(VendaSituacao.CANCELADA);
		//REGRA 3
		vendaDao.update(venda);
		//REGRA 4
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
		//REGRA: validar item {validaVendaItem}
		item.setVenda(this.vendaAtiva);
		validaVendaItem(item);
		verificaVendaAberta(item.getVenda());

		vendaItemDao.insert(item);
	}
	
	public void alteraVendaItem(VendaItem item) {
		//REGRA 1: validar item {validaVendaItem}
		//REGRA 2: persistir dados do item
		//REGRA 1
		validaVendaItem(item);
		//REGRA 2
		vendaItemDao.update(item);
	}
	
	public void removeVendaItem(VendaItem item) {
		//REGRA 1: venda deve estar aberta
		//REGRA 2: persistir dados do item
		//REGRA 1
		verificaVendaAberta(item.getVenda());
		//REGRA 2
		vendaItemDao.delete(item);
	}

	//************* Operações sobre PDV ************//
	
	public void abreCaixa(Usuario operador, double saldoInicial) {
		//REGRA: caixa deve estar fechado
		//REGRA: saldo inicial deve ser maior ou igual a zero
		if (pdv == null){
			throw new UserException(msg_padrao + "Pdv não encontrado ou não configurado!");
		}
		if (pdv.isAberto()){
			throw new UserException(msg_padrao + "O caixa já está aberto!");
		}
		if (saldoInicial <= 0){
			throw new UserException(msg_padrao + "O saldo Iniciao não pode ser menor ou igual a zero!");			
		}
		if (operador == null || operador.getId() == 0){
			throw new UserException(msg_padrao + "Informar um operador para abrir o caixa");
		}
		pdv.setAberto(true);
		pdv.setOperador(operador);
		pdv.setSaldoDinheiro(saldoInicial);
		pdvDao.update(pdv);
		
	}
	
	/**
	 * Retorna o saldo da finalizadora seguindo a regra: movimentos da finalizadora -
	 * valores já sangrados
	 * @param finalizadora
	 * @return
	 */
	public double calculaSaldoFinalizadora(Finalizadora finalizadora) {
		//REGRA: seguir regra
		return 0;
	}
	
	public void sangraCaixa(Finalizadora finalizadora, double valor, Usuario sangrador) {
		//REGRA: valor da sangria não pode ser maior que o saldo da finalizadora
	}
	
	private void sangriaTotal(Usuario sangrador) {
		//REGRA: executa a sangria total para o fechamento do caixa
	}

	public void fechaCaixa(Usuario fechador) {
		//REGRA: pdv deve estar aberto
		//REGRA: fazer sangria total (verificar configuração)
		if (fechador == null || fechador.getId() == 0){
			throw new UserException(msg_padrao + "Informar um operador para fechar o caixa");
		}		
		
		verificaPdvAberto(pdv);
			
		pdv.setSaldoDinheiro(0);
		pdv.setAberto(false);
		pdv.setOperador(null);
		pdv.setVendaAtiva(null);
		PdvService.get().pdvDao.update(PdvService.get().getPdv(13));
		
	}
	
	public void reforcoCaixa(double valor) {
		//REGRA: pdv deve estar aberto
	}
	
	public void trocaOperador(Usuario novoOperador) {
		//REGRA: pdv deve estar aberto
	}
	
	/**
	 * Utilizar somente em modo venda varejo (lojas / supermercados)
	 * @param venda
	 */
	public void defineVendaAtiva(Venda venda) {
		//REGRA: não pode ter venda definida (campo deve estar null) em pdv
		//REGRA: definir pdv atual
		//REGRA: definir operador
		this.vendaAtiva = venda;
	}

}
