package br.com.promocao.marketing;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
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
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.ImpressaoNotaHelpper;
import br.com.sankhya.modelcore.comercial.util.print.PrintManager;
import br.com.sankhya.modelcore.comercial.util.print.converter.PrintConversionService;
import br.com.sankhya.modelcore.comercial.util.print.model.PrintInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.ReportManager;
import br.com.sankhya.sps.enumeration.DocTaste;
import br.com.sankhya.sps.enumeration.DocType;
import br.com.sankhya.modelcore.comercial.util.print.PrintManager;
import br.com.sankhya.modelcore.comercial.util.print.converter.PrintConversionService;
import br.com.sankhya.modelcore.comercial.util.print.model.PrintInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.Report;
import br.com.sankhya.modelcore.util.ReportManager;
import br.com.sankhya.sps.enumeration.DocTaste;
import br.com.sankhya.sps.enumeration.DocType;
import net.sf.jasperreports.engine.JasperPrint;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

public class promocao implements AcaoRotinaJava {

	
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
	public String Imp = "";
	public String Dobro = "";
	public String nUnota = "";

	
	
		@Override
		public void doAction(ContextoAcao arg0) throws Exception {
			// TODO Auto-generated method stub
			usu = arg0.getUsuarioLogado(); 
			BigDecimal relatorio = new BigDecimal(298);	
			Registro line = arg0.getLinhas()[0];
			
			try {
			    	//int nuNota = (int) arg0.getParam("NUNOTA");
			    	String CPF  =  (String) arg0.getParam("CPF");
			    	//String CPF = (arg0.getParam("CPF") != null) ? (String) arg0.getParam("CPF") : "";
			    	// BigDecimal romaneio = new BigDecimal(nuNota);
			    		
					 
					 getinfo(line, arg0,CPF);
					 System.out.println("IMP"+Imp);
					 System.out.println("CPF"+CPF);
					 System.out.println("Dobro"+Dobro);
					 
					if ("S".equals(Imp))
					{
						System.out.println("Ja foi Impresso!!!");
						System.out.println("O Cupom já foi impresso");
         			   throw new IOException("<br><b>O Cupom Já foi Impresso: "+"<br><br><br>");
					}
					else
					{
						if ("S".equals(Dobro))
						{
							imprimir(relatorio,CPF,new Integer(2));
							Upd(line,arg0);
						}
						else
						{
							imprimir(relatorio,CPF,new Integer(1));
							Upd(line,arg0);
						}
					}
							 
			}
				    catch (Exception e) {
						e.printStackTrace();
						this.MsgErro = String.valueOf(this.MsgErro) + e.getMessage() + " <br> ";
					}finally {  
				    }
			
			if (this.MsgErro.equals("")) {
             } else {
           	  arg0.setMensagemRetorno(this.MsgErro);
            } 

		
		}
		
		 public void getinfo (Registro line,ContextoAcao arg0,String CPF) throws Exception
		 {
			 QueryExecutor rset = arg0.getQuery();
			 StringBuffer sqlverifica = new StringBuffer();
			 try {
			 sqlverifica.append("SELECT IMPRESSO,DOBRO AS DOBRO,NUNOTA AS NOTA");
			 sqlverifica.append(" FROM ");
			 sqlverifica.append(" CA_PROMOCAO ");
			 sqlverifica.append(" WHERE  CGC_CPF =" +"'"+CPF+"'");
			 rset.nativeSelect(sqlverifica.toString());
			 System.out.println("O SELECT"+sqlverifica.toString());
			 if (rset.next())
		    	{
		    		Imp = rset.getString("IMPRESSO");
		    		System.out.println("IMPRESSO"+Imp);
		    		Dobro = rset.getString("DOBRO");
		    		System.out.println("Dobro"+Dobro);
		    		nUnota = rset.getString("NOTA");
		    		System.out.println("Nunota da Empressao"+nUnota);
		    		nuNota = new BigDecimal(nUnota);
		    	}
			 }catch (Exception e) {
				 e.printStackTrace();
				  System.out.println("Deu Erro");}
			 finally {
				 rset.close();
				 }
		 }

		 

		 
		 private void Upd (Registro line,ContextoAcao arg0) throws Exception
		 {
			 NativeSql sql = new NativeSql(jdbc);
			 try {
				 
			
			 sql.setNamedParameter("P_NUNOTA", nuNota);
			 sql.executeUpdate(" UPDATE CA_PROMOCAO SET IMPRESSO = 'S' WHERE NUNOTA= :P_NUNOTA");
			 }
			 catch (Exception e) {
				 e.printStackTrace();
				  System.out.println("Deu Erro no Update");}
			 finally {
				 }
		 }
		 
		 
		
		
		//public void imprimir(BigDecimal romaneio, BigDecimal relatorio,String CPF) throws Exception{
		public void imprimir(BigDecimal relatorio,String CPF,int copie) throws Exception{
			EntityFacade dwfEntityFacade;
		    JdbcWrapper jdbc = null;
		   

		    try {

		        dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		        jdbc = dwfEntityFacade.getJdbcWrapper();
		        jdbc.openSession();
		        	       
		        Map<String, Object> param = new HashMap<>();
		        //param.put("NUNOTA", romaneio);
		        param.put("CPF", CPF);
		        		
		        Report report = ReportManager.getInstance().getReport(relatorio, dwfEntityFacade);

		        JasperPrint jasperPrint = report.buildJasperPrint(param,jdbc.getConnection());

		        byte[] conteudo = PrintConversionService.getInstance().convert(jasperPrint, byte[].class);
		        PrintManager printManager = PrintManager.getInstance();

		        String printerName = "?";
		        String jobDescription = "Impressão por job";
		        int copies = 1;

		        BigDecimal userId = AuthenticationInfo.getCurrent().getUserID();
		        String userName = "SUP";
		        BigDecimal codEmp = BigDecimal.ONE;
		        String idDocumento = "0";

		        PrintInfo printInfo = new PrintInfo(conteudo, DocTaste.JASPER, DocType.RELATORIO, printerName, jobDescription, copie, userId, userName, codEmp, idDocumento);

		        printManager.print(printInfo);

	        }
	        catch (Exception e) {
				e.printStackTrace();
			}finally {
		        JdbcWrapper.closeSession(jdbc);
		    }
			
		}
		
		
	}
		
		





