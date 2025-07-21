package br.com.sankhya.importacaosystock;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class Autosol implements AcaoRotinaJava {

	private String msgErro = "";
	public BigDecimal nusystock = new BigDecimal(0);

	public BigDecimal VLRVENDA = new BigDecimal(0);

	public BigDecimal nunotaProd = BigDecimal.ZERO;

	AuthenticationInfo auth = AuthenticationInfo.getCurrent();

	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();

	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();

	NativeSql nativeSql = new NativeSql(this.jdbc);

	StringBuilder stringBuild = new StringBuilder();

	public String control = "";

	public String ven = "";

	public String volume = "";

	public String Descrproduto = "";
	StringBuffer mensagem = new StringBuffer();

	public String mensagefinal = "";

	public BigDecimal PA_Produto = new BigDecimal(0);
	public BigDecimal codvend = new BigDecimal(0);
	public StringBuilder notasGeradas = new StringBuilder(); 

	public void doAction(ContextoAcao ctx) throws Exception {

		Registro line = ctx.getLinhas()[0];

		geraNota(line, ctx);

		if (this.msgErro.equals("")) {
			ctx.setMensagemRetorno("Solicitações Criadas com sucesso! ");
			atualizar(line,ctx);
		} else {
			ctx.setMensagemRetorno(this.msgErro);
		}

	}
	
	
	private void atualizar (Registro line, ContextoAcao ctx) throws Exception {
		BigDecimal nusystock = (BigDecimal) line.getCampo("NUSYTOCK");
		final JapeWrapper impDAO = JapeFactory.dao("AD_IMPSYSTOCK");
		final DynamicVO impVo = impDAO.findOne("NUSYTOCK = ?", new Object[]{nusystock});
		  FluidUpdateVO impUpdVO = impDAO.prepareToUpdate(impVo);
          impUpdVO.set("DHPROC", new Timestamp(System.currentTimeMillis()));
          impUpdVO.set("LS",notasGeradas.toString());
          impUpdVO.update();
	}
	
	class DadosInsercao {
	    BigDecimal produto;
	    String val;
	    BigDecimal CodLoc;
	    String tipo;
	    BigDecimal QTD;
	    BigDecimal vlrvenda;
	    String vol;
	    BigDecimal vlrvenda2;
	    BigDecimal Origem;
	    BigDecimal Sequencia;

	    public DadosInsercao(BigDecimal produto, String val, BigDecimal CodLoc, String tipo, BigDecimal QTD, 
	                         BigDecimal vlrvenda, String vol, BigDecimal vlrvenda2, BigDecimal Origem, BigDecimal Sequencia) {
	        this.produto = produto;
	        this.val = val;
	        this.CodLoc = CodLoc;
	        this.tipo = tipo;
	        this.QTD = QTD;
	        this.vlrvenda = vlrvenda;
	        this.vol = vol;
	        this.vlrvenda2 = vlrvenda2;
	        this.Origem = Origem;
	        this.Sequencia = Sequencia;
	    }
	}


	private void geraNota(Registro line, ContextoAcao ctx) throws Exception {

		BigDecimal nusystock = (BigDecimal) line.getCampo("NUSYTOCK");
	
		NativeSql sql = new NativeSql(this.jdbc);

		QueryExecutor rset4 = ctx.getQuery();
		QueryExecutor rset1 = ctx.getQuery();
		QueryExecutor rset3 = ctx.getQuery();
		QueryExecutor rset2 = ctx.getQuery();
		ResultSet rset = null;
		//List<DadosInsercao> listaInsercoes = new ArrayList<>();

		try {

			sql.appendSql(" SELECT DISTINCT ");
			sql.appendSql(" ORIGEM,CODTIPOPER ");
			sql.appendSql(" FROM ");
			sql.appendSql(" AD_ITESYSTOCK ");
			sql.appendSql(" WHERE ");
			sql.appendSql(" NUSYTOCK =" + nusystock);
			rset = sql.executeQuery();
			StringBuffer sqlIt3 = new StringBuffer();
			while (rset.next()) {
				String operacao = rset.getString("CODTIPOPER");
				BigDecimal oper = new BigDecimal(operacao);
				String origem = rset.getString("ORIGEM");
				BigDecimal orig = new BigDecimal(origem);
				BigDecimal Nota = criaCabecalho(orig, oper, new BigDecimal(158690), new BigDecimal(2060000),
						new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "A", "F",
						new BigDecimal(800000000));
				
				if (notasGeradas.length() > 0) {
		            notasGeradas.append(", ");
		        }
		        notasGeradas.append(Nota.toString());

				sqlIt3.setLength(0);

				sqlIt3.append(
						" SELECT STOCK.NUSYTOCK AS NUSYTOCK, STOCK.NUSEQ AS SEQUENCIA, STOCK.ORIGEM AS ORIG , STOCK.CODPROD AS CODPROD, STOCK.QTD AS QTD, PRO.CODVOL AS CODVOL, STOCK.CODTIPOPER AS TOPP ");
				sqlIt3.append(" FROM AD_ITESYSTOCK STOCK ");
				sqlIt3.append(" INNER JOIN TGFPRO PRO ON PRO.CODPROD = STOCK.CODPROD ");
				sqlIt3.append(" WHERE STOCK.NUSYTOCK = " + nusystock + " AND STOCK.CODTIPOPER = " + oper);

				rset1.nativeSelect(sqlIt3.toString());

				while (rset1.next()) {
					try {
						System.out.println("Linha Atual " + rset1.getString("CODPROD") + rset1.getString("ORIG")
								+ "A TOP ATUAL é " + rset1.getString("TOPP"));
						String Codprod = rset1.getString("CODPROD");
						BigDecimal produto = new BigDecimal(Codprod);
						String Qtd = rset1.getString("QTD");
						BigDecimal QTD = new BigDecimal(Qtd);
						String vol = rset1.getString("CODVOL");

						String origi = rset1.getString("ORIG");
						BigDecimal Origem = new BigDecimal(origi);
						String seq = rset1.getString("SEQUENCIA");
						BigDecimal Sequencia = new BigDecimal(seq);
						BigDecimal CodLoc = retornaLocal(produto, Origem, QTD);

						BigDecimal vlrvenda = retornapreco(produto);
						String val = retornaval(produto, CodLoc);
						
						
						if ((CodLoc != null) || (CodLoc.compareTo(BigDecimal.ZERO) != 0))
							{
							insertnaite(Nota, produto, val, CodLoc, "V", QTD, vlrvenda, vol, vlrvenda, Origem, Sequencia);
							}
						 //listaInsercoes.add(new DadosInsercao(produto, val, CodLoc, "V", QTD, vlrvenda, vol, vlrvenda, Origem, Sequencia));

						System.out.println("INSERIU NA NOTA");
					} catch (Exception e) { 
						System.err.println("Erro ao inserir na nota: " + e.getMessage());
						e.printStackTrace(); // Opcional: imprime o erro completo no log
						//StringWriter sw = new StringWriter();
						//PrintWriter pw = new PrintWriter(sw);
						continue;
						//e.printStackTrace(pw);
						//msgErro = sw.toString();
						

					}
				}
			}
		}

		catch (Exception e) {
			System.out.println("Deu pau");
			System.out.println("O erro é" + e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			msgErro = sw.toString();

		} finally {
			rset.close();
			rset1.close();
			rset2.close();
			rset3.close();
			rset4.close();
		}

	}

	public BigDecimal retornapreco(BigDecimal Produto) throws Exception {
		BigDecimal preco = new BigDecimal(0);
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		sqlite1.append("SELECT CUS.CUSMED AS CUSTO ");
		sqlite1.append("FROM TGFCUS CUS ");
		sqlite1.append("JOIN ( ");
		sqlite1.append("    SELECT I.CODPROD, MAX(C.DTENTSAI) AS DTENTSAI_MAX ");
		sqlite1.append("    FROM TGFITE I ");
		sqlite1.append("    JOIN TGFCAB C ON C.NUNOTA = I.NUNOTA ");
		sqlite1.append("    WHERE ");
		sqlite1.append("        C.TIPMOV = 'C' ");
		sqlite1.append("        AND C.STATUSNOTA = 'L' ");
		sqlite1.append("        AND YEAR(C.DTNEG) > 2021 ");
		sqlite1.append("        AND I.CODPROD = " + Produto);
		sqlite1.append("    GROUP BY I.CODPROD ");
		sqlite1.append(") ULTIMA_ENT ");
		sqlite1.append("    ON CUS.CODPROD = ULTIMA_ENT.CODPROD ");
		sqlite1.append("   AND CUS.DTATUAL = ULTIMA_ENT.DTENTSAI_MAX ");
		sqlite1.append("ORDER BY CUS.CODPROD ASC;");
		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		if (query.next()) {
			String Price = query.getString("CUSTO");
			preco = new BigDecimal(Price);
			System.out.println("O preço é" + preco);
		}
		query.close();

		return preco;
	}

	public String retornaval(BigDecimal Produto, BigDecimal Codlocal) throws Exception {
		String validade = "";
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		sqlite1.append(" SELECT ISNULL(CONTROLE,'') AS CONTROLE");
		sqlite1.append(" FROM TGFEST ");
		sqlite1.append(" WHERE CODPROD = " + Produto + " AND CODLOCAL =" + Codlocal);
		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		if (query.next()) {
			validade = query.getString("CONTROLE");
		}
		query.close();

		return validade;
	}

	public BigDecimal retornaLocal(BigDecimal Produto, BigDecimal Origem, BigDecimal qtd) throws Exception {
		BigDecimal local = new BigDecimal(0);
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		sqlite1.append(" SELECT TOP 1 EST.CODLOCAL");
		sqlite1.append(" FROM TGFEST EST INNER JOIN TGFLOC LOC ON LOC.CODLOCAL = EST.CODLOCAL");
		sqlite1.append(" WHERE LOC.AD_SYSTOCK = 'S' AND EST.CODPROD = " + Produto + "AND EST.CODEMP = " + Origem
				+ "AND (EST.ESTOQUE-EST.RESERVADO)> " + qtd);
		System.out.println("O Select do codlocal:" + sqlite1.toString());
		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		if (query.next()) {
			String Loc = query.getString("CODLOCAL");
			System.out.println("O Codlocal é:" + Loc);
			local = new BigDecimal(Loc);
		}
		query.close();

		return local;
	}

	public void insertnaite(BigDecimal nuNota, BigDecimal codprod, String controle, BigDecimal Codlocal, String uso,
			BigDecimal qtd, BigDecimal unitario, String Volume, BigDecimal vlrvenda, BigDecimal emp, BigDecimal seq)
			throws Exception {

		System.out.println("INICIO DO METODO DA ITE");
		System.out.println("nunota" + nuNota);
		System.out.println("codprod" + codprod);
		System.out.println("controle" + controle);
		System.out.println("qtd" + qtd);
		System.out.println("unitario" + unitario);
		System.out.println("volume" + Volume);
		System.out.println("vlrvenda" + vlrvenda);

		CACHelper cacHelper = new CACHelper();
		JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
		Collection<PrePersistEntityState> itensNota = new ArrayList<>();
		DynamicVO itemVO = (DynamicVO) this.dwfEntityFacade.getDefaultValueObjectInstance("ItemNota");
		itemVO.setProperty("NUNOTA", nuNota);
		itemVO.setProperty("CODEMP", emp);
		itemVO.setProperty("CODPROD", codprod);
		itemVO.setProperty("SEQUENCIA", seq);
		itemVO.setProperty("CODLOCALORIG", Codlocal);
		itemVO.setProperty("USOPROD", uso);
		itemVO.setProperty("CONTROLE", controle);
		itemVO.setProperty("CODUSU", new BigDecimal(0));
		itemVO.setProperty("QTDNEG", qtd);
		itemVO.setProperty("VLRUNIT", vlrvenda);
		itemVO.setProperty("VLRTOT", vlrvenda.multiply(qtd));
		itemVO.setProperty("CODVOL", Volume);
		itemVO.setProperty("ATUALESTOQUE", new BigDecimal(1));
		itemVO.setProperty("RESERVA", "S");

		PrePersistEntityState itePreState = PrePersistEntityState.build(this.dwfEntityFacade, "ItemNota", itemVO);
		itensNota.add(itePreState);
		cacHelper.incluirAlterarItem(nuNota, auth, itensNota, true);
	}

	@SuppressWarnings("null")
	public BigDecimal criaCabecalho(BigDecimal empresa, BigDecimal tipoOperacao, BigDecimal parceiro,
			BigDecimal natureza, BigDecimal codTipVenda, BigDecimal codCenCus, BigDecimal modeloNota, BigDecimal codUsu,
			String Statusnota, String Fob, BigDecimal codproj) throws Exception {

		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		EntityVO padraoNPVO = null;
		try {
			padraoNPVO = dwfFacade.getDefaultValueObjectInstance("CabecalhoNota");
		} catch (Exception e) {
			throw new Exception(
					"Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
		}
		DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO;
		System.out.println("modeloNota" + modeloNota);
		// System.out.println("cabecalhoVO" + cabecalhoVO);
		System.out.println("log_parceiro" + parceiro);
		cabecalhoVO.setProperty("CODTIPOPER", tipoOperacao);
		cabecalhoVO.setProperty("TIPMOV", "J");
		cabecalhoVO.setProperty("CODPARC", parceiro);
		cabecalhoVO.setProperty("CODCENCUS", codCenCus);
		cabecalhoVO.setProperty("CODTIPVENDA", codTipVenda);
		cabecalhoVO.setProperty("CODEMP", empresa);
		cabecalhoVO.setProperty("CODNAT", natureza);
		cabecalhoVO.setProperty("NUMNOTA", new BigDecimal(0));
		cabecalhoVO.setProperty("APROVADO", "N");
		cabecalhoVO.setProperty("CODUSU", codUsu);
		cabecalhoVO.setProperty("CIF_FOB", "S");
		cabecalhoVO.setProperty("OBSERVACAO", "Gerado por Rotina do Systock");
		cabecalhoVO.setProperty("PENDENTE", "S");
		cabecalhoVO.setProperty("STATUSNOTA", Statusnota);
		cabecalhoVO.setProperty("CIF_FOB", Fob);
		cabecalhoVO.setProperty("CODPROJ", codproj);
		dwfFacade.createEntity("CabecalhoNota", (EntityVO) cabecalhoVO);
		nunotaProd = cabecalhoVO.asBigDecimal("NUNOTA");

		// System.out.println("Cabe" + cabecalhoVO);
		return nunotaProd;
	}

}
