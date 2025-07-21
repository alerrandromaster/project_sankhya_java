package src.gmad;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.comercial.ImpressaoNotaHelpper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;

import com.google.gson.JsonObject;

public class Imprimir_nota implements AcaoRotinaJava {
	
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	  NativeSql nativeSql = new NativeSql(jdbc);
	  StringBuilder stringBuild = new StringBuilder();
	  
public BigDecimal usu = new BigDecimal(0);
public BigDecimal nuNota = new BigDecimal(0);
public BigDecimal contador = new BigDecimal(0);
public String contador_s = "";
public BigDecimal id_print = new BigDecimal(0);
public BigDecimal idnovo = new BigDecimal(0);

public String idnew = "";
public BigDecimal idNew = new BigDecimal(0);
public  String MsgErro = "";
	@Override
	public void doAction(ContextoAcao arg0) throws Exception {
		// TODO Auto-generated method stub
		
		usu = arg0.getUsuarioLogado(); 
		Registro[] linhas = arg0.getLinhas();
	  
     	
        for (Registro linha : linhas) {
            nuNota = (BigDecimal) linha.getCampo("NUNOTA");;
            getinfo(linhas,arg0,usu,nuNota);

            try {
            	System.out.println("O contador é"+contador);
            	if (contador.compareTo(BigDecimal.valueOf(2)) < 0) {
                   	System.out.print("O Valor do Novo numero é:"+idnovo);
                ImpressaoNotaHelpper impressaoNotaHelpper = new ImpressaoNotaHelpper();
                impressaoNotaHelpper.inicializaNota(nuNota);	
                impressaoNotaHelpper.imprimirNota();
            		inserirComposicao(idNew,usu,nuNota,new BigDecimal(1));
            		   System.out.println("COMPLETOU CAMINHO DA IMPRESSAO");
            }else {
            			   System.out.println("O Caminho DEu ERRO");
            			   throw new IOException("<br><b>A nota so pode ser impressa uma vez: "+"<br><br><br>");
            		   }
               
            } catch (Exception e) {
                e.printStackTrace();
                this.MsgErro = String.valueOf(this.MsgErro) + e.getMessage() + " <br> ";
            }
            
            if (this.MsgErro.equals("")) {
              } else {
            	  arg0.setMensagemRetorno(this.MsgErro);
              } 
	}
        
        
	}
	
	public void getinfo (Registro[] linhas, ContextoAcao arg0,BigDecimal Numero,BigDecimal Nu) throws Exception {
		 QueryExecutor rset = arg0.getQuery();
		QueryExecutor rset1 = arg0.getQuery();
		StringBuffer sql = new StringBuffer();
		StringBuffer sq2 = new StringBuffer();
		try {
		
		System.out.println("Usuario é "+usu+"Nunota é :"+nuNota);	
		sql.append(" SELECT count(*) as C");
		sql.append(" FROM ");
		sql.append(" AD_PRINTHS ");
		sql.append(" WHERE ");
		sql.append(" USU = " + Numero + " and NUNOTA = " +Nu);
		rset.nativeSelect(sql.toString());
		
		if (rset.next())
		{
			System.out.println("Caiu no Loop de Verificação");
			contador_s = rset.getString("C");
			contador = new BigDecimal(contador_s);
			System.out.println("o valor da conta é:"+contador);
			
		}
		
		sq2.append(" SELECT count(*)+1 as NEW");
		sq2.append(" FROM ");
		sq2.append(" AD_PRINTHS ");
		rset.nativeSelect(sq2.toString());
		
		if (rset1.next())
		{
			System.out.println("Caiu na Criação de New Id");
			idnew = rset1.getString("NEW");
			idNew = new BigDecimal(idnew);
			System.out.println("O id novo é:"+idNew);
		}
		
	 }catch (Exception e) {
		 e.printStackTrace();
		  System.out.println("Deu Erro");}
	 finally {
		 rset.close();
		 rset1.close();
		 }
	 }
		
		
	
	
	
	private void inserirComposicao(BigDecimal id_print, BigDecimal usu, BigDecimal nuNota,BigDecimal QtdPrint)
			 throws Exception {
	    try {
	      JapeWrapper composicaoDAO = JapeFactory.dao("AD_PRINTHS");
	      DynamicVO dynamicVO = (DynamicVO) (((FluidCreateVO)((FluidCreateVO)((FluidCreateVO)
	    composicaoDAO.create().set("USU", usu)).set("NUNOTA", nuNota)).set("QTDPRINT", QtdPrint)).save());
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("O INSERT DO PRINT DEU ERRADO ");
	    } 
	  }
	
	
	/*  public BigDecimal criarId(BigDecimal a) {
			SessionHandle hnd = null;
			JdbcWrapper jdbc = null;

			try {
				hnd = JapeSession.open();
				EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

				jdbc = dwfFacade.getJdbcWrapper();
				jdbc.openSession();
				
				Integer idimp = 0;
				String rec = "";
				

				CallableStatement cstmt = jdbc.getConnection().prepareCall("{call STP_KEYGEN_TGFNUM(?,?,?,?,?,?)}");
				cstmt.setQueryTimeout(60);

				cstmt.setString(1, "AD_PRINTHS");
				cstmt.setBigDecimal(2, BigDecimal.ONE);
				cstmt.setString(3, "AD_PRINTHS");
				cstmt.setString(4, "ID");
				cstmt.setBigDecimal(5, BigDecimal.ZERO);
		    
				cstmt.registerOutParameter(6, idimp);
				rec = (String) cstmt.getObject(6);
				System.out.println("O registro de Saida do KEy é: "+rec);
				

				cstmt.execute();

				id_print = (BigDecimal) cstmt.getObject(6);
				


				System.out.println("Saida: " + id_print);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("DEU ERRADO A CRIAÇÂO NO KEYGEN");
			} finally {
				JdbcWrapper.closeSession(jdbc);
				JapeSession.close(hnd);
			}
			return id_print;
		}
	*/
	
	
}
	
	


