package br.com.gmad.listeners.AtualizacaoStatusSeparacao;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;
import java.sql.ResultSet;

public class AtualizaStatusSeparacao implements EventoProgramavelJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
  public void afterDelete(PersistenceEvent arg0) throws Exception {}
  
  public void afterInsert(PersistenceEvent arg0) throws Exception {}
  
  public void afterUpdate(PersistenceEvent ctx) throws Exception {
    DynamicVO registro = (DynamicVO)ctx.getVo();
    BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
    BigDecimal CODTIPOPER = registro.asBigDecimal("CODTIPOPER");
    String STATUSNFE = registro.asString("STATUSNFE");
    String AD_ENTREGA = registro.asString("AD_ENTREGA");
    String AD_CANCELADO = registro.asString("AD_CANCELADO");
    System.out.println("STATUSNFELOG: " + STATUSNFE);
    DefinePago(NUNOTA);
    atualizarStatusEntrega(NUNOTA);
    
    if (("A".equals(STATUSNFE) || CODTIPOPER.compareTo(new BigDecimal(33)) == 0) && "S".equals(AD_ENTREGA)) {
      System.out.println("AD_ENTREGA " + AD_ENTREGA);
      System.out.println("NUNOTALOG AFTER UPDATE ATUAIZA SEPARACAO:" + NUNOTA);
      atualizarStatus(NUNOTA);
      
    } 
    atualizarStatusEntregue(NUNOTA);
    
    if ("C".equals(AD_CANCELADO))
      cancelarSeparacao(NUNOTA); 
  }
  
  private void atualizarStatus(BigDecimal NUNOTA) {
    JapeWrapper separacaoDAO = JapeFactory.dao("CONFCAB");
    System.out.println("atualizarStatus >> NUNOTALOG:" + NUNOTA);
    try {
      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
      if (NUNOTA != null) {
        ((FluidUpdateVO)((FluidUpdateVO)((FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
          .set("STATUS", "PAGO"))
          .set("ENTREGUE", "S"))
          .set("ENTREGA", "S"))
          .update();
        System.out.println("separacaoVO: " + separacaoVO);
      } else {
        criaEatualizarStatus(NUNOTA);
      } 
    } catch (Exception e) {
      System.out.println("erro:" + e.toString());
      e.printStackTrace();
    } 
  }
  
  private void criaEatualizarStatus(BigDecimal NUNOTA) {
    System.out.println("CRIA E ATUALIZA NUNOTALOG:" + NUNOTA);
    try {
      EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
      DynamicVO separacaoVO = (DynamicVO)dwfFacade.getDefaultValueObjectInstance("CONFCAB");
      separacaoVO.setProperty("STATUS", "PAGO");
      separacaoVO.setProperty("ENTREGUE", "S");
      separacaoVO.setProperty("ENTREGA", "S");
      dwfFacade.createEntity("CONFCAB", (EntityVO)separacaoVO);
      System.out.println("separacaoVO: " + separacaoVO);
    } catch (Exception e) {
      System.out.println("erro:" + e.toString());
      e.printStackTrace();
    } 
  }
  
  private void cancelarSeparacao(BigDecimal NUNOTA) {
    JapeWrapper separacaoDAO = JapeFactory.dao("CONFCAB");
    System.out.println("NUNOTALOG:" + NUNOTA);
    try {
      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
      ((FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
        .set("ENTREGUE", "C"))
        .update();
      System.out.println("separacaoVO: " + separacaoVO);
    } catch (Exception e) {
      System.out.println("erro:" + e.toString());
      e.printStackTrace();
    } 
  }
  
  private void atualizarStatusEntregue(BigDecimal NUNOTA) throws Exception {
	    JapeWrapper separacaoDAO = JapeFactory.dao("CONFCAB");
	    System.out.println("atualizarStatusEntregue >> NUNOTALOG:" + NUNOTA);
	    StringBuilder sqlite1 = new StringBuilder();
	    String Verificador = "";

		sqlite1.append(" SELECT 1 AS REGISTRO");
		sqlite1.append(" FROM AD_CONFITE ");
		sqlite1.append(" WHERE NUNOTA = " + NUNOTA+" AND ENTREGUE = 'S'");
	    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
	    if(query.next())
	    {
	    	Verificador = query.getString("REGISTRO");
	    }
	    query.close();
	    
	    try {
	      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
	      if (NUNOTA != null && Verificador!=null ) {
	        ((FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
	          .set("ENTREGUE", "S"))
	          .update();
	        System.out.println("separacaoVO: " + separacaoVO);
	      } else {
	      } 
	    } catch (Exception e) {
	      System.out.println("erro:" + e.toString());
	      e.printStackTrace();
	    } 
	  }
  
  private void atualizarStatusEntrega(BigDecimal NUNOTA) throws Exception {
	    JapeWrapper separacaoDAO = JapeFactory.dao("CONFCAB");
	    System.out.println("atualizarStatusEntrega >> NUNOTALOG:" + NUNOTA);
	    StringBuilder sqlite3 = new StringBuilder();
	    String Verificador = "";

		sqlite3.append(" SELECT 1 AS REGISTROA");
		sqlite3.append(" FROM TGFCAB ");
		sqlite3.append(" WHERE NUNOTA = " + NUNOTA+" AND AD_ENTREGA = 'S'");
	    ResultSet queryVr = nativeSql.executeQuery(sqlite3.toString());
	    if(queryVr.next())
	    {
	    	Verificador = queryVr.getString("REGISTROA");
	    }
	    queryVr.close();
	    
	    try {
	      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
	      if (NUNOTA != null && Verificador!=null ) {
	        ((FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
	          .set("ENTREGA", "S"))
	          .update();
	        System.out.println("separacaoVO: " + separacaoVO);
	      } else {
	      } 
	    } catch (Exception e) {
	      System.out.println("erro:" + e.toString());
	      e.printStackTrace();
	    } 
	  }
  
  private void DefinePago(BigDecimal NUNOTA) throws Exception {
	    JapeWrapper separacaoDAO = JapeFactory.dao("CONFCAB");
	    System.out.println("Atualização do Pagamento >> NUNOTALOG:" + NUNOTA);
	    StringBuilder sqlite2 = new StringBuilder();
	    String StatusN = "";
	    String StatusC = "";
	    String NF = "";
	    BigDecimal NumNota = null;

		sqlite2.append(" SELECT MAX(CAB.STATUSNFE) AS STATUS,MAX(TPEMISNFE) AS STTSERVICO,NUMNOTA AS NF");
		sqlite2.append(" FROM TGFVAR VA ");
		sqlite2.append(" INNER JOIN TGFCAB CAB ON CAB.NUNOTA=VA.NUNOTA");
		sqlite2.append(" WHERE VA.NUNOTA = " + NUNOTA);
	    ResultSet queryVar = nativeSql.executeQuery(sqlite2.toString());
	    if(queryVar.next())
	    {
	    	StatusN = queryVar.getString("STATUS");
	    	StatusC = queryVar.getString("STTSERVICO");
	    	NF = queryVar.getString("NF");
	    	NumNota = new BigDecimal(NF);
	    }
	    queryVar.close();
	      
	    try {
	      DynamicVO separacaoVO = separacaoDAO.findOne("NUNOTA = ?", new Object[] { NUNOTA });
	      if (NUNOTA != null && "A".equals(StatusN) || "9".equals(StatusC)) {
	        ((FluidUpdateVO)(FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
	          .set("STATUS", "PAGO"))
	          .set("NUMNOTA", NumNota)
	          .update();
	        System.out.println("separacaoVO: " + separacaoVO);
	      } else {
	    	  ((FluidUpdateVO)(FluidUpdateVO)separacaoDAO.prepareToUpdate(separacaoVO)
	    	          .set("STATUS", "Não Pago"))
	    	          .set("NUMNOTA", NumNota)
	    	          .update();
	      } 
	    } catch (Exception e) {
	      System.out.println("erro:" + e.toString());
	      e.printStackTrace();
	    } 
	  }
  
  
  
  
  
  
  public void beforeCommit(TransactionContext arg0) throws Exception {}
  
  public void beforeDelete(PersistenceEvent arg0) throws Exception {}
  
  public void beforeInsert(PersistenceEvent arg0) throws Exception {}
  
  public void beforeUpdate(PersistenceEvent arg0) throws Exception {}
}
