package br.com.solicitacao.vendedor;

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

public class criar_sol implements AcaoRotinaJava {

	 private String msgErro = "";
	  
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

		System.out.println("LOGPROD:" + ctx.getParam("PROD"));
		System.out.println("LOGQTD:" + ctx.getParam("QTD"));
		System.out.println("LOGTIPO:" + ctx.getParam("TIPO"));
		System.out.println("LOGLOCAL:" + ctx.getParam("CODLOCAL"));
		System.out.println("INICIO");

		P_produto = new BigDecimal(8);

		System.out.println("Inicia a Parada" + ctx.getParam("TIPO"));

		P_codlocal = ctx.getParam("CODLOCAL").toString();

		P_Quantidade = ctx.getParam("QTD").toString();

		System.out.println("PEGANDO OS PARAMS");

		Registro line = ctx.getLinhas()[0];

		geraNota(line, ctx);

		if (this.msgErro.equals("")) {
			ctx.setMensagemRetorno("A solicitação concluida! "+nunotaProd + "/////" + mensagem);
		    
			mensagefinal =  mensagem.toString();
			System.out.println("A MSG ENCODADA"+mensagefinal);
			enviar_msg_polichat();
		} else {
			ctx.setMensagemRetorno(this.msgErro);
		}

	}

	private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
		
		String qtdString, codprodstring;
	    codvend = (BigDecimal)line.getCampo("CODVEND");
	    this.P_tipo = ctx.getParam("TIPO").toString();
	    System.out.println("codvend: " + codvend);
	    System.out.println("Inicia a Parada");
	    this.nunota = (BigDecimal)line.getCampo("NUNOTA");
	    Object param = ctx.getParam("QTD");
	    if (param instanceof Integer) {
	      qtdString = param.toString();
	    } else {
	      qtdString = (String)param;
	    } 
	    BigDecimal qtd = new BigDecimal(qtdString);
	    String pHistorico = ctx.getParam("PROD").toString();
	    BigDecimal P_prod = new BigDecimal(pHistorico);
	    String plocais = ctx.getParam("CODLOCAL").toString();
	    BigDecimal P_Location = new BigDecimal(plocais);
	    Object param2 = ctx.getParam("CODPROD");
	    if (param instanceof Integer) {
	      codprodstring = param.toString();
	    } else {
	      codprodstring = (String)param2;
	    } 
	    
	    switch (P_tipo) {
		case "1":
			Empresa = new BigDecimal(10);
			break;
		case "4":
		Empresa = new BigDecimal(10);
		break;
		case "2":
		Empresa = new BigDecimal(5);
		break;
		case "6":
		Empresa = new BigDecimal(5);
		break;
		case "5":
		Empresa = new BigDecimal(11);
		break;
		case "7":
		Empresa = new BigDecimal(11);
		break;
		case "8":
		Empresa = new BigDecimal(9);
		break;
		case "3":
		Empresa = new BigDecimal(9);
		break;
		case "9":
			Empresa = new BigDecimal(9);
			break;
		case "10":
			Empresa = new BigDecimal(9);
			break;
		case "11":
			Empresa = new BigDecimal(9);
			break;
		case "12":
			Empresa = new BigDecimal(9);
			break;
		default:
			
			break;
		}
	    
	    NativeSql sql = new NativeSql(this.jdbc);

	    QueryExecutor rset4 = ctx.getQuery();
	    QueryExecutor rset1 = ctx.getQuery();
	    QueryExecutor rset3 = ctx.getQuery();
	    QueryExecutor rset2 = ctx.getQuery();
	    ResultSet rset = null;
	    
	     try {
	    BigDecimal codprodi = new BigDecimal(codprodstring);
	    System.out.println("codprodi: " + codprodi);
	   
	    sql.appendSql("SELECT CUS.CUSMED AS CUSTO ");
	    sql.appendSql("FROM TGFCUS CUS ");
	    sql.appendSql("JOIN ( ");
	    sql.appendSql("    SELECT I.CODPROD, MAX(C.DTENTSAI) AS DTENTSAI_MAX ");
	    sql.appendSql("    FROM TGFITE I ");
	    sql.appendSql("    JOIN TGFCAB C ON C.NUNOTA = I.NUNOTA ");
	    sql.appendSql("    WHERE ");
	    sql.appendSql("        C.TIPMOV = 'C' ");
	    sql.appendSql("        AND C.STATUSNOTA = 'L' ");
	    sql.appendSql("        AND YEAR(C.DTNEG) > 2021 ");
	    sql.appendSql("        AND I.CODPROD = " + P_prod);
	    sql.appendSql("    GROUP BY I.CODPROD ");
	    sql.appendSql(") ULTIMA_ENT ");
	    sql.appendSql("    ON CUS.CODPROD = ULTIMA_ENT.CODPROD ");
	    sql.appendSql("   AND CUS.DTATUAL = ULTIMA_ENT.DTENTSAI_MAX ");
	    sql.appendSql("ORDER BY CUS.CODPROD ASC;");
	     rset = sql.executeQuery();
	    
	    if (rset.next()) {
	      System.out.println("VALOR " + rset.getString("CUSTO"));
	      this.VLRVENDA = rset.getBigDecimal("CUSTO");
	    } 
	    System.out.println("ANTES DO CODPROD CODVOLPRODUTO" + this.P_produto);
	    StringBuffer sqlIt3 = new StringBuffer();
	    sqlIt3.append(" SELECT CODVOL");
	    sqlIt3.append(" FROM ");
	    sqlIt3.append(" TGFPRO ");
	    sqlIt3.append(" WHERE ");
	    sqlIt3.append(" CODPROD =" + P_prod);
	
	    System.out.println("DEPOIS DO CODPROD CODVOL");
	    rset1.nativeSelect(sqlIt3.toString());
	    if (rset1.next()) {
	      System.out.println("CODVOL PEGANDO" + rset1.getString("CODVOL"));
	      this.volume = rset1.getString("CODVOL");
	    } 
	    System.out.println("CODVOL PEGADOU" + this.volume);
	    System.out.println("VEN ATUAL" + this.nunota);
	    StringBuffer sqlIt = new StringBuffer();

	    sqlIt.append(" SELECT VEN.APELIDO ");
	    sqlIt.append(" FROM ");
	    sqlIt.append(" TGFCAB CAB ");
	    sqlIt.append(" INNER JOIN TGFVEN VEN ON VEN.CODVEND=CAB.CODVEND");
	    sqlIt.append(" WHERE ");
	    sqlIt.append(" CAB.NUNOTA = " + this.nunota);
	    System.out.println("VEN C" + this.nunota);
	    
	    rset2.nativeSelect(sqlIt.toString());
	    if (rset2.next()) {
	      
	      this.ven = rset2.getString("APELIDO");
	    } 
	    
	    System.out.println("INICIDO DO SQL DO CONTROLE");
	    System.out.println("A query antes"+P_prod+"empresa"+Empresa+"Location"+P_Location );
	   
	    
	    StringBuffer Controlit = new StringBuffer();

	    Controlit.append(" SELECT CONTROLE ");
	    Controlit.append(" FROM ");
	    Controlit.append(" TGFEST ");
	    Controlit.append(" WHERE ");
	    Controlit.append(" CODPROD =" + P_prod + " AND CODEMP = " + Empresa + " AND CODLOCAL = " + P_Location);
	    rset3.nativeSelect(Controlit.toString());
	    System.out.println("A query depois"+P_prod+"empresa"+Empresa+"Location"+P_Location );
	    if (rset3.next()) {
	      System.out.println("PEGANDO O SQL DO CONTROLE" + rset3.getString("CONTROLE"));
	      control = rset3.getString("CONTROLE");
	    } 
	    System.out.println("INICIO DO SWITCH");
	    System.out.println("LOGPTIPO: " + this.P_tipo);
	    
	    StringBuffer Prod_info = new StringBuffer();
	    Prod_info.append(" SELECT DESCRPROD");
	    Prod_info.append(" FROM");
	    Prod_info.append(" TGFPRO");
	    Prod_info.append(" WHERE");
	    Prod_info.append(" CODPROD = " + P_prod );
	    rset4.nativeSelect(Prod_info.toString());
	    if (rset4.next())
	    {
	    	Descrproduto = rset4.getString("DESCRPROD");
	    }
	      }
		catch (Exception e) {
			 System.out.println("Deu pau");
			 
		} finally {
			rset.close();
			rset1.close();
			rset2.close();
			rset3.close();
			rset4.close();
}
		System.out.println("INICIO DO SWITCH");
		System.out.println("LOGPTIPO: " + P_tipo);
		switch (P_tipo) {
			
		case "1":
				this.newNota = criaCabecalho(new BigDecimal(10), new BigDecimal(8000), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA CD-->> MATRIZ. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
		        System.out.println("Criando o cabecalho");
		        System.out.println("INICIO DO ITE");
		        System.out.println("Valor thisnewnota" + this.newNota);
		        System.out.println("Valor thisnewnota" + this.VLRVENDA);
		        insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(10));
		        System.out.println("ANTES DO ITE");
		        System.out.println("Valor nunotaprod" + this.nunotaProd);
		        mensagem.append(" *Solicitação de Item:* ");
		        mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DO Centro de Distribuição PARA MATRIZ* ");
				System.out.println("Msg para polichat" + mensagem);
				
				break;
			case "4":
				this.newNota = criaCabecalho(new BigDecimal(10), new BigDecimal(7800), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA CD-->> MATRIZ. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(10));
				mensagem.append(" *Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia Do CENTRO DE DISTRIBUIÇÃO PARA LOJA ZL* ");
				System.out.println("Msg para polichat" + mensagem);
				break;
			case "2":
				this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(7996), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA Matriz->> CD. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(5));
				mensagem.append("*Solicitação de Item* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA Matriz PARA Centro de Distribuição* ");
				System.out.println("Msg para polichat" + mensagem);
				break;

			case "6":
				this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(8001), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MTZ-->> ZL. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(5));
				mensagem.append("*Solicitação de Item*");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+ Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA MATRIZ PARA LOJA ZL* ");
				System.out.println("Msg para polichat" + mensagem);

				break;

			case "5":
				this.newNota = criaCabecalho(new BigDecimal(11), new BigDecimal(8002), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> CD. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(11));
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DO ZL PARA CENTRO DE DISTRIBUICAO* ");
				System.out.println("Msg para polichat" + mensagem);

				break;
			case "7":
				this.newNota = criaCabecalho(new BigDecimal(11), new BigDecimal(8003), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> MATRIZ. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(11));
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA LOJA ZL PARA Matriz* ");
				System.out.println("Msg para polichat" + mensagem);
				
				break;
			case "8":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA IND-->> ZL. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				mensagem.append(" *Solicitação de Item:*  ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:*  "+ven);
				mensagem.append("                         ");
				mensagem.append(" *Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append(" *Produto:* " +P_prod+" "+ Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA INDUSTRIA PARA LOJA ZL* ");
				System.out.println("Msg para polichat" + mensagem);
				break;

			case "3":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA IND-->> MATRIZ. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				System.out.println("ANTES DO ITE");
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				System.out.println("INICIO DO ITE");
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append("*Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append("*Produto:* " + P_prod+" "+ Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" Transferencia DA INDUSTRIA PARA Matriz ");
				System.out.println("Msg para polichat" + mensagem);
				break;
				
			case "9":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MATRIZ-->> CD. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				System.out.println("ANTES DO ITE");
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				System.out.println("INICIO DO ITE");
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append("*Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append("*Produto:* " + P_prod+" "+Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA INDUSTRIA MATRIZ PARA O CENTRO DE DISTRUBUICAO* ");
				System.out.println("Msg para polichat" + mensagem);
				break;
				
				
			case "10":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> CD. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				System.out.println("ANTES DO ITE");
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				System.out.println("INICIO DO ITE");
				System.out.println("Vendedor"+ven+"Nr unico"+nunotaProd+"Produto"+Descrproduto+"qtd"+qtd);
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append("*Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append("*Produto:* "+ P_prod+" " + Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA INDUSTRIA ZONA LESTE PARA O CENTRO DE DISTRUBUICAO* ");
				System.out.println("Msg para polichat" + mensagem);
				break;
				
			case "11":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> MTZ. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				System.out.println("ANTES DO ITE");
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				System.out.println("INICIO DO ITE");
				System.out.println("Vendedor"+ven+"Nr unico"+nunotaProd+"Produto"+Descrproduto+"qtd"+qtd);
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append("*Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append("*Produto:* "+ P_prod+" " + Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA INDUSTRIA ZONA LESTE PARA A MATRIZ* ");
				System.out.println("Msg para polichat" + mensagem);
				break;
				
			case "12":
				this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MTZ-->> ZL. VENDEDOR(A) "+ven, "A", "F", new BigDecimal(800000000));
				System.out.println("ANTES DO ITE");
				insertnaite(this.newNota, P_prod, this.control, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9));
				System.out.println("INICIO DO ITE");
				System.out.println("Vendedor"+ven+"Nr unico"+nunotaProd+"Produto"+Descrproduto+"qtd"+qtd);
				mensagem.append("*Solicitação de Item:* ");
				mensagem.append("                         ");
				mensagem.append(" *Solicitante:* "+ven);
				mensagem.append("                         ");
				mensagem.append("*Numero da Solicitacao:* "+nunotaProd);
				mensagem.append("                         ");
				mensagem.append("*Produto:* "+ P_prod+" " + Descrproduto);
				mensagem.append("                         ");
				mensagem.append(" *Quantidade:* "+qtd);
				mensagem.append("                         ");
				mensagem.append(" *Transferencia DA INDUSTRIA MATRIZ PARA A ZL* ");
				System.out.println("Msg para polichat" + mensagem);
				break;
				
				

			default:
				break;
		}

		

	}

	public void insertnaite(BigDecimal nuNota, BigDecimal codprod, 
			String controle, BigDecimal Codlocal, String uso, 
			BigDecimal qtd, BigDecimal unitario, 
			String Volume, BigDecimal vlrvenda, BigDecimal emp) throws Exception {

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
		    itemVO.setProperty("SEQUENCIA", "1");
		    itemVO.setProperty("CODLOCALORIG", Codlocal);
		    itemVO.setProperty("SEQUENCIA", new BigDecimal(1));
		    itemVO.setProperty("USOPROD", uso);
		    itemVO.setProperty("CONTROLE", controle);
		    itemVO.setProperty("CODUSU", new BigDecimal(0));
		    itemVO.setProperty("QTDNEG", qtd);
		    itemVO.setProperty("VLRUNIT", vlrvenda);
		    itemVO.setProperty("VLRTOT", vlrvenda.multiply(qtd));
		    itemVO.setProperty("CODVOL", Volume);
		    itemVO.setProperty("ATUALESTOQUE", new BigDecimal(1));
		    itemVO.setProperty("RESERVA", "S");
		itemVO.setProperty("CODVEND", codvend);

		PrePersistEntityState itePreState = PrePersistEntityState.build(this.dwfEntityFacade, "ItemNota", itemVO);
		itensNota.add(itePreState);
		cacHelper.incluirAlterarItem(nunotaProd, auth, itensNota, true);
	}

	@SuppressWarnings("null")
	public BigDecimal criaCabecalho(BigDecimal empresa, BigDecimal tipoOperacao, BigDecimal parceiro, BigDecimal natureza, BigDecimal codTipVenda, BigDecimal codCenCus, BigDecimal modeloNota, BigDecimal codUsu, String Obs, String Statusnota, String Fob, BigDecimal codproj) throws Exception {

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
		cabecalhoVO.setProperty("PENDENTE", "S");
		cabecalhoVO.setProperty("OBSERVACAO", Obs);
		cabecalhoVO.setProperty("STATUSNOTA", Statusnota);
		cabecalhoVO.setProperty("CIF_FOB", Fob);
		cabecalhoVO.setProperty("CODPROJ", codproj);
		cabecalhoVO.setProperty("CODVEND", codvend);
		cabecalhoVO.setProperty("AD_ENTREGA", "U");
		dwfFacade.createEntity("CabecalhoNota", (EntityVO) cabecalhoVO);
		nunotaProd = cabecalhoVO.asBigDecimal("NUNOTA");

		System.out.println("Cabe" + cabecalhoVO);
		return nunotaProd;
	}
	
	 private void enviar_msg_polichat() {
			SessionHandle hnd = null;
			JdbcWrapper jdbc = null;

			try {
				hnd = JapeSession.open();
				EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

				jdbc = dwfFacade.getJdbcWrapper();
				jdbc.openSession();


				CallableStatement cstmt = jdbc.getConnection().prepareCall("{CALL SANKHYA.CA_PC_SEND_MSG_ID (?,?)}");
				cstmt.setQueryTimeout(60);
				cstmt.setInt(1, 44055345);
				cstmt.setString(2, mensagefinal);
		    

				cstmt.execute();


			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JdbcWrapper.closeSession(jdbc);
				JapeSession.close(hnd);
			}
	

}
}