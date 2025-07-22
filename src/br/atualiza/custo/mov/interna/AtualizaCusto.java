package br.atualiza.custo.mov.interna;



import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class AtualizaCusto implements EventoProgramavelJava {

	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	
	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		DynamicVO registro = (DynamicVO)arg0.getVo();
	}

	@Override
	public void afterInsert(PersistenceEvent ctx) throws Exception {
		DynamicVO registro = (DynamicVO)ctx.getVo();
		BigDecimal CODPROD = registro.asBigDecimal("CODPROD");
		BigDecimal NUNOTA = registro.asBigDecimal("NUNOTA");
		BigDecimal MOV = ProcuraMov(NUNOTA);
		BigDecimal Custo = procurarPrecoCusto(CODPROD);
		BigDecimal Qtd = registro.asBigDecimal("QTDNEG");
		Set<BigDecimal> valoresValidos = new HashSet<>(Arrays.asList(
			    new BigDecimal("7800"),
			    new BigDecimal("7973"),
			    new BigDecimal("7996"),
			    new BigDecimal("8001"),
			    new BigDecimal("8002"),
			    new BigDecimal("8003"),
			    new BigDecimal("7995"),
			    new BigDecimal("9401"),
			    new BigDecimal("9403")
			));
		
		
		try {
			if (valoresValidos.contains(MOV)) 
			{
				
		AtualizarCus(NUNOTA,CODPROD,Custo,Qtd);
			}
			
		} catch (Exception e) {
			System.err.println("Erro ao Atualizar Nota: " + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	 private BigDecimal procurarPrecoCusto(BigDecimal CODPROD) throws Exception {
		 	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		    BigDecimal CUSTO = new BigDecimal(0);
		 	System.out.println("PrecoCusto >> CODPROD:" + CODPROD);
		    StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    sqlite1.append("SELECT CUS.CUSMED AS CUSTO ");
		    sqlite1.append("FROM TGFCUS CUS ");
		    sqlite1.append("JOIN ( ");
		    sqlite1.append("    SELECT I.CODPROD, MAX(C.DTENTSAI) AS DTENTSAI_MAX ");
		    sqlite1.append("    FROM TGFITE I ");
		    sqlite1.append("    JOIN TGFCAB C ON C.NUNOTA = I.NUNOTA ");
		    sqlite1.append("    WHERE ");
		    sqlite1.append("        C.TIPMOV = 'C' ");
		    sqlite1.append("        AND C.STATUSNOTA = 'L' ");
		    sqlite1.append("        AND YEAR(C.DTNEG) > 2021 ");
		    sqlite1.append("        AND I.CODPROD = " + CODPROD);
		    sqlite1.append("    GROUP BY I.CODPROD ");
		    sqlite1.append(") ULTIMA_ENT ");
		    sqlite1.append("    ON CUS.CODPROD = ULTIMA_ENT.CODPROD ");
		    sqlite1.append("   AND CUS.DTATUAL = ULTIMA_ENT.DTENTSAI_MAX ");
		    sqlite1.append("ORDER BY CUS.CODPROD ASC;");
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		    if(query.next())
				    {
		    	CUSTO = query.getBigDecimal("CUSTO");
		    	System.out.println("O produto é:"+CODPROD);
		    	System.out.println("O custo medio do Produto é:"+CUSTO);
				    }
		    query.close();
			return CUSTO;
	 }

	 private BigDecimal ProcuraMov(BigDecimal NUNOTA) throws Exception {
		 	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		    BigDecimal MOV = new BigDecimal(0);
		    StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    sqlite1.append("SELECT CODTIPOPER ");
		    sqlite1.append("FROM TGFCAB ");
		    sqlite1.append("WHERE NUNOTA = " + NUNOTA);
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		    if(query.next())
				    {
		    	MOV = query.getBigDecimal("CODTIPOPER");
		    	System.out.println("Mov da Nota é:"+MOV+"/ NUNOTA:"+NUNOTA);
				    }
		    query.close();
			return MOV;
	 }
	 
	 public void AtualizarCus (BigDecimal Nunota,BigDecimal CODPROD,BigDecimal CUSTO,BigDecimal QTDNEG)
	  {
		  JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
	        JapeSession.SessionHandle hnd = JapeSession.open();
	        NativeSql sql2 = new NativeSql(jdbc);
	        String ex ="UPDATE TGFITE SET VLRUNIT = "+CUSTO+",VLRTOT= "+CUSTO.multiply(QTDNEG)+ " WHERE NUNOTA= "+Nunota +" and CODPROD = " + CODPROD;
	        try {
	        	System.out.println("A query do Update do Custo:"+ex);
	        	sql2.executeUpdate(ex);
	  } catch (Exception a )
	  {
		  a.printStackTrace();
		  System.out.println("Deu erro no Update do Custo"+Nunota+"CODPROD>"+CODPROD);
	  }
	  finally {
		  JdbcWrapper.closeSession(jdbc);
			JapeSession.close(hnd);
	  }
	  } 
	 
	 
	 
	 
	 
	 
	@Override
	public void afterUpdate(PersistenceEvent ctx) throws Exception {
		 
		 
		
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

}
