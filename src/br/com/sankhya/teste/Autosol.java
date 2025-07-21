package br.com.sankhya.teste;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import br.com.sankhya.ws.ServiceContext;
import java.net.URLEncoder;

import java.util.Collection;

public class Autosol implements AcaoRotinaJava {

	 private String msgErro = "";
	 public BigDecimal nusystock = new BigDecimal(0);
	  
	  private BigDecimal nuNota = new BigDecimal(0);
	  
	  private BigDecimal nuNotaPedido = new BigDecimal(0);
	  
	  private BigDecimal newNota = new BigDecimal(0);
	  
	  private BigDecimal nunota = new BigDecimal(0);
	  
	  private BigDecimal top = new BigDecimal(0);
	  
	  public BigDecimal VLRVENDA = new BigDecimal(0);
	  
	  public BigDecimal nunotaProd = BigDecimal.ZERO;
	  
	  AuthenticationInfo auth = AuthenticationInfo.getCurrent();
	  
	  EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	  
	  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	  
	  NativeSql nativeSql = new NativeSql(this.jdbc);
	  
	  StringBuilder stringBuild = new StringBuilder();
	  
	  private BigDecimal Empresa = new BigDecimal(0);
	  
	  public String control = "";
	  
	  public String ven = "";
	  
	  public String volume = "";
	  
	  private BigDecimal P_produto = new BigDecimal(0);
	  
	  private String P_tipo = "";
	  
	  private String P_codlocal = "";
	  
	  private String P_Quantidade = "";
	  
	  public String Descrproduto = "";
	  StringBuffer mensagem = new StringBuffer();
	  
	  public String mensagefinal = "";
	  
	  public BigDecimal PA_Produto = new BigDecimal(0);
	  public BigDecimal codvend = new BigDecimal(0);

	public void doAction(ContextoAcao ctx) throws Exception {


		Registro line = ctx.getLinhas()[0];

		geraNota(line, ctx);

		
	}

	private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
		
	
	     try {

	    	 for (int i = 0; i < 2; i++) {
	    		  BigDecimal nunota = criaCabecalho(new BigDecimal(5),new BigDecimal(41), new BigDecimal(1199),new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "A", "F", new BigDecimal(800000000));
	    		}
	  
	    
	    
	      }
		catch (Exception e) {
			 System.out.println("Deu pau");
			 
		} finally {

}

		

	}

	public void insertnaite(BigDecimal nuNota, BigDecimal codprod, 
			String controle, BigDecimal Codlocal, String uso, 
			BigDecimal qtd, BigDecimal unitario, 
			String Volume, BigDecimal vlrvenda, BigDecimal emp, int seq) throws Exception {

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
	public BigDecimal criaCabecalho(BigDecimal empresa, BigDecimal tipoOperacao, BigDecimal parceiro, BigDecimal natureza, BigDecimal codTipVenda, BigDecimal codCenCus, BigDecimal modeloNota, BigDecimal codUsu, String Statusnota, String Fob, BigDecimal codproj) throws Exception {

		EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
		EntityVO padraoNPVO = null;
		try {
			padraoNPVO = dwfFacade.getDefaultValueObjectInstance("CabecalhoNota");
		} catch (Exception e) {
			throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
		}
		DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO;
		System.out.println("modeloNota" + modeloNota);
		System.out.println("cabecalhoVO" + cabecalhoVO);
		System.out.println("log_parceiro" + parceiro);
		cabecalhoVO.setProperty("CODTIPOPER", tipoOperacao);
		cabecalhoVO.setProperty("TIPMOV", "P");
		cabecalhoVO.setProperty("CODPARC", parceiro);
		cabecalhoVO.setProperty("CODCENCUS", codCenCus);
		cabecalhoVO.setProperty("CODTIPVENDA", codTipVenda);
		cabecalhoVO.setProperty("CODEMP", empresa);
		cabecalhoVO.setProperty("CODNAT", natureza);
		cabecalhoVO.setProperty("NUMNOTA", new BigDecimal(0));
		cabecalhoVO.setProperty("APROVADO", "N");
		cabecalhoVO.setProperty("CODUSU", codUsu);
		cabecalhoVO.setProperty("CIF_FOB", "S");
		cabecalhoVO.setProperty("PENDENTE", "S");
		cabecalhoVO.setProperty("STATUSNOTA", Statusnota);
		cabecalhoVO.setProperty("CIF_FOB", Fob);
		cabecalhoVO.setProperty("CODPROJ", codproj);
		cabecalhoVO.setProperty("AD_ENTREGA", "N");
		cabecalhoVO.setProperty("OBSERVACAO", "Gerado por Botao de Acao de Teste");
		dwfFacade.createEntity("CabecalhoNota", (EntityVO) cabecalhoVO);
		nunotaProd = cabecalhoVO.asBigDecimal("NUNOTA");

		System.out.println("Cabe" + cabecalhoVO);
		return nunotaProd;
	}
	
}
