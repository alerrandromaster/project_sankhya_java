package br.importacaorotativo;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.StringWriter;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class ImportRotativo implements AcaoRotinaJava {

	 EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
     public JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
    @Override
    public void doAction(final ContextoAcao contextoAcao) throws Exception {
        final Registro[] registros = contextoAcao.getLinhas();
       

        if (registros.length == 0) {
            throw new MGEModelException("Linha não selecionada!");
        }

        BigDecimal codImp = (BigDecimal) registros[0].getCampo("ID");

        // Valida se arquivo já foi processado
       

        final JapeWrapper impDAO = JapeFactory.dao("AD_IMPROT");
        final DynamicVO impVo = impDAO.findOne("ID = ?", new Object[]{codImp});
        byte[] blobData = impVo.asBlob("ARQUIVO");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(blobData);
             Scanner scanner = new Scanner(inputStream)) {

        	int nlinha = 0;

            // Desconsidera as primeiras 7 linhas (cabeçalho e metadados)
            for (int i = 0; i < 2; i++) {
            	nlinha++;
                if (scanner.hasNextLine()) {
                    System.out.println("A LINHA ATUAL é "+scanner.nextLine().toString());
                	scanner.nextLine();
                    
                } else {
                    throw new IllegalArgumentException("O arquivo contém menos de 7 linhas, impossível processar.");
                }
            }
            
            

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println("Linha original (bruta): [" + line + "]");
                line = line.trim();

                if (line.isEmpty()) {
                    System.out.println("Linha ignorada pois está vazia");
                    continue;
                }
                String[] tokens = line.split(";");

                for (int i = 0; i < tokens.length; i++) {
                    System.out.println("Token para o log é " + i + ": " + tokens[i]);
                }
                
                if (tokens.length < 5 || 
                        tokens[0].trim().isEmpty() || 
                        tokens[1].trim().isEmpty() || 
                        tokens[2].trim().isEmpty() || 
                        tokens[3].trim().isEmpty() || 
                        tokens[4].trim().isEmpty()) {
                        
                        continue;
                    } 
                	//tokens.length >= 0 && tokens.length <= 5
	                    nlinha++;
	                   
	                    System.out.println("Número de tokens: " + tokens.length);
	                    String Codprod = tokens[0];
	                    String Codlocal = tokens[1];
	                    String Conf = tokens[2];
	                    String Codemp = tokens[3];
	                    String Ciclo = tokens[4];
	                    
	                    
	                    
	                   // if (RetornaEnd(Codprod, Codlocal, Ciclo).compareTo(new BigDecimal(1)) == 0) {
	                     //   System.out.println("Linha ignorada pois já existe registro para: Codprod=" + Codprod + ", Codlocal=" + Codlocal + ", Ciclo=" + Ciclo);
	                       // continue;
	                    //}
	                    
	                    System.out.println("Codprod é"+Codprod);
	                    
	                    StringBuilder sqlite1 = new StringBuilder();
	            		NativeSql nativeSql = new NativeSql(jdbc);
	            		sqlite1.append(" SELECT DISTINCT "+ Ciclo+ " AS CICLO," +"GETDATE() AS DTCONT,"+Codprod+" AS PRODUTO,"+Codlocal+" as Local,"+
	            		"CASE WHEN EST.CONTROLE IS NULL THEN '' ELSE EST.CONTROLE END AS CONTROLE ,"+" LO.CODEND AS IDEND"+","+Codemp+" AS CODEMP,"+Conf+" AS Conferente");
	            		sqlite1.append(" FROM AD_ENDLOC LO INNER JOIN AD_CADEND ED ON ED.CODEND = LO.CODEND LEFT JOIN TGFEST EST ON EST.CODPROD = LO.CODPROD and EST.CODLOCAL = ED.CODLOCAL");
	            		sqlite1.append(" WHERE LO.CODLOCAL ="+ Codlocal + "and LO.CODEMP =" + Codemp + " AND LO.CODPROD= " + Codprod + "AND NOT EXISTS (SELECT 1 FROM AD_ROTCAB CC WHERE CC.CODPROD = "+ Codprod + " AND CC.CODLOCAL= " + Codlocal + " AND CC.IDCICLO= " + Ciclo+")");
	            		System.out.println("O Select do codlocal:" + sqlite1.toString());
	            		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
	            		while (query.next()) {
	            
	            			System.out.println("a query entrou no while");
	            			String Dtcont = query.getString("DTCONT");
	            			
	            			String Produto = query.getString("PRODUTO");
	            			System.out.println("PRODUTO"+Produto);
	            			String Controle = query.getString("CONTROLE");
	            			String Idend = query.getString("IDEND");
	            			System.out.println("End"+Idend);
	            			String EMP = query.getString("CODEMP");
	            			String confe = query.getString("Conferente");
	            			String local = query.getString("Local");
	            			
	            			System.out.println("a query finalizou no while");
	            		
	            			Registro proc = contextoAcao.novaLinha("AD_ROTCAB");
	            		    proc.setCampo("IDCICLO", Ciclo);
		                    proc.setCampo("DTCONT", new Timestamp(System.currentTimeMillis()));
		                    proc.setCampo("CODPROD", Produto);
		                    proc.setCampo("CODLOCAL", local);
		                    proc.setCampo("CONTROLE", Controle);
		                    proc.setCampo("IDEND", Idend);
		                    proc.setCampo("CODEMP", Codemp);
		                    proc.setCampo("CODCONT",confe );
		                    
		                    try {
		                        proc.save();
		                        System.out.println("Inserção realizada com sucesso!");
		                    } catch (Exception ex) {
		                        System.out.println("Erro ao salvar registro: " + ex.getMessage());
		                        ex.printStackTrace();
		                    }
	            			
		                    System.out.println("a inserção falhou");
	            		}
	            		//query.close();
	            	    
	                    
	                    
	                
            	}
            

        } catch (Exception e) {
            throw new MGEModelException("Problemas ao importar na tabela detalhe,  Erro: " + e.getMessage(), e);

        }

        contextoAcao.setMensagemRetorno("Arquivo processado com sucesso!");
    }


  //Valida se ja tem registros importado desse aquivo
  	public int getValArq(ContextoAcao contexto, BigDecimal codImp) throws MGEModelException {
  	    QueryExecutor query = contexto.getQuery();
  	    String sql = null;
  	    int valarq = 0;
  	    
  	    sql = "SELECT COUNT(*) COUNT FROM AD_IMPLINCIELO WHERE NUIMP ='" + codImp +"'";

  	    try {
  	      query.nativeSelect(sql);
  	      
  	      if (query.next()) {
  	    	  valarq = query.getInt("COUNT");
  	    	  
  	      }
  	      
  	      query.close();
  	    }
  	    catch (Exception e) {
  	    	throw new MGEModelException("Problemas buscar linha importada, Erro: " + e.getMessage(), e);
  	    }
  	    return valarq;
  	  }
  	
  	
  	public BigDecimal RetornaEnd(String IDCICLO,String Produto, String local) throws Exception {
		BigDecimal end = new BigDecimal(0);
		StringBuilder sqlite1 = new StringBuilder();
		NativeSql nativeSql = new NativeSql(jdbc);
		sqlite1.append(" SELECT 1 AS CONT");
		sqlite1.append(" FROM AD_ROTCAB CC");
		sqlite1.append(" WHERE CC.CODPROD= "+ Produto+" and CC.CODLOCAL = "+ local + " and CC.IDCICLO= " + IDCICLO);
		System.out.println("O Select da verificação do ciclo é" + sqlite1.toString());
		ResultSet query = nativeSql.executeQuery(sqlite1.toString());
		if (query.next()) {
			String Loc = query.getString("CONT");
			System.out.println("O Codlocal é:" + Loc);
			end = new BigDecimal(Loc);
		}
		query.close();

		return end;
	}
	
  	
  	
  	
  	
  	
  	
  	
  	
  	
  	
}
