package br.com.wmsliberardoca;

import br.com.sankhya.mgewms.model.helpper.ExpedicaoMercadoriaHelper;

import java.math.BigDecimal;
import java.util.Collection;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.PersistenceException;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.dao.TXLock;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.FinderWrapper;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidUpdateVO;
import br.com.sankhya.mgewms.model.builders.ExtratoProdutoBuilder;
import br.com.sankhya.mgewms.model.builders.ExtratoTarefaBuilder;
import br.com.sankhya.mgewms.model.resources.LoaderSql;
import br.com.sankhya.mgewms.model.separacoes.ItemSeparacaoAvulso;
import br.com.sankhya.mgewms.model.services.MgeWmsSPBean;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.helper.WmsHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

import br.com.sankhya.dwf.services.ServiceUtils;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.mgecomercial.model.facades.FormacaoCargaSP;
import br.com.sankhya.mgecomercial.model.facades.FormacaoCargaSPHome;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import java.math.BigDecimal;
import br.com.sankhya.ws.ServiceContext;
import org.jdom.Element;

import com.sankhya.util.XMLUtils;


public class liberacaodoca implements AcaoRotinaJava {
	
	private WmsHelper wmsHelper;
	public BigDecimal usu;
	 private EntityFacade dwfEntityFacade;
	 private JdbcWrapper jdbc;
	NativeSql nativeSql = new NativeSql(jdbc);


	
	@Override
	public void doAction(ContextoAcao arg0) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Doca Sendo Leberada!!!");
		usu = arg0.getUsuarioLogado(); 
		Registro line = arg0.getLinhas()[0];
		//getinfo(line,arg0);
		
		
		ExpedicaoMercadoriaHelper(dwfEntityFacade,jdbc);
		//leberarDocar();
				

		//leberaDoca();
	}
	
	
	/* public void getinfo (Registro line,ContextoAcao arg0) throws Exception
	 {
		BigDecimal Nuseparacao = (BigDecimal) line.getCampo("NUSEPAR"); 
		System.out.print("O numero da Separação é"+Nuseparacao);
	 }*/
	
	
	public void ExpedicaoMercadoriaHelper(EntityFacade dwfEntityFacade, JdbcWrapper jdbc) throws Exception {
	    this.dwfEntityFacade = dwfEntityFacade;
	    this.wmsHelper = new WmsHelper(AuthenticationInfo.getCurrent().getUserID());
	    this.jdbc = jdbc;
	    WmsHelper.CacheCenter.registryInstance(dwfEntityFacade);
	    WmsHelper.CacheCenter.getInstance().putJdbcOnCache(jdbc);
	    try {
	    this.wmsHelper.setaDocaLiberada(new BigDecimal(71));
	   System.out.println("Wms helper é:"+ wmsHelper.toString());
	   this.wmsHelper.ordemCargaEnviadaWMS(new BigDecimal(10), new BigDecimal(328));
	    }catch (Exception e) {
	    	System.out.println("o Help Nao Funcionou: " + e.fillInStackTrace());
	    	e.printStackTrace();
	    }
	  }
	
	/*
	public void leberarDocar()
	{
		EntityFacade entityFacade = EntityFacadeFactory.getDWFFacade();
	    JdbcWrapper jdbc = entityFacade.getJdbcWrapper();
	    try {
	        ServiceContext ctx = ServiceContext.getCurrent();
	        Element parametrosElem = XMLUtils.getRequiredChild(ctx.getRequestBody(), "parametros");
	        Element requestBody = new Element("requestBody");
	       System.out.println("O REQUEST BODY é esse"+ requestBody.toString());
	        ctx.setRequestBody(requestBody);
	        Collection<Element> docasElement = parametrosElem.getChildren();
	        WmsHelper wmsHelper = new WmsHelper(AuthenticationInfo.getCurrent().getUserID());
	        ctx.getBodyElement().setAttribute("docaLiberada", "true");

	      } catch (Exception e) {
	        System.out.println("ErroConfirmarApontamento:" + e.getMessage());
	        e.toString();
	        e.printStackTrace();
	      } 


	}
	
	*/
	 
	
	/*public void leberaDoca() throws Exception
	{
		wmsHelper.setaDocaLiberada(new BigDecimal(78));
	}
*/
	
}
