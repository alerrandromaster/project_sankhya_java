package src.gmad;


import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.mgeprod.model.helper.OperacaoProducaoHelper;
import br.com.sankhya.mgeprod.model.services.OperacaoProducaoSPBean;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import com.sankhya.util.TimeUtils;
import com.sankhya.util.XMLUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdom.Content;
import org.jdom.Element;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.math.BigDecimal;

public class block_mdf implements EventoProgramavelJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
    StringBuilder sqlite1 = new StringBuilder();
    StringBuilder sqlite2 = new StringBuilder();
	
	
	public BigDecimal nunota = new BigDecimal(0);

	
	public void beforeInsert(PersistenceEvent persistenceEvent) throws Exception {
		this.bloquearInserirProduto(persistenceEvent);
	}
	
	public void afterInsert(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterUpdate(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void beforeDelete(PersistenceEvent persistenceEvent) throws Exception { }
	
	public void afterDelete(PersistenceEvent persistenceEvent) throws Exception { }

	public void beforeCommit(TransactionContext persistenceEvent) throws Exception { }
	
	private void bloquearInserirProduto(PersistenceEvent arg0) throws Exception {
		
		 DynamicVO cabNota = (DynamicVO)arg0.getVo();
		    BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("NUNOTA"));
		

		sqlite1.append(" SELECT CODLOCALORIG");
		sqlite1.append(" FROM TGFITE ");
		sqlite1.append(" WHERE NUNOTA = " + nunota+" AND SEQUENCIA = 1");
	    ResultSet queryVar = nativeSql.executeQuery(sqlite1.toString());
	   
	    
	    sqlite2.append ("SELECT CODLOCALORIG");
	    sqlite1.append(" FROM TGFITE ");
	    sqlite1.append(" WHERE NUNOTA = " + nunota);
	    ResultSet queryVar2 = nativeSql.executeQuery(sqlite2.toString());
	    
	    if (queryVar.next() && queryVar2.next() ) {
	      BigDecimal localorig1 = BigDecimalUtil.getValueOrZero(queryVar.getBigDecimal("CODLOCALORIG"));
	      BigDecimal localorig2 = BigDecimalUtil.getValueOrZero(queryVar2.getBigDecimal("CODLOCALORIG"));
	      
	      
	      
	      if (localorig1 == new BigDecimal(7000000) || localorig2 ==  new BigDecimal(88010000) || localorig2 ==  new BigDecimal(88030000)
	      || localorig2 ==  new BigDecimal(88040000) || localorig2 ==  new BigDecimal(88050000) || localorig2 ==  new BigDecimal(88050000)) {
	    	    System.out.println("O valor está na matriz.");
	    	} else {
	    	    System.out.println("O valor não está na lista.");
	    	    exibirErro("Só possivel retirar o mdf de uma empresa");
	    	}
	      
		
	    }	
	}

	

	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("<p align=\"center\"><img src=\"https://dallabernardina.vteximg.com.br/arquivos/logo_header.png\"></img></p><br/><br/><br/><br/><br/><br/>\n\n\n\n<font size=\"12\" color=\"#BF2C2C\"><b> " + mensagem + "</b></font>\n\n\n");
	}

}