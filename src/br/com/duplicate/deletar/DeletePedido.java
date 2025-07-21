package br.com.duplicate.deletar;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.core.JapeSession;
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
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgeprod.model.helper.OperacaoProducaoHelper;
import br.com.sankhya.mgeprod.model.services.OperacaoProducaoSPBean;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.AtributosRegras;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
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


public class DeletePedido implements EventoProgramavelJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
    StringBuilder sqlite1 = new StringBuilder();
    StringBuilder sqlite2 = new StringBuilder();
	public BigDecimal nunota = new BigDecimal(0);
	public BigDecimal id = new BigDecimal(0);
	public BigDecimal orc = new BigDecimal(0);
	public BigDecimal notaantiga ;
	
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		DynamicVO cabNota = (DynamicVO)arg0.getVo();
	    BigDecimal nunota = cabNota.asBigDecimal("NUNOTAORIG");
	    BigDecimal topi = cabNota.asBigDecimal("CODTIPOPER");
	    System.out.println("top evento:"+topi);
	    System.out.println("nota caiu no evento da dup"+nunota);
	    
	    /*if (hnd != null && topi.equals(new BigDecimal(7041))){
	   
	    /*	if(JapeSession.getProperty(AtributosRegras.NUNOTA_SENDO_DUPLICADA) != null ){
	    	//	notaantiga= (BigDecimal) JapeSession.getProperty(AtributosRegras.NUNOTA_SENDO_DUPLICADA);
	    		System.out.println("levou desvantagi"+"a nota levou"+notaantiga);
	        }
		}*/
	    
	   if (topi.equals(new BigDecimal(7041)))
	   {
		 System.out.println("Entrou no if de Delete");
	    deleteprod(nunota);
	   }
	    
		
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
	public void beforeInsert(PersistenceEvent arg0) throws Exception {

	}
	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	private void exibirErro(String mensagem) throws Exception  {
		throw new PersistenceException("--"+mensagem+"----");
	}
	
	 private void deleteprod(BigDecimal NUNOTA) throws Exception {
		 	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		    NativeSql nativeSql = new NativeSql(jdbc);
		 try {
		    System.out.println("O delete iniciou!!");
		    System.out.println("nunota a ser excluido"+NUNOTA);
		    nativeSql.executeUpdate(" DELETE TGFCAB WHERE NUNOTA = " + NUNOTA);
		    System.out.println("O Update Finalizou");

	 }catch (Exception e) {
		 System.out.println("Erro no Update:" + e.getMessage());
	      e.toString();
	      e.printStackTrace();
	 }
		 
	 }
	

	 /*private void procurarNotaAntiga(BigDecimal NUNOTA) throws Exception {
		    StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
			sqlite1.append(" SELECT STATUSNOTA");
			sqlite1.append(" FROM TGFCAB ");
			sqlite1.append(" WHERE NUNOTA = " + NUNOTA);
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		  
		
		    if(query.next())
				    {
		    	Sinalizar = query.getString("STATUSNOTA");
		    	
				    }
		    query.close();

	 }*/
	
	
}
