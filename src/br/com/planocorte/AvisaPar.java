package br.com.planocorte;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class AvisaPar implements EventoProgramavelJava {
	
	  public void afterInsert(PersistenceEvent arg0) throws Exception {

		    
		  }

	private void VerificaUsu(PersistenceEvent arg0) throws Exception {
		DynamicVO PlanoVO = (DynamicVO)arg0.getVo();
		
		String NLS = PlanoVO.asString("NLS");
	    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	    StringBuilder sqlprint = new StringBuilder();
	    StringBuilder sqlprint2 = new StringBuilder();
	    System.out.println("EVENTO ON");
	    NativeSql sqlverifica = new NativeSql(jdbc);
	    BigDecimal nUnota = new BigDecimal(0);
	   
	  
	    sqlprint2.append(" SELECT COUNT(*) AS A");
	    sqlprint2.append(" FROM TGFCAB ");
	    sqlprint2.append(" WHERE CODTIPOPER IN (1015,158,1014,1011,8100,8109,8182,2000,182) AND NUNOTA = "+nunota);
	    ResultSet qer = sqlverifica.executeQuery(sqlprint2.toString());
	    if(qer.next())
	    {
	    	nUnota = qer.getBigDecimal("A");
		    System.out.println("O VALOR DA NOTA DE COMPRA NO IF  é"+nUnota);
	    }
	    qer.close();
	    
	 
	    
	    }
	    catch (Exception e) {
	    	exibirErro();
	    	System.out.println("TRAVOU O BLOCK");
  	    }
	    
	    System.out.println("EVENTO WORKS AGORA VAI");
	    
	    
	}
	
	public  void buscaNota ()
	{
		
	}
	
	
	
	
	public void exibirErro() throws Exception  {
		throw new PersistenceException("A confirmação da Nota para seu usuario nao está permitida");
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
		
	}

	@Override
	public void beforeDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent arg0) throws Exception {
		VerificaUsu(arg0);
	    System.out.println("TA FUNCIONANDO O BLOQUEIO");
		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {

		
	}
	
	public void V_Parc (String LS)
	{
		String[] partes = LS.split(",");

        // Lista para armazenar os números válidos
        List<Integer> numerosValidos = new ArrayList<>();

        // Processar cada parte
        for (String parte : partes) {
            try {
                // Remover espaços em branco antes de converter
                int numero = Integer.parseInt(parte.trim());
                numerosValidos.add(numero);
            } catch (NumberFormatException e) {
                // Ignora partes que não são números
                System.out.println("Valor inválido ignorado: " + parte);
            }
        }
	}
	
}
