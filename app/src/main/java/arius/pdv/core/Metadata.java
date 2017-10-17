package arius.pdv.core;

public class Metadata {
	
	public static final int ULTIMA_VERSAO_METADATA = 1;
	
	public static String[] metadata_execute(){
		return versao(ULTIMA_VERSAO_METADATA);
	}
	
	private static String[] versao(int versao_metada){		
		String[] vcmd = {};
		switch (versao_metada){
			case 1 : vcmd = versao1();
					break;
		}
		return vcmd;
	}
		
	private static String[] versao1(){
		String[] vcmd = {"CREATE TABLE usuarios(" +
			    "	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    nome VARCHAR2(50) NOT NULL, " +
			    "    login VARCHAR2(15) NOT NULL UNIQUE, " +
			    "    senha VARCHAR2(15) NOT NULL, " +
			    "    tipo INTEGER NOT NULL);",

				"CREATE TABLE empresas(" +
				"    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"    razao_social VARCHAR2(100), " +
				"    nome_fantasia VARCHAR2(100)); ",

			    "CREATE TABLE unidades_medidas(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    descricao VARCHAR2(50), " +
			    "    sigla VARCHAR2(2), " +
			    "    fracionada BOOLEAN); ",

			    "CREATE TABLE finalizadoras(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    descricao VARCHAR2(50), " +
			    "    tipo INT, " +
			    "    permitetroco BOOLEAN DEFAULT 0, " +
			    "    aceitasangria BOOLEAN DEFAULT 0); ",

				"CREATE TABLE produtos_categorias(" +
				"    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				"    descricao VARCHAR2(50), " +
				"    produtocategoria_id INTEGER REFERENCES produtos_categorias(id)); ",

			    "CREATE TABLE produtos(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    codigo INT64 NOT NULL, " +
			    "    descricao VARCHAR2(100), " +
			    "    descricaoreduzida VARCHAR2(50), " +
			    "    unidademedida_id INTEGER REFERENCES unidades_medidas(id)," +
				"	 produtocategoria_id INTEGER REFERENCES produtos_categorias(id)," +
				"	 principal BOOLEAN DEFAULT 0); ",

                "CREATE TABLE produtos_precos(" +
                        "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "    empresa_id INTEGER REFERENCES empresas(id)," +
                        "	 produto_id INTEGER REFERENCES produtos(id)," +
                        "    valor_venda DOUBLE(15, 2)); ",

				"CREATE TABLE historicos(" +
						"    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
						"    descricao VARCHAR2(50), " +
						"    tipo INT); ",

			    "CREATE TABLE pdvs(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			    "    saldodinheiro DOUBLE(15, 2) DEFAULT 0, " +
			    "    operador_id INTEGER REFERENCES usuarios(id), " +
			    "    vendaativa_id INTEGER REFERENCES vendas(id), " +
			    "    status INT NOT NULL," +
			    "	 codigo_pdv INTEGER," + 
			    "	 dataabertura DATETIME, " +
				"	 empresa_id INTEGER REFERENCES empresas(id)); ",

			    "CREATE TABLE pdvs_valores(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    data_hora DATETIME, " +
			    "    pdv_id INTEGER REFERENCES pdvs(id), " +
			    "    tipo INT, " +
			    "    finalizadora_id INTEGER REFERENCES finalizadoras(id), " +
			    "    usuario1_id INTEGER REFERENCES usuarios(id), " +
			    "    usuario2_id INTEGER REFERENCES usuarios(id), " +
			    "    valor DOUBLE(15, 2)); ",

			    "CREATE TABLE vendas(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    pdv_id INTEGER REFERENCES pdvs(id), " +
			    "    operador_id INTEGER REFERENCES usuarios(id), " +
			    "    situacao INT, " +
			    "    data_hora DATETIME, " +
			    "    cpf_cnpj VARCHAR2(14), " +
			    "    valor_troco DOUBLE(15, 2)); ",

			    "CREATE TABLE vendas_Itens(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    venda_id INTEGER NOT NULL REFERENCES vendas(id), " +
			    "    produto_id INTEGER NOT NULL, " +
			    "	 qtde DOUBLE(12, 3), " +
			    "    valortotal DOUBLE(15, 2), " +
			    "    desconto DOUBLE(15, 4), " +
			    "    acrescimo DOUBLE(15, 4)); ",

			    "CREATE TABLE vendas_finalizadoras(" +
			    "    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
			    "    venda_id INTEGER NOT NULL REFERENCES vendas(id), " +
			    "    finalizadora_id INTEGER NOT NULL, " +
			    "    valor DOUBLE(15, 2)); ",			    
			   //O comando abaixo é para atualizar a versão do banco;				
			    "PRAGMA user_version = " + ULTIMA_VERSAO_METADATA + ";"};		
		return vcmd;
	}	

}
