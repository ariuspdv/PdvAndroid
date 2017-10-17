package arius.pdv.base;

import java.util.List;

import arius.pdv.core.AppContext;
import arius.pdv.core.Entity;
import arius.pdv.core.FuncionaisFilters;

public class Produto extends Entity {
	
	private long codigo; //podendo ser barras / interno
	private String descricao;
	private String descricaoReduzida;
	private UnidadeMedida unidadeMedida;
	private ProdutoCategoria produtoCategoria;
	private boolean principal = false;

	public long getCodigo() {
		return codigo;
	}
	public void setCodigo(long codigo) {
		this.codigo = codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricaoReduzida() {
		return descricaoReduzida;
	}
	public void setDescricaoReduzida(String descricaoReduzida) {
		this.descricaoReduzida = descricaoReduzida;
	}
	public UnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}
	public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public ProdutoCategoria getProdutoCategoria() {
		return produtoCategoria;
	}

	public void setProdutoCategoria(ProdutoCategoria produtoCategoria) {
		this.produtoCategoria = produtoCategoria;
	}

	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	public double getValor_Venda(final Pdv pdv){
		if (pdv == null)
			return 0.00;

		FuncionaisFilters<ProdutoPreco> filtro = new FuncionaisFilters<ProdutoPreco>() {
			@Override
			public boolean test(ProdutoPreco p) {
				return p.getEmpresa_id() == pdv.getEmpresa().getId() &&
					   p.getProduto_id() == getId();
			}
		};
		List<ProdutoPreco> v_aux = AppContext.get().getDao(ProdutoPrecoDao.class).listCache(filtro);
		double v_retorno = 0.00;
		if (v_aux != null)
			v_retorno = v_aux.get(0).getValor_venda();

		return v_retorno;
	}

}
