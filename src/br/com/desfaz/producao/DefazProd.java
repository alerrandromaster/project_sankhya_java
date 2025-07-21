package br.com.desfaz.producao;

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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class DefazProd implements AcaoRotinaJava {
  private String msgErro = "";
  
  
  public BigDecimal nunotaProd = BigDecimal.ZERO;
  
  AuthenticationInfo auth = AuthenticationInfo.getCurrent();
  
  EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
  
  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
  
  NativeSql nativeSql = new NativeSql(this.jdbc);
  
  StringBuilder stringBuild = new StringBuilder();
  
  StringBuffer mensagem = new StringBuffer();
  
  public String mensagefinal = "";
  
  public BigDecimal PA_Produto = new BigDecimal(0);
  
  public BigDecimal codvend = new BigDecimal(0);
  
  public String P_Controle = "''";
  
  public String ControleFinal = "";
  public String Erro = "";
  
  public void doAction(ContextoAcao ctx) throws Exception {
	  
    Registro line = ctx.getLinhas()[0];
    geraNota(line, ctx);

    	ctx.setMensagemRetorno("Produção Desfeita!!");

    
    
  }
  
  private void geraNota(Registro line, ContextoAcao ctx) throws Exception {
    BigDecimal AD_ORC_VENDA = (BigDecimal) line.getCampo("AD_ORC_VEND_MDF");
    BigDecimal NUNOTA = (BigDecimal)line.getCampo("NUNOTA");
    BigDecimal CODVEND = (BigDecimal)line.getCampo("CODVEND");
    
    NativeSql sql = new NativeSql(this.jdbc);
    QueryExecutor rset1 = ctx.getQuery();
    QueryExecutor rset2 = ctx.getQuery();
    ResultSet rset = null;
    try {
        StringBuffer sqlIt3 = new StringBuffer();
        sqlIt3.append("WITH BASE AS (");
        sqlIt3.append(" SELECT ");
        sqlIt3.append(" L.CODPRODMP, ");
        sqlIt3.append(" ITE.NUTAB, ");
        sqlIt3.append(" ITE.SEQUENCIA, ");
        sqlIt3.append(" ITE.CODLOCALORIG, ");
        sqlIt3.append(" ITE.QTDNEG, ");
        sqlIt3.append(" ITE.VLRUNIT, ");
        sqlIt3.append(" ITE.VLRTOT, ");
        sqlIt3.append(" ITE.NUNOTA, ");
        sqlIt3.append(" ROW_NUMBER() OVER (ORDER BY ITE.SEQUENCIA) AS RN");
        sqlIt3.append(" FROM TGFITE ITE ");
        sqlIt3.append(" INNER JOIN TPRLMP L ON L.CODPRODPA = ITE.CODPROD ");
        sqlIt3.append(" WHERE ITE.AD_PRODUZIR = 'S' AND ITE.NUNOTA = ").append(NUNOTA);
        sqlIt3.append("), ");
        sqlIt3.append("MAXSEQ AS ( ");
        sqlIt3.append(" SELECT MAX(SEQUENCIA) AS MAX_SEQ ");
        sqlIt3.append(" FROM TGFITE ");
        sqlIt3.append(" WHERE NUNOTA = ").append(NUNOTA);
        sqlIt3.append(") ");
        sqlIt3.append("SELECT ");
        sqlIt3.append(" B.CODPRODMP AS CODPROD, ");
        sqlIt3.append(" B.NUTAB, ");
        sqlIt3.append(" B.SEQUENCIA, ");
        sqlIt3.append(" (M.MAX_SEQ + B.RN) AS NOVA_SEQUENCIA, ");
        sqlIt3.append(" B.CODLOCALORIG, ");
        sqlIt3.append(" B.QTDNEG, ");
        sqlIt3.append(" B.VLRUNIT, ");
        sqlIt3.append(" B.VLRTOT ");
        sqlIt3.append("FROM BASE B ");
        sqlIt3.append("CROSS JOIN MAXSEQ M ");
        System.out.println("O SELECT DO REFAZER é "+sqlIt3.toString());
        rset1.nativeSelect(sqlIt3.toString());
        while (rset1.next()) {
        	BigDecimal CODPROD = rset1.getBigDecimal("CODPROD");
        	BigDecimal NUTAB = rset1.getBigDecimal("NUTAB");
        	BigDecimal SEQUENCIA = rset1.getBigDecimal("NOVA_SEQUENCIA");
        	BigDecimal CODLOCALORIG = rset1.getBigDecimal("CODLOCALORIG");
        	BigDecimal QTDNEG = rset1.getBigDecimal("QTDNEG");
        	BigDecimal VLRUNIT = rset1.getBigDecimal("VLRUNIT");
        	BigDecimal VLRTOT = rset1.getBigDecimal("VLRTOT");
        	String CODVOL = ProcurarUn(CODPROD,ctx);
        	System.out.println("CODOVOL é "+CODVOL);
        	insertnaite(NUNOTA,CODPROD,NUTAB,SEQUENCIA,CODLOCALORIG,QTDNEG,VLRUNIT,VLRTOT,CODVEND,CODVOL);
        } 


      StringBuffer sqlIt = new StringBuffer();
      sqlIt.append(" SELECT ITE.CODPROD ");
      sqlIt.append(" FROM TGFITE ITE  ");
      sqlIt.append(" INNER JOIN TPRLMP L ON L.CODPRODPA = ITE.CODPROD ");
      sqlIt.append(" INNER JOIN AD_COMPOSICAO COM ON COM.NUNOTA = ITE.NUNOTA ");
      sqlIt.append(" WHERE ");
      sqlIt.append(" ITE.NUNOTA = " + NUNOTA + " and COM.TIPO = 'MP' and ITE.AD_PRODUZIR = 'S'");
      rset2.nativeSelect(sqlIt.toString());
      while (rset2.next())
      {
    	BigDecimal Codprod = rset2.getBigDecimal("CODPROD");
    	deleteItem(Codprod,NUNOTA);
      }
        
      deleteNota(NUNOTA,AD_ORC_VENDA);
      
    } catch (Exception e) {
      System.out.println("Deu pau");
		System.out.println("O erro é de Desfaz Produção" + e);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		} finally {
      rset1.close();
      rset2.close();
    } }

    
  public void deleteItem (BigDecimal Codprod,BigDecimal Nunota)
  {
	  JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
        JapeSession.SessionHandle hnd = JapeSession.open();
        NativeSql sql2 = new NativeSql(jdbc);
        try {
        	String sqlDelete = "DELETE FROM TGFITE WHERE AD_PRODUZIR = 'S' AND CODPROD = " + Codprod + " AND NUNOTA = " + Nunota;
        	System.out.println("Executando SQL: " + sqlDelete);
        	 sql2.executeUpdate(sqlDelete);

  } catch (Exception a )
  {
	  a.printStackTrace();
	  System.out.println("Deu erro no delete do item "+Codprod);
  }
  finally {
	  JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
  }
  } 
  
  public void deleteNota (BigDecimal Nunota,BigDecimal NunotaOrc)
  {
	  JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
        JapeSession.SessionHandle hnd = JapeSession.open();
        NativeSql sql2 = new NativeSql(jdbc);
        try {
        sql2.setNamedParameter("P_NOTA", NunotaOrc);
        sql2.executeUpdate("DELETE FROM TGFCAB WHERE NUNOTA = :P_NOTA");
        sql2.executeUpdate("UPDATE TGFCAB SET AD_ORC_VEND_MDF = NULL WHERE NUNOTA="+Nunota);
        sql2.executeUpdate("DELETE FROM AD_COMPOSICAO WHERE NUNOTA="+Nunota);
        
        
  } catch (Exception a )
  {
	  a.printStackTrace();
	  System.out.println("Deu erro no Delete da Nota"+Nunota);
  }
  finally {
	  JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
  }
  } 
  
  
  public String ProcurarUn (BigDecimal Codprod,ContextoAcao ctx)
  {
	  JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
        JapeSession.SessionHandle hnd = JapeSession.open();
        NativeSql sql2 = new NativeSql(jdbc);
        QueryExecutor rset2 = ctx.getQuery();
        String CODVOL = "";
        try {
        	StringBuffer sqlIt = new StringBuffer();
            sqlIt.append(" SELECT CODVOL ");
            sqlIt.append(" FROM TGFPRO ");
            sqlIt.append(" WHERE ");
            sqlIt.append(" CODPROD = " + Codprod );
            rset2.nativeSelect(sqlIt.toString());
            if (rset2.next())
            {
            	CODVOL = rset2.getString("CODVOL");
            }
          
  } catch (Exception a )
  {
	  a.printStackTrace();
	  System.out.println("Não Achou o Codvol do Produto");
  }
  finally {
	  JdbcWrapper.closeSession(jdbc);
		JapeSession.close(hnd);
		rset2.close();
  }
		return CODVOL;
  } 
  
  
  
  
 
  
  public void insertnaite(BigDecimal nuNota, BigDecimal codprod, BigDecimal Nutab, 
		  BigDecimal Sequencia, BigDecimal CodLocalOrig, 
		  BigDecimal QTDNEG, BigDecimal VLRUNIT, 
		  BigDecimal VLRTOT, BigDecimal CODVEND,String CODVOL) throws Exception {
    System.out.println("INICIO DO METODO DA ITE");
    System.out.println("nunota" + nuNota);
    System.out.println("codprod" + codprod);
    System.out.println("nutab" + Nutab);
    System.out.println("qtd" + QTDNEG);
    System.out.println("unitario" + VLRUNIT);
    System.out.println("Valor Total" + VLRTOT);
    System.out.println("VEN" + CODVEND);
    CACHelper cacHelper = new CACHelper();
    JapeSessionContext.putProperty("br.com.sankhya.com.CentralCompraVenda", Boolean.TRUE);
    Collection<PrePersistEntityState> itensNota = new ArrayList<>();
    DynamicVO itemVO = (DynamicVO)this.dwfEntityFacade.getDefaultValueObjectInstance("ItemNota");
    itemVO.setProperty("NUTAB", Nutab);
    itemVO.setProperty("NUNOTA", nuNota);
    itemVO.setProperty("CODEMP", new BigDecimal(9));
    itemVO.setProperty("CODPROD", codprod);
    itemVO.setProperty("SEQUENCIA", Sequencia);
    itemVO.setProperty("CODLOCALORIG", CodLocalOrig);
    itemVO.setProperty("USOPROD", "M");
    itemVO.setProperty("CODUSU", new BigDecimal(0));
    itemVO.setProperty("QTDNEG", QTDNEG);
    itemVO.setProperty("VLRUNIT", VLRUNIT);
    itemVO.setProperty("VLRTOT", VLRTOT);
    itemVO.setProperty("CODVOL", CODVOL);
    itemVO.setProperty("PENDENTE", "S");
    itemVO.setProperty("ATUALESTOQUE", new BigDecimal(0));
    itemVO.setProperty("RESERVA", "S");
    itemVO.setProperty("AD_PRODUZIR", "S");
    itemVO.setProperty("FATURAR", "S");
    itemVO.setProperty("GERAPRODUCAO", "S");
    itemVO.setProperty("CODVEND", CODVEND);
    PrePersistEntityState itePreState = PrePersistEntityState.build(this.dwfEntityFacade, "ItemNota", itemVO);
    itensNota.add(itePreState);
    cacHelper.incluirAlterarItem(nuNota, this.auth, itensNota, true);
  }
  
 


}
