package br.com.imp_lista_sep;
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

public class imp_sep implements AcaoRotinaJava {
	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	  NativeSql nativeSql = new NativeSql(jdbc);
	  StringBuilder stringBuild = new StringBuilder();
	  public String nusepar = "";
	  public BigDecimal separacao = new BigDecimal(0);
	@Override
	
	public void doAction(ContextoAcao arg0) throws Exception {
		// TODO Auto-generated method stub
		
		  BigDecimal relatorio = new BigDecimal(312);
			Registro line = arg0.getLinhas()[0];
			try {
				
				getinfo(line, arg0);
				System.out.println("o numero da Separação no main é"+nusepar);
		    	 imprimir(relatorio);
	}
			    catch (Exception e) {
					e.printStackTrace();
				}
	}
			
			
	public void getinfo (Registro line,ContextoAcao arg0) throws Exception
	 {
		separacao = (BigDecimal)line.getCampo("NUSEPARACAO");
		nusepar = separacao.toString();
		 System.out.println("o numero da Separacao é "+nusepar);		 }

	

	public void imprimir(BigDecimal relatorio) throws Exception{
		EntityFacade dwfEntityFacade;
	    JdbcWrapper jdbc = null;
	   

	    try {

	        dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	        jdbc = dwfEntityFacade.getJdbcWrapper();
	        jdbc.openSession();
	        	       
	        Map<String, Object> param = new HashMap<>();
	        param.put("NuSepar", nusepar); 
	        
	        System.out.println("O parametro na nusepar é"+nusepar);
	        		
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
