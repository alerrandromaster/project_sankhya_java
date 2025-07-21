package br.com.inserir.codbarras;

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

import com.hazelcast.internal.nio.Connection;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.TimeUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;




public class AtualizaCodBarraVOA implements EventoProgramavelJava {
	
	
	
	  public void afterInsert(PersistenceEvent arg0) throws Exception {
		  atualizarcodigobarra(arg0);
		    
		  }

	private void atualizarcodigobarra(PersistenceEvent arg0) throws Exception {
		DynamicVO Voa = (DynamicVO)arg0.getVo();
		BigDecimal Codprod = BigDecimalUtil.getValueOrZero((BigDecimal)Voa.getProperty("CODPROD"));
		String codvol = Voa.asString("CODVOL");


	    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	    System.out.println("EVENTO ON");
	    NativeSql sqlverifica = new NativeSql(jdbc);
		String codbarra =generateUniqueBarcode();
	    
        sqlverifica.executeUpdate("UPDATE TGFVOA SET CODBARRA =" + codbarra + "WHERE CODPROD = "+ Codprod + " AND CODVOL = " + "'"+codvol+"'" + " AND CONTROLE = ''" );
	   

	}
	
	public static boolean doesBarcodeExist(String barcode) throws Exception {
	    EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	    NativeSql nativeSql = new NativeSql(jdbc);

	    StringBuilder sql = new StringBuilder();
	    sql.append("SELECT CODBARRA ");
	    sql.append("FROM TGFVOA ");
	    sql.append("WHERE CODBARRA = ?");

	    try (PreparedStatement statement = jdbc.getConnection().prepareStatement(sql.toString())) {
	        statement.setString(1, barcode); // Usa um parâmetro seguro
	        try (ResultSet resultSet = statement.executeQuery()) {
	            return resultSet.next(); // Retorna true se o código de barras existir
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Erro ao verificar código de barras", e);
	    }
	}
	
	
	

	public static String generateUniqueBarcode() {
		  Random random = new Random();

	        // Escolhe prefixo 789 ou 790
	        String prefix = random.nextBoolean() ? "789" : "790";

	        // Gera os 9 dígitos restantes aleatórios (com zero à esquerda se necessário)
	        StringBuilder middle = new StringBuilder();
	        for (int i = 0; i < 9; i++) {
	            middle.append(random.nextInt(10)); // dígitos de 0 a 9
	        }

	        String base12 = prefix + middle.toString(); // 12 primeiros dígitos

	        // Calcula o dígito verificador (13º dígito)
	        int sum = 0;
	        for (int i = 0; i < base12.length(); i++) {
	            int digit = Character.getNumericValue(base12.charAt(i));
	            sum += (i % 2 == 0) ? digit : digit * 3;
	        }

	        int checkDigit = (10 - (sum % 10)) % 10;

	        return base12 + checkDigit;
	    }
	
	
	
	  /*public static String generateUniqueBarcode() throws Exception {
	        String novoCodigoBarra;
	        Random random = new Random();

	        do {
	            // Gera um valor único similar ao CHECKSUM(NEWID()) % 1000000000 + 1000000000
	            long uniqueValue = Math.abs(UUID.randomUUID().getLeastSignificantBits());
	            long barcodeValue = uniqueValue % 1000000000 + 1000000000;
	            novoCodigoBarra = Long.toString(barcodeValue);
	        } while (doesBarcodeExist(novoCodigoBarra));

	        return novoCodigoBarra;
	    }*/

	
	
	
	
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

		
	}

	@Override
	public void beforeInsert(PersistenceEvent arg0) throws Exception {

		
	}
}
