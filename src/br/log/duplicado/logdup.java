package br.log.duplicado;
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

public class logdup implements EventoProgramavelJava  {
	
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);
	public BigDecimal notaantiga ;
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void afterInsert(PersistenceEvent arg0) throws Exception {
		JapeSession	hnd = JapeSession.getCurrentSession();
		DynamicVO cabNota = (DynamicVO)arg0.getVo();
	    BigDecimal nunota = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("NUNOTA"));
	    BigDecimal topi = BigDecimalUtil.getValueOrZero((BigDecimal)cabNota.getProperty("CODTIPOPER"));
	    System.out.println("a top é:"+topi);
	    System.out.println("LOG DE DUP NUNOTA"+nunota);
	    
	    
	    if (hnd != null){
	   
	    	if(JapeSession.getProperty(AtributosRegras.NUNOTA_SENDO_DUPLICADA) != null && topi.equals(new BigDecimal(9042) ))
	    	{
	    		deleteprod(nunota);
	    		System.out.println("levou desvantagi");
	    //		notaantiga= (BigDecimal) JapeSession.getProperty(AtributosRegras.NUNOTA_SENDO_DUPLICADA);
	    		
	    //  JapeWrapper duplicacaoDAO = JapeFactory.dao("AD_LOGDD");
	   //DynamicVO dup = (DynamicVO) ((FluidCreateVO)(FluidCreateVO)(FluidCreateVO)(FluidCreateVO)duplicacaoDAO.
	    //	    		create().set("NUNOTAORIG", notaantiga).set("NUNOTADEST", nunota)).set("CODTIPOPER", topi)
			//   .set("DTDUP", TimeUtils.getNow())
			  // .save();
	    		
	    		
	    		
	    		
	    		
	    		
	        	
	        //	}
		
	}
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
		// TODO Auto-generated method stub
		
	}
	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	 private void deleteprod(BigDecimal NUNOTA) throws Exception {
		    StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    nativeSql.executeUpdate(" DELETE TGFITE WHERE NUNOTA = " + NUNOTA);

	 }
	

}
