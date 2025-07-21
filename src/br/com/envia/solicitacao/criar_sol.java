package br.com.envia.solicitacao;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
  
  public String P_Controle = "''";
  
  public String ControleFinal = "";
  
  public void doAction(ContextoAcao ctx) throws Exception {
    System.out.println("LOGPROD:" + ctx.getParam("PROD"));
    System.out.println("LOGQTD:" + ctx.getParam("QTD"));
    System.out.println("LOGTIPO:" + ctx.getParam("TIPO"));
    System.out.println("LOGLOCAL:" + ctx.getParam("CODLOCAL"));
    System.out.println("INICIO");
    this.P_produto = new BigDecimal(8);
    System.out.println("Inicia a Parada" + ctx.getParam("TIPO"));
    this.P_codlocal = ctx.getParam("CODLOCAL").toString();
    this.P_Quantidade = ctx.getParam("QTD").toString();
    this.P_Controle = ctx.getParam("CONTROLE") != null ? ctx.getParam("CONTROLE").toString() : "1";
    System.out.println("O CONTROLE NO PARAM é"+P_Controle);
    System.out.println("PEGANDO OS PARAMS");
    Registro line = ctx.getLinhas()[0];
    geraNota(line, ctx);
    if (this.msgErro.equals("")) {
      ctx.setMensagemRetorno("A solicitaconcluida! " + this.nunotaProd + "/////" + this.mensagem);
      this.mensagefinal = this.mensagem.toString();
      System.out.println("A MSG ENCODADA" + this.mensagefinal);
      enviar_msg_polichat();
    } else {
      ctx.setMensagemRetorno(this.msgErro);
    } 
  }
  
  private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
    String qtdString, codprodstring;
    this.codvend = (BigDecimal)line.getCampo("CODVEND");
    this.P_tipo = ctx.getParam("TIPO").toString();
    System.out.println("codvend: " + this.codvend);
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
    String str1;
    switch ((str1 = this.P_tipo).hashCode()) {
      case 49:
        if (!str1.equals("1"))
          break; 
        this.Empresa = new BigDecimal(10);
        break;
      case 50:
        if (!str1.equals("2"))
          break; 
        this.Empresa = new BigDecimal(5);
        break;
      case 51:
        if (!str1.equals("3"))
          break; 
        this.Empresa = new BigDecimal(9);
        break;
      case 52:
        if (!str1.equals("4"))
          break; 
        this.Empresa = new BigDecimal(10);
        break;
      case 53:
        if (!str1.equals("5"))
          break; 
        this.Empresa = new BigDecimal(11);
        break;
      case 54:
        if (!str1.equals("6"))
          break; 
        this.Empresa = new BigDecimal(5);
        break;
      case 55:
        if (!str1.equals("7"))
          break; 
        this.Empresa = new BigDecimal(11);
        break;
      case 56:
        if (!str1.equals("8"))
          break; 
        this.Empresa = new BigDecimal(9);
        break;
      case 57:
        if (!str1.equals("9"))
          break; 
        this.Empresa = new BigDecimal(9);
        break;
      case 1567:
        if (!str1.equals("10"))
          break; 
        this.Empresa = new BigDecimal(9);
        break;
      case 1568:
        if (!str1.equals("11"))
          break; 
        this.Empresa = new BigDecimal(9);
        break;
      case 1569:
        if (!str1.equals("12"))
          break; 
        this.Empresa = new BigDecimal(9);
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
      sql.appendSql(" SELECT ");
      sql.appendSql(" VLRVENDA ");
      sql.appendSql(" FROM ");
      sql.appendSql(" TGFEXC ");
      sql.appendSql(" WHERE ");
      sql.appendSql(" CODPROD =" + P_prod + " AND NUTAB = (SELECT MAX(NUTAB) FROM TGFEXC WHERE CODPROD =" + P_prod + ")");
      rset = sql.executeQuery();
      if (rset.next()) {
        System.out.println("VALOR " + rset.getString("VLRVENDA"));
        this.VLRVENDA = rset.getBigDecimal("VLRVENDA");
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
      if (rset2.next())
        this.ven = rset2.getString("APELIDO"); 
      System.out.println("INICIDO DO SQL DO CONTROLE");
      System.out.println("A query antes" + P_prod + "empresa" + this.Empresa + "Location" + P_Location);
      StringBuffer Controlit = new StringBuffer();
      Controlit.append(" SELECT CONTROLE ");
      Controlit.append(" FROM ");
      Controlit.append(" TGFEST ");
      Controlit.append(" WHERE ");
      Controlit.append(" CODPROD =" + P_prod + " AND CODEMP = " + this.Empresa + " AND CODLOCAL = " + P_Location);
      rset3.nativeSelect(Controlit.toString());
      System.out.println("A query depois" + P_prod + "empresa" + this.Empresa + "Location" + P_Location);
      if (rset3.next()) {
        System.out.println("PEGANDO O SQL DO CONTROLE" + rset3.getString("CONTROLE"));
        this.control = rset3.getString("CONTROLE");
      } 
      System.out.println("INICIO DO SWITCH");
      System.out.println("LOGPTIPO: " + this.P_tipo);
      StringBuffer Prod_info = new StringBuffer();
      Prod_info.append(" SELECT DESCRPROD");
      Prod_info.append(" FROM");
      Prod_info.append(" TGFPRO");
      Prod_info.append(" WHERE");
      Prod_info.append(" CODPROD = " + P_prod);
      rset4.nativeSelect(Prod_info.toString());
      if (rset4.next())
        this.Descrproduto = rset4.getString("DESCRPROD"); 
    } catch (Exception e) {
      System.out.println("Deu pau");
    } finally {
      rset.close();
      rset1.close();
      rset2.close();
      rset3.close();
      rset4.close();
    } 
    System.out.println("INICIO DO SWITCH");
    System.out.println("LOGPTIPO: " + this.P_tipo);
    String str2;
    
    
    if (P_Controle.equals("1")) {
    	ControleFinal = control;	
    }else
    {
    	ControleFinal = P_Controle;
    }
    		
    
    
    switch ((str2 = this.P_tipo).hashCode()) {
      case 49:
        if (!str2.equals("1"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(10), new BigDecimal(8000), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA CD-->> MATRIZ. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        System.out.println("Criando o cabecalho");
        System.out.println("INICIO DO ITE");
        System.out.println("Valor thisnewnota" + this.newNota);
        System.out.println("Valor thisnewnota" + this.VLRVENDA);
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(10),new BigDecimal(0));
        System.out.println("ANTES DO ITE");
        System.out.println("Valor nunotaprod" + this.nunotaProd);
        this.mensagem.append(" *Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DO Centro de Distribuicao PARA MATRIZ* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 50:
        if (!str2.equals("2"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(7972), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(0), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA Matriz->> CD. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"J",new BigDecimal(10));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(5),new BigDecimal(73000000));
        this.mensagem.append("*Solicitade Item* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA Matriz PARA Centro de Distribui");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 51:
        if (!str2.equals("3"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA IND-->> MATRIZ. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        System.out.println("ANTES DO ITE");
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(0));
        System.out.println("INICIO DO ITE");
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append("*Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append("*Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" Transferencia DA INDUSTRIA PARA Matriz ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 52:
        if (!str2.equals("4"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(10), new BigDecimal(7800), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA CD-->> ZL. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(10),new BigDecimal(0));
        this.mensagem.append(" *Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia Do CENTRO DE DISTRIBUIPARA LOJA ZL* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 53:
        if (!str2.equals("5"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(11), new BigDecimal(7972), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(0), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> CD. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"J",new BigDecimal(10));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "R", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(11),new BigDecimal(73000000));
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DO ZL PARA CENTRO DE DISTRIBUICAO* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 54:
        if (!str2.equals("6"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(5), new BigDecimal(8001), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MTZ-->> ZL. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(5),new BigDecimal(0));
        this.mensagem.append("*Solicitade Item*");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA MATRIZ PARA LOJA ZL* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 55:
        if (!str2.equals("7"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(11), new BigDecimal(8003), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> MATRIZ. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(11),new BigDecimal(0));
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA LOJA ZL PARA Matriz* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 56:
        if (!str2.equals("8"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA IND-->> ZL. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(0));
        this.mensagem.append(" *Solicitade Item:*  ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:*  " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA INDUSTRIA PARA LOJA ZL* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 57:
        if (!str2.equals("9"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9402), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(0), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MATRIZ-->> CD. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"J",new BigDecimal(9));
        System.out.println("ANTES DO ITE");
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9), new BigDecimal(72000000));
        System.out.println("INICIO DO ITE");
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append("*Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append("*Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA INDUSTRIA MATRIZ PARA O CENTRO DE DISTRUBUICAO* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 1567:
        if (!str2.equals("10"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9402), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(0), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> CD. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"J",new BigDecimal(9));
        System.out.println("ANTES DO ITE");
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(72000000));
        System.out.println("INICIO DO ITE");
        System.out.println("Vendedor" + this.ven + "Nr unico" + this.nunotaProd + "Produto" + this.Descrproduto + "qtd" + qtd);
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append("*Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append("*Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA INDUSTRIA ZONA LESTE PARA O CENTRO DE DISTRUBUICAO* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 1568:
        if (!str2.equals("11"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA ZL-->> MTZ. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        System.out.println("ANTES DO ITE");
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(0));
        System.out.println("INICIO DO ITE");
        System.out.println("Vendedor" + this.ven + "Nr unico" + this.nunotaProd + "Produto" + this.Descrproduto + "qtd" + qtd);
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append("*Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append("*Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA INDUSTRIA ZONA LESTE PARA A MATRIZ* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
      case 1569:
        if (!str2.equals("12"))
          break; 
        this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "REF PEDIDO DE VENDA MTZ-->> ZL. VENDEDOR(A) " + this.ven, "A", "F", new BigDecimal(800000000),"T",new BigDecimal(0));
        System.out.println("ANTES DO ITE");
        insertnaite(this.newNota, P_prod, ControleFinal, P_Location, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(0));
        System.out.println("INICIO DO ITE");
        System.out.println("Vendedor" + this.ven + "Nr unico" + this.nunotaProd + "Produto" + this.Descrproduto + "qtd" + qtd);
        this.mensagem.append("*Solicitade Item:* ");
        this.mensagem.append("                         ");
        this.mensagem.append(" *Solicitante:* " + this.ven);
        this.mensagem.append("                         ");
        this.mensagem.append("*Numero da Solicitacao:* " + this.nunotaProd);
        this.mensagem.append("                         ");
        this.mensagem.append("*Produto:* " + P_prod + " " + this.Descrproduto);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Quantidade:* " + qtd);
        this.mensagem.append("                         ");
        this.mensagem.append(" *Transferencia DA INDUSTRIA MATRIZ PARA A ZL* ");
        System.out.println("Msg para polichat" + this.mensagem);
        break;
    } 
  }
  
  public void insertnaite(BigDecimal nuNota, BigDecimal codprod, String controle, BigDecimal Codlocal, String uso, BigDecimal qtd, BigDecimal unitario, String Volume, BigDecimal vlrvenda, BigDecimal emp, BigDecimal localdestino) throws Exception {
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
    DynamicVO itemVO = (DynamicVO)this.dwfEntityFacade.getDefaultValueObjectInstance("ItemNota");
    itemVO.setProperty("NUNOTA", nuNota);
    itemVO.setProperty("CODEMP", emp);
    itemVO.setProperty("CODPROD", codprod);
    itemVO.setProperty("SEQUENCIA", "1");
    itemVO.setProperty("CODLOCALORIG", Codlocal);
    if (localdestino.compareTo(BigDecimal.ZERO) != 0) {
        itemVO.setProperty("CODLOCALDEST", localdestino);
    }
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
    itemVO.setProperty("CODVEND", this.codvend);
    PrePersistEntityState itePreState = PrePersistEntityState.build(this.dwfEntityFacade, "ItemNota", itemVO);
    itensNota.add(itePreState);
    cacHelper.incluirAlterarItem(this.nunotaProd, this.auth, itensNota, true);
  }
  
 

  
  
  public BigDecimal criaCabecalho(BigDecimal empresa, BigDecimal tipoOperacao, BigDecimal parceiro, BigDecimal natureza, BigDecimal codTipVenda, BigDecimal codCenCus, BigDecimal modeloNota, BigDecimal codUsu, String Obs, String Statusnota, String Fob, BigDecimal codproj, String Tipo,BigDecimal Empneg) throws Exception {
    EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
    EntityVO padraoNPVO = null;
    try {
      padraoNPVO = dwfFacade.getDefaultValueObjectInstance("CabecalhoNota");
    } catch (Exception e) {
      throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
    } 
    DynamicVO cabecalhoVO = (DynamicVO)padraoNPVO;
    System.out.println("modeloNota" + modeloNota);
    System.out.println("cabecalhoVO" + cabecalhoVO);
    System.out.println("log_parceiro" + parceiro);
    cabecalhoVO.setProperty("CODTIPOPER", tipoOperacao);
    cabecalhoVO.setProperty("TIPMOV", Tipo);
    cabecalhoVO.setProperty("CODPARC", parceiro);
    cabecalhoVO.setProperty("CODCENCUS", codCenCus);
    cabecalhoVO.setProperty("CODTIPVENDA", codTipVenda);
    cabecalhoVO.setProperty("CODEMP", empresa);
    if (Empneg.compareTo(BigDecimal.ZERO) != 0) {
    cabecalhoVO.setProperty("CODEMPNEGOC",Empneg);
    }
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
    cabecalhoVO.setProperty("CODVEND", this.codvend);
    cabecalhoVO.setProperty("AD_ENTREGA", "U");
    dwfFacade.createEntity("CabecalhoNota", (EntityVO)cabecalhoVO);
    this.nunotaProd = cabecalhoVO.asBigDecimal("NUNOTA");
    System.out.println("Cabe" + cabecalhoVO);
    return this.nunotaProd;
  }
  
  private void chamarorigem(BigDecimal nunota, BigDecimal codemp, BigDecimal codempneg) {
	    JapeSession.SessionHandle hnd = null;
	    JdbcWrapper jdbc = null;
	    try {
	      hnd = JapeSession.open();
	      EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
	      jdbc = dwfFacade.getJdbcWrapper();
	      jdbc.openSession();
	      CallableStatement cstmt = jdbc.getConnection().prepareCall("{CALL SANKHYA.SNK_ORIGEM_DESTINO (?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	      cstmt.setQueryTimeout(60);
	      cstmt.setBigDecimal(1,nunota );
	      cstmt.setBigDecimal(2,codemp  );
	      cstmt.setBigDecimal(3,codempneg );
	      cstmt.setInt(4, 54359 );
	      cstmt.setInt(5,0 );
	      cstmt.setInt(6,0 );
	      cstmt.setInt(7,0 );
	      cstmt.setString(8,"T" );
	      cstmt.setString(9, "R");
	      cstmt.execute();
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally {
	      JdbcWrapper.closeSession(jdbc);
	      JapeSession.close(hnd);
	    } 
	  }
	
  
  
  
  private void enviar_msg_polichat() {
    JapeSession.SessionHandle hnd = null;
    JdbcWrapper jdbc = null;
    try {
      hnd = JapeSession.open();
      EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
      jdbc = dwfFacade.getJdbcWrapper();
      jdbc.openSession();
      CallableStatement cstmt = jdbc.getConnection().prepareCall("{CALL SANKHYA.CA_PC_SEND_MSG_ID (?,?)}");
      cstmt.setQueryTimeout(60);
      cstmt.setInt(1, 44055345);
      cstmt.setString(2, this.mensagefinal);
      cstmt.execute();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      JdbcWrapper.closeSession(jdbc);
      JapeSession.close(hnd);
    } 
  }
}
