package br.com.imp.rom;

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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.ibm.icu.text.SimpleDateFormat;

public class impprom implements AcaoRotinaJava {

	
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
	public String Rota_Sankhya = "";
	public String Rota_Pegada = "";
	public String Data_Sanhkya = "";
	
		@Override
		public void doAction(ContextoAcao arg0) throws Exception {
			// TODO Auto-generated method stub
			usu = arg0.getUsuarioLogado(); 
			BigDecimal relatorio = new BigDecimal(303);
			Registro line = arg0.getLinhas()[0];
			try {
					getinfo(line, arg0);
			    	 BigDecimal romaneio = new BigDecimal(0);
					 System.out.println("Rota na Chamada Principal é"+Rota_Pegada);
					 System.out.println("A data na Chamada Principal é:"+Data_Sanhkya);
			    	 imprimir(romaneio,relatorio,Rota_Pegada,Data_Sanhkya);}
			 		
				    catch (Exception e) {
						e.printStackTrace();
					}finally {  
				    }

		}
		
		


		 public void getinfo (Registro line,ContextoAcao arg0) throws Exception
		 {
			 Rota_Sankhya = (String) line.getCampo("AD_IDPATHFIND");
			 Timestamp dataSankhyaTimestamp = (Timestamp) line.getCampo("DTPREVSAIDA");
			 
			 if (dataSankhyaTimestamp != null) {
				    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
				    Data_Sanhkya = sdf.format(dataSankhyaTimestamp);
				}
			 
			 
			 System.out.println("O ID DA ROTA DA TGFORD é"+Rota_Sankhya);
			 System.out.println("A DATA DA ROTA DA TGFORD é"+Data_Sanhkya);
			 
			 QueryExecutor rset = arg0.getQuery();
			 StringBuffer sqlverifica = new StringBuffer();
			 try {
			 sqlverifica.append("SELECT CASE \r\n"
			 		+ "        WHEN PATINDEX('%[^0-9]%', LEFT(AD_IDPATHFIND, 5)) = 0 \r\n"
			 		+ "            THEN LEFT(AD_IDPATHFIND, 5)\r\n"
			 		+ "        ELSE LEFT(AD_IDPATHFIND, PATINDEX('%[^0-9]%', AD_IDPATHFIND) - 1)\r\n"
			 		+ "    END AS NUMERO_ROTA");
			 sqlverifica.append(" FROM TGFORD");
			 sqlverifica.append(" WHERE AD_IDPATHFIND = "+"'"+ Rota_Sankhya+"'" );
			 rset.nativeSelect(sqlverifica.toString());
			 System.out.println("O SELECT"+sqlverifica.toString());
			 if (rset.next())
		    	{
				 Rota_Pegada = rset.getString("NUMERO_ROTA");
				 System.out.println("A O IDPATHFIND TRANSFORMADO é:"+Rota_Pegada);
		    	}
			 }catch (Exception e) {
				 e.printStackTrace();
				  System.out.println("Deu Erro");}
			 finally {
				 rset.close();
				 }
		 }
		
		
		
		
		public void imprimir(BigDecimal romaneio, BigDecimal relatorio, String Rota,String Dt) throws Exception{
			EntityFacade dwfEntityFacade;
		    JdbcWrapper jdbc = null;
		   

		    try {

		        dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		        jdbc = dwfEntityFacade.getJdbcWrapper();
		        jdbc.openSession();
		        	       
		        Map<String, Object> param = new HashMap<>();
		        param.put("P_ID", Rota); 
		        param.put("P_DATA",Dt);
		        		
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

		        PrintInfo printInfo = new PrintInfo(conteudo, DocTaste.JASPER, DocType.RELATORIO, printerName, jobDescription, copies, userId, userName, codEmp, idDocumento);

		        printManager.print(printInfo);

	        }
	        catch (Exception e) {
				e.printStackTrace();
			}finally {
		        JdbcWrapper.closeSession(jdbc);
		    }
			
		}
		
		
	}
		
		





