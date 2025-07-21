package src.gmad;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;



public class sendmsgafterbuy implements EventoProgramavelJava {
	
	  public void afterInsert(PersistenceEvent arg0) throws Exception {
		    verificarimp(arg0);
		    System.out.println("TA FUNCIONANDO O LOG");
		  }

	private void verificarimp(PersistenceEvent arg0) throws Exception {
		DynamicVO iteNota = (DynamicVO)arg0.getVo();
	    BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)iteNota.getProperty("NUNOTA"));
	    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	    NativeSql nativeSql = new NativeSql(jdbc);
	    StringBuilder sqlprint = new StringBuilder();
	    StringBuilder sqlprint2 = new StringBuilder();
	    System.out.println("EVENTO ON");
	    NativeSql sqlverifica = new NativeSql(jdbc);
	    BigDecimal nUnota = new BigDecimal(0);
	    BigDecimal top = new BigDecimal(0);
	    BigDecimal numeroxml = new BigDecimal(0);
	  
	    
	    sqlprint2.append(" SELECT NUNOTA");
	    sqlprint2.append(" FROM TGFCAB ");
	    sqlprint2.append(" WHERE CODTIPOPER IN (1015,158,1014,1011,8100,8109,8182,2000) AND NUNOTA = "+nunota);
	    ResultSet qer = sqlverifica.executeQuery(sqlprint2.toString());
	    if(qer.next())
	    {
	    	nUnota = qer.getBigDecimal("NUNOTA");
		    System.out.println("O VALOR DA VERIFICAÇÂO DA CAB é"+nUnota);
	    }

	    
	    qer.close();
	    
	    sqlprint.append(" SELECT COUNT(*) AS CONTAGEM");
	    sqlprint.append(" FROM TGFIXN ");
	    sqlprint.append(" WHERE NUNOTA = "+nUnota);
	    ResultSet queryPrint = nativeSql.executeQuery(sqlprint.toString());
	    System.out.println("O NUNOTA PASSOU NO PRIMEIRO IF: " + sqlprint.toString());
	    
	    if(queryPrint.next())
	    {
	    numeroxml = queryPrint.getBigDecimal("CONTAGEM");
	    System.out.print("numeroimp NESSE:"+numeroxml);
	    }
	    queryPrint.close();
	    
	    if (numeroxml.compareTo(BigDecimal.ONE) == 0 || nUnota.compareTo(BigDecimal.ONE)>0 ) {
	    	System.out.println("Caiu no If do Aviso: ");
	    	JapeWrapper avisoSistema = JapeFactory.dao(DynamicEntityNames.AVISO_SISTEMA);
	    	avisoSistema.create()
	    	.set("NUAVISO", null)
	    	.set("CODGRUPO", BigDecimal.valueOf(23)) 
	    	.set("DESCRICAO", "A nota de Compra Lançada!!!!  " + nunota)
	    	.set("DHCRIACAO", TimeUtils.getNow())
	    	.set("IDENTIFICADOR", "PERSONALIZADO")
	    	.set("IMPORTANCIA", BigDecimal.valueOf(2))				
	    	.set("TIPO", "P")
	    	.set("TITULO", "Nota de Compra Lançamento")
	    	.save();	
	    }
	    
	    
	    
	    System.out.println("EVENTO WORKS");
	    
	    
	    
	}
	

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeCommit(TransactionContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}








}
