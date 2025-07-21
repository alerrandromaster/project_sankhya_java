package br.com.listner;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import java.math.BigDecimal;
import java.sql.SQLException;

public class atualizaPrecoPA implements EventoProgramavelJava {
	  private static final String URL = "jdbc:sqlserver://10.1.100.15:1433;databaseName=sankhya_prod";
	  
	  private static final String USER = "sankhya";
	  
	  private static final String PASSWORD = "tecsis";
	  
	  public void afterDelete(PersistenceEvent arg0) throws Exception {}
	  
	  public void afterInsert(PersistenceEvent arg0) throws Exception {
		   DynamicVO registro = (DynamicVO)arg0.getVo();
		    BigDecimal codprod = registro.asBigDecimal("CODPROD");
		    BigDecimal vlrvenda = registro.asBigDecimal("VLRVENDA");
		    System.out.println("attluaizacao: " + codprod);
		    System.out.println("AtualizaPrecoPA: " + vlrvenda);
		    atualizaPrecoProduto(codprod.toString(), vlrvenda.toString());
	  }
	  
	  public void afterUpdate(PersistenceEvent arg0) throws Exception {
	    DynamicVO registro = (DynamicVO)arg0.getVo();
	    BigDecimal codprod = registro.asBigDecimal("CODPROD");
	    BigDecimal vlrvenda = registro.asBigDecimal("VLRVENDA");
	    System.out.println("AtualizaPrecoPA: " + codprod);
	    System.out.println("AtualizaPrecoPA: " + vlrvenda);
	    atualizaPrecoProduto(codprod.toString(), vlrvenda.toString());
	  }
	  
	  public void beforeCommit(TransactionContext arg0) throws Exception {}
	  
	  public void beforeDelete(PersistenceEvent arg0) throws Exception {}
	  
	  public void beforeInsert(PersistenceEvent arg0) throws Exception {
		  DynamicVO registro = (DynamicVO)arg0.getVo();
		    BigDecimal codprod = registro.asBigDecimal("CODPROD");
		    BigDecimal vlrvenda = registro.asBigDecimal("VLRVENDA");
		    System.out.println("attluaizacao: " + codprod);
		    System.out.println("attluaizacaovlrvenda: " + vlrvenda);
	  }
	  
	  public void beforeUpdate(PersistenceEvent arg0) throws Exception {}
	  
	  public static void atualizaPrecoProduto(String codigoOriginal, String vlrVenda) {
	    String codigoCom9 = "9" + codigoOriginal;
	    String selectProdutoCom9 = "SELECT * FROM TGFEXC WHERE CODPROD = ?";
	    String updatePrecoProduto = "UPDATE TGFEXC SET VLRVENDA = ? WHERE CODPROD = ?";
	    try {
	      Exception exception2, exception1 = null;
	    } catch (NumberFormatException e) {
	      System.out.println("Erro ao converter o valor de venda: " + vlrVenda);
	      e.printStackTrace();
	    } 
	  }
	}