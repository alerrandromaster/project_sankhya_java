package br.com.envia.transf;

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

public class Transf_rot implements AcaoRotinaJava {
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

    System.out.println("INICIO");
    Registro line = ctx.getLinhas()[0];
    this.P_Quantidade = ctx.getParam("QTD").toString();
    geraNota(line, ctx);
   
    if (this.msgErro.equals("")) {
      ctx.setMensagemRetorno("A solicitaconcluida! " + this.nunotaProd + "/////" + this.mensagem);
      this.mensagefinal = this.mensagem.toString();
    } else {
      ctx.setMensagemRetorno(this.msgErro);
    } 
  }
  
  private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
    String qtdString, codprodstring;
    String controle = "";
    BigDecimal CodLocal,CodProd,codemp,qtd = new BigDecimal(0);
    
    CodProd = (BigDecimal)line.getCampo("CODPROD");
    CodLocal = (BigDecimal)line.getCampo("CODLOCAL");
    codemp = (BigDecimal)line.getCampo("CODEMP");
    controle = (String)line.getCampo("CONTROLE");
    qtd = new BigDecimal(P_Quantidade);

    NativeSql sql = new NativeSql(this.jdbc);
    QueryExecutor rset4 = ctx.getQuery();
    QueryExecutor rset1 = ctx.getQuery();
    QueryExecutor rset3 = ctx.getQuery();
    QueryExecutor rset2 = ctx.getQuery();
    ResultSet rset = null;
    try {

      sql.appendSql(" SELECT ");
      sql.appendSql(" VLRVENDA ");
      sql.appendSql(" FROM ");
      sql.appendSql(" TGFEXC ");
      sql.appendSql(" WHERE ");
      sql.appendSql(" CODPROD =" + CodProd + " AND NUTAB = (SELECT MAX(NUTAB) FROM TGFEXC WHERE CODPROD =" + CodProd + ")");
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
      sqlIt3.append(" CODPROD =" + CodProd);
      System.out.println("DEPOIS DO CODPROD CODVOL");
      rset1.nativeSelect(sqlIt3.toString());
      if (rset1.next()) {
        System.out.println("CODVOL PEGANDO" + rset1.getString("CODVOL"));
        this.volume = rset1.getString("CODVOL");
      } 
      
      /*
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
        
        */
      StringBuffer Controlit = new StringBuffer();
     
      Controlit.append(" SELECT CONTROLE ");
      Controlit.append(" FROM ");
      Controlit.append(" TGFEST ");
      Controlit.append(" WHERE ");
      Controlit.append(" CODPROD =" + CodProd + " AND CODEMP = " + codemp + " AND CODLOCAL = " + CodLocal);
      rset3.nativeSelect(Controlit.toString());
      System.out.println("A query depois" + CodProd + "empresa" + codemp + "Location" + CodLocal);
      if (rset3.next()) {
        this.control = rset3.getString("CONTROLE");
      } 

      StringBuffer Prod_info = new StringBuffer();
      Prod_info.append(" SELECT DESCRPROD");
      Prod_info.append(" FROM");
      Prod_info.append(" TGFPRO");
      Prod_info.append(" WHERE");
      Prod_info.append(" CODPROD = " + CodProd);
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
    
    		
    
    switch (codemp.toPlainString()) {
	case "10":
		 this.newNota = criaCabecalho(new BigDecimal(10), new BigDecimal(8000), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "TRANSFERENCIA CD ->> MTZ VIA ROTINA REL ROTATIVO" + "0", "A", "F", new BigDecimal(800000000),"J",new BigDecimal(0));
	        insertnaite(this.newNota, CodProd, controle, CodLocal, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(10),new BigDecimal(0));
	        break;
	case "9":
		  this.newNota = criaCabecalho(new BigDecimal(9), new BigDecimal(9401), new BigDecimal(158690), new BigDecimal(2060000), new BigDecimal(130), new BigDecimal(10316), BigDecimal.ZERO, BigDecimal.ZERO, "TRANSFERENCIA IND VIA ROTINA REL ROTATIVO " + "0", "A", "F", new BigDecimal(800000000),"J",new BigDecimal(0));
	        System.out.println("ANTES DO ITE");
	        insertnaite(this.newNota, CodProd, controle, CodLocal, "V", qtd, this.VLRVENDA, this.volume, this.VLRVENDA, new BigDecimal(9),new BigDecimal(0));
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
  

 
}
