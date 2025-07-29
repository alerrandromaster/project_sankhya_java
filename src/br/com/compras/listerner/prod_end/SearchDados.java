package br.com.compras.listerner.prod_end;


import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.sql.ResultSet;




public class SearchDados implements EventoProgramavelJava{
	
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	public String Local = "";

	@Override
	public void afterDelete(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
	
	private String procuraSepar(String ALMOX, String COM_IND) throws Exception {
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
		StringBuilder sqlite1 = new StringBuilder();
	    NativeSql nativeSql = new NativeSql(jdbc);
	    String Area = "";
		sqlite1.append(" select top 1 en.CODEND as Eend,sp.CODLOCAL as Local ");
		sqlite1.append(" from ad_areasep sp ");
		sqlite1.append(" inner join AD_ENDSEP en ON en.CODAREA = sp.CODAREA ");
		sqlite1.append(" WHERE sp.ALMOX = "+"'" + ALMOX +"'"+ " AND COM_IND = " +"'" +COM_IND +"'" +" and en.VIR_END = 'S'");
	    //sqlite1.toString();
		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
	    
	    if(query.next())
			    {
	    	Area = query.getString("Eend");
	    	Local = query.getString("Local");
			    }
	    query.close();
	    
	    return Area;
 }

	@Override
	public void afterInsert(PersistenceEvent ctx) throws Exception {
		DynamicVO registro = (DynamicVO)ctx.getVo();
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
		BigDecimal CODPROD = registro.asBigDecimal("CODPROD");
		String ALMOX = registro.asString("AD_ALMOX") == null ? "" : registro.asString("AD_ALMOX");
		String USOPROD = registro.asString("USOPROD");
		String End = "";
		 //String ATIVO = registro.asString("ATIVO");
		
		
		/* if (ALMOX.isEmpty()) {
			 exibirMensagem(CODPROD);
		 }*/
		
		if (USOPROD.contains("M"))
		{
			
			End = procuraSepar(ALMOX,"N");
			JapeWrapper ENDSEP = JapeFactory.dao("AD_ENDLOC");
			BigDecimal END = new BigDecimal(End);
			BigDecimal LOC = new BigDecimal(Local);
			ENDSEP.create()
	    	.set("CODPROD", CODPROD)
	    	.set("CODEMP", new BigDecimal(9)) 
	    	.set("CODLOCAL",LOC )
	    	.set("CODEND",END)
	    	.save();	
		}
		else 
		{
			End = procuraSepar(ALMOX,"S");
			JapeWrapper ENDSEP = JapeFactory.dao("AD_ENDLOC");
			BigDecimal END = new BigDecimal(End);
			BigDecimal LOC = new BigDecimal(Local);
			ENDSEP.create()
	    	.set("CODPROD", CODPROD)
	    	.set("CODEMP", new BigDecimal(10)) 
	    	.set("CODLOCAL",LOC )
	    	.set("CODEND",END)
	    	.save();	
			
		}
		
		
	}

	
	@Override
	public void afterUpdate(PersistenceEvent arg0) throws Exception {
		// TODO Auto-generated method stub
		 DynamicVO registro = (DynamicVO)arg0.getVo();
		 String ALMOX = "";
		 String ATIVO = registro.asString("ATIVO");
		 //String Resu = "";
		 //String TemNota = "";
		 BigDecimal CODPROD = registro.asBigDecimal("CODPROD");
		// BigDecimal CODGRUPOPROD = new BigDecimal(0);
		// CODGRUPOPROD = registro.asBigDecimal("CODGRUPOPROD");
		 System.out.println("AD_ALMOX é"+ALMOX);
		 System.out.println("ATIVO é"+ATIVO);
		 System.out.println("CODPROD é "+CODPROD);
		 ALMOX = registro.asString("AD_ALMOX") == null ? "" : registro.asString("AD_ALMOX");
		//Resu = ProcuraLiberacao(CODPROD);
		//TemNota = ProcuraNota(CODPROD);
		//System.out.println("A variavel TemNota é:"+TemNota);
		 if (ATIVO.contains("N") && ALMOX.isEmpty()) {
			 exibirMensagem(CODPROD);
		 }
		 //System.out.println("O Resultado é"+Resu);
		 //if (ATIVO.contains("S") && Resu.equals("NAO"))
			//	 {
		//	 exibirMensagem2(CODPROD);
		//}
		 
		 
		     
	}

	 public static void exibirMensagem(BigDecimal codprod) throws IOException {
		    throw new IOException("O campo Almox Padrão não está preenchido do Produto :"+codprod);
		  }
	 public static void exibirMensagem2(BigDecimal codprod) throws IOException {
		    throw new IOException("O Produto Pendente de Liberação:"+codprod);
		  }
	
	/* 
	 private String ProcuraLiberacao(BigDecimal CODPROD) throws Exception {
		 EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		 StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    BigDecimal VLRATUAL = new BigDecimal(0);
		    BigDecimal VLRLIBERADO = new BigDecimal(0);
		    String Resultado = "" ;
		    sqlite1.append(" SELECT VLRATUAL,VLRLIBERADO");
			sqlite1.append(" FROM TSILIB ");
			sqlite1.append(" WHERE VLRDESDOB = " + CODPROD);
			System.out.println("A QUERY é:"+sqlite1.toString());
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		    if(query.next())
				    {
		    	VLRATUAL=query.getBigDecimal("VLRATUAL");
		    	VLRLIBERADO=query.getBigDecimal("VLRLIBERADO");	
		    	System.out.println ("O VALOR ATUAL é :"+VLRATUAL);
		    	System.out.println ("O VALOR VLRLIBERADO é :"+VLRLIBERADO);
		    	if (VLRATUAL != null && VLRLIBERADO != null && VLRATUAL.compareTo(VLRLIBERADO) == 0) {
		    	    System.out.println("Valores iguais");
		    	    Resultado = "LIBERADO";
		    	}else
		    	{
		    		Resultado = "NAO";
		    	}
		    	 
				    }
		    query.close();
			return Resultado;

	 }*/
	 
	 
	/* private String ProcuraNota(BigDecimal CODPROD) throws Exception {
		 EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		 JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
		 StringBuilder sqlite1 = new StringBuilder();
		    NativeSql nativeSql = new NativeSql(jdbc);
		    BigDecimal VLRATUAL = new BigDecimal(0);
		    String Resultado = "" ;
		    sqlite1.append(" select COUNT(*) AS CONTAGEM");
			sqlite1.append("  from TGFCAB C INNER JOIN TGFITE I ON C.NUNOTA = I.NUNOTA ");
			sqlite1.append(" WHERE  C.TIPMOV = 'V' and C.STATUSNOTA = 'L' AND I.CODPROD =" + CODPROD);
			System.out.println("A QUERY é:"+sqlite1.toString());
		    ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		    if(query.next())
				    {
		    	VLRATUAL=query.getBigDecimal("CONTAGEM");
		    	System.out.println ("O VALOR DO COUNT é :"+VLRATUAL);
		    	if (VLRATUAL.compareTo(BigDecimal.ZERO) > 0) {
		    	    System.out.println("Valores iguais");
		    	    Resultado = "LIBERADO";
		    	}else
		    	{
		    		Resultado = "NAO";
		    	}
		    	 
				    }
		    query.close();
			return Resultado;

	 }*/
	 
	 
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
