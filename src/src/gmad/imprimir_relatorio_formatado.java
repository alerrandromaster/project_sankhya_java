package src.gmad;

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

public class imprimir_relatorio_formatado implements AcaoRotinaJava {

	
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
			BigDecimal relatorio = new BigDecimal(298);
			try {
			    	int nuNota = (int) arg0.getParam("NUNOTA");
			    	//String CPF  =  (String) arg0.getParam("CPF");
			    	String CPF = (arg0.getParam("CPF") != null) ? (String) arg0.getParam("CPF") : "";
			    	 BigDecimal romaneio = new BigDecimal(nuNota);
					 imprimir(romaneio,relatorio,CPF);}

				    catch (Exception e) {
						e.printStackTrace();
					}finally {  
				    }

		
		}
		
		
		public void imprimir(BigDecimal romaneio, BigDecimal relatorio, String CPF) throws Exception{
			EntityFacade dwfEntityFacade;
		    JdbcWrapper jdbc = null;
		   

		    try {

		        dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		        jdbc = dwfEntityFacade.getJdbcWrapper();
		        jdbc.openSession();
		        	       
		        Map<String, Object> param = new HashMap<>();
		        param.put("Codigo Produto", romaneio); 
		        param.put("CPF",CPF);
		        		
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
		
		





