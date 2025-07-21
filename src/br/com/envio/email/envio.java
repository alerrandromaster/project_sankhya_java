package br.com.envio.email;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.DynamicEntityNames;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.*;

	

	public class envio implements AcaoRotinaJava {

	    public BigDecimal NUnota;
	    public String Contexto = "";
	    public BigDecimal Codparc;
	    public String NOMEPARC = "";
	    public byte[] anex2;
	    public void doAction(ContextoAcao contextoAcao) throws Exception {
	        System.out.println("BOTAO ENVIAR EMAIL");
	        
	        Registro line = contextoAcao.getLinhas()[0];
	        
	        String seuEmail = "alerrandro.barreto@centrodoaluminio.com.br";
	        getinfo(line,contextoAcao);
	        BigDecimal Nunota = NUnota;
	        
	        System.out.println("Nunota do Email é"+Nunota);
	        byte[] binarioRelatorio = getAnexo(Nunota);
	        System.out.print("O BINARIO é"+binarioRelatorio);

	        
	        System.out.println("NOME DO PARCEIRO é"+NOMEPARC);	
	        // Enviar o email com anexo
	        enviarEmailComRelatorio(binarioRelatorio, Contexto.toCharArray(), NOMEPARC, seuEmail);

	        System.out.println("FIM");
	    }

	    public void getinfo (Registro line,ContextoAcao contextoAcao) throws Exception
	    {
	    	NUnota = (BigDecimal) line.getCampo("NUNOTA");
	    	Contexto = (String) line.getCampo("OBSERVACAO");
	    	Codparc = (BigDecimal)line.getCampo("CODPARC");
	    	
	    	QueryExecutor rset = contextoAcao.getQuery();
			 StringBuffer sqlverifica = new StringBuffer();
			 try {
			 sqlverifica.append("SELECT NOMEPARC");
			 sqlverifica.append(" FROM ");
			 sqlverifica.append(" TGFPAR ");
			 sqlverifica.append(" WHERE  CODPARC ="+Codparc);
			 rset.nativeSelect(sqlverifica.toString());
			 System.out.println("O SELECT COMPLETO"+sqlverifica);
			 if (rset.next())
		    	{
				 NOMEPARC = rset.getString("NOMEPARC");
		    	}
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				  System.out.println("Deu Erro");}
			 finally {
				 rset.close();
				 }
	    }
	    	
	    
	
	    
	    
	    private byte[] getAnexo(BigDecimal nunota) throws Exception {
	        byte[] pdfAn = null;
	        String Conteudo = "";

	        EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
	        JdbcWrapper jdbc = entityFacade.getJdbcWrapper();
	        NativeSql nativeSql = new NativeSql(jdbc);
	        jdbc.openSession();
	        StringBuilder sqlNotaProducao = new StringBuilder();
	       
	        try {
	        	
	        	 
		        sqlNotaProducao.append(" SELECT DESCRICAO");
		        sqlNotaProducao.append(" FROM TSIATA ");
		        sqlNotaProducao.append(" WHERE CODATA = " + nunota);
		        ResultSet queryNota = nativeSql.executeQuery(sqlNotaProducao.toString());
		        if (queryNota.next()) {
		        	Conteudo = queryNota.getString("DESCRICAO");
		        }
	          	
				JapeWrapper ANEXODAO = JapeFactory.dao(DynamicEntityNames.ANEXO);
				DynamicVO anexo = ANEXODAO.findByPK(nunota,"N",Conteudo,BigDecimal.ZERO,BigDecimal.ZERO);
				System.out.println("Anexo é pk"+anexo.asBigDecimal("CODATA"));  
				System.out.println("Anexo é pk"+anexo.asString("ARQUIVO"));  
				
				
				if (anexo != null) {
				    pdfAn = anexo.asBlob("CONTEUDO");
				} else {
				    System.out.println("Nenhum anexo encontrado para o ID: " + nunota);
				}
				
	            
	            System.out.println("O CONTEUDO CONVERTIDO BYTES é"+pdfAn);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new SQLException("Erro ao obter anexo: " + e.getMessage(), e);
	        } finally {
	            jdbc.closeSession();
	            JdbcWrapper.closeSession(jdbc);

	        }
	        return pdfAn;
	    }
	    

	    public static void enviarEmailComRelatorio(byte[] relatorio, char[] mensagem, String assunto, String email) throws Exception {
	        BigDecimal codigoFila = BigDecimal.ZERO;
	        BigDecimal nuAnexoRelatorio = BigDecimal.ZERO;
	        JapeSession.SessionHandle hnd = null;
	        try {
	            hnd = JapeSession.open();
	            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

	            // Email
	            EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
	            DynamicVO dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("ASSUNTO", assunto);
	            dynamicVO.setProperty("DTENTRADA", TimeUtils.getNow());
	            dynamicVO.setProperty("STATUS", "Pendente");
	            dynamicVO.setProperty("EMAIL", email);
	            dynamicVO.setProperty("TENTENVIO", new BigDecimal(1));
	            dynamicVO.setProperty("MENSAGEM", mensagem);
	            dynamicVO.setProperty("TIPOENVIO", "E");
	            dynamicVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
	            dynamicVO.setProperty("CODSMTP", new BigDecimal(3));
	            dynamicVO.setProperty("CODCON", new BigDecimal(0));
	            PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDFilaMensagem", entityVO);
	            DynamicVO save = (DynamicVO) createEntity.getValueObject();
	            codigoFila = ((DynamicVO) createEntity.getValueObject()).asBigDecimal("CODFILA");



	            // Cria anexo do relatorio
	            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoMensagem");
	            dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("NOMEARQUIVO", "relatorio.pdf");
	            dynamicVO.setProperty("TIPO", "application/pdf");
	            dynamicVO.setProperty("ANEXO", relatorio);
	            createEntity = dwfFacade.createEntity("AnexoMensagem", entityVO);
	            save = (DynamicVO) createEntity.getValueObject();
	            nuAnexoRelatorio = save.asBigDecimal("NUANEXO");
	            

	            // Fila de mensagem
	            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoPorMensagem");
	            dynamicVO = (DynamicVO) entityVO;
	            dynamicVO.setProperty("CODFILA", codigoFila);
	            dynamicVO.setProperty("NUANEXO", nuAnexoRelatorio);
	            createEntity = dwfFacade.createEntity("AnexoPorMensagem", entityVO);
	            save = (DynamicVO) createEntity.getValueObject();
	        } catch (Exception e) {
	            throw new MGEModelException("Erro ao tentar incluir os dados dentro do e-mail!" + e.getMessage());
	        } finally {
	            JapeSession.close(hnd);
	        }
	    }
	}


