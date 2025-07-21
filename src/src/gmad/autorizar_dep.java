package src.gmad;

import java.io.IOException;
import java.math.BigDecimal;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.QueryExecutor;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.util.JapeSessionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.vo.PrePersistEntityState;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.BarramentoRegra;
import br.com.sankhya.modelcore.comercial.CentralFaturamento;
import br.com.sankhya.modelcore.comercial.ConfirmacaoNotaHelper;
import br.com.sankhya.modelcore.comercial.centrais.CACHelper;
import br.com.sankhya.modelcore.facades.avisossistema.AvisoSistemaHelper.AvisoSistema;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.BigDecimalUtil;
import com.sankhya.util.StringUtils;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import br.com.sankhya.ws.ServiceContext;
import java.net.URLEncoder;

import java.util.Collection;

public class autorizar_dep implements AcaoRotinaJava {
	public BigDecimal codparc = new BigDecimal(0);
	public String Codparc_s = "";
	public BigDecimal Nufin = new BigDecimal(0);
	public String CodConta = "";
	public String TipoTitulo = "";
	public String Top = "";
	public BigDecimal CodigoTop = new BigDecimal(0);
	public String NUCOMP = "";
	public BigDecimal NuCompess = new BigDecimal(0);
	public String DHBAIXA = "";
    public BigDecimal codiemp = new BigDecimal(0);
    public String s_codiemp = "";
	public BigDecimal CODNAT = new BigDecimal(0);
	public String s_codnat = "";
	public BigDecimal NUMNOTA = new BigDecimal(0);
	public Timestamp DTNEG;
	public Timestamp DTVENC;
	public BigDecimal CODPARC = new BigDecimal(0);
	public Timestamp DHTIPOPER;
	public BigDecimal CODPROJ = new BigDecimal(0);
	public BigDecimal CODVEND = new BigDecimal(0);
	public BigDecimal CODMOEDA = new BigDecimal(0);
	public String NOSSONUM = "";
	public String HISTORICO = "";
	public BigDecimal VLRDESDOB = new BigDecimal(0);
	public String PROVISAO = "";
	public String dtneg = "";
	public String dtvenc = "";
	public String dhtipoper = "";
	public String codproj = "";
	public String codvend = "";
	public String codmoeda = "";
	public String historico = "";
	public String vlrdesdob = "";
	public String provisao = "";
	public String numnota = "";
	public String DESDBOBR = "";
	public String codcenus = "";
	public BigDecimal CODCENCUS = new BigDecimal(0);
	public BigDecimal Numero_Gerado_Fin = new BigDecimal(0);
	public BigDecimal codUsu = new BigDecimal(0);
	public BigDecimal Numero_Gerado_FRE = new BigDecimal(0);
	public BigDecimal Numero_Gerado_FRE2 = new BigDecimal(0);
	public BigDecimal Nunota = new BigDecimal(0);
	public String nunota = "";
	public BigDecimal newFin = new BigDecimal(0);
	public BigDecimal newFre = new BigDecimal(0);
	public BigDecimal newFre2 = new BigDecimal(0);
	public String msgErro = "";
	public BigDecimal codusuinc = new BigDecimal(0);
	public String CODUSUINC = "";
	public String ad_reg = "";
	public BigDecimal AD_REG = new BigDecimal(0);
	public String msg  = ""; 
	public String nuacerto ="";
	public BigDecimal NUACERTO = new BigDecimal(0);
	public BigDecimal VerificaDH = new BigDecimal(0);
	public BigDecimal Reg = new BigDecimal(0);
	public String MS = "";
	
	public String V_top = "";
	public String V_tipo_titulo = "";
	public BigDecimal V_TOP = new BigDecimal(0);
	public BigDecimal V_TIPO_TITULO = new BigDecimal(0);
	
	
	
	
	 AuthenticationInfo auth = AuthenticationInfo.getCurrent();
	  EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	  JdbcWrapper jdbc = this.dwfEntityFacade.getJdbcWrapper();
	  NativeSql nativeSql = new NativeSql(jdbc);
	  StringBuilder stringBuild = new StringBuilder();
	  
	  
	
	
	  public void doAction(ContextoAcao contextoAcao) throws Exception {
	    	
	    	System.out.println("Codigo do parametro: " + Codparc_s);

	    	codUsu = contextoAcao.getUsuarioLogado(); 
	    	Registro line = contextoAcao.getLinhas()[0];

	    	getinfo(line, contextoAcao);
	    	

	    	
	    	
	    	if (this.msgErro.equals("")) {
	    		msg  = "Deposito Autorizado com Sucesso!!" ;
	    		contextoAcao.setMensagemRetorno(msg);
			     Registro avisoSistema = contextoAcao.novaLinha("TSIAVI");
			     avisoSistema.setCampo("NUAVISO",null);
			     MS = "DEPOSITO LANÇADO NRO: "+ "<br>" + Nunota + " Hora:" +" " +TimeUtils.getNow();
			     avisoSistema.setCampo("TITULO", "DEPOSITO");
			     avisoSistema.setCampo("DESCRICAO", MS);
			     avisoSistema.setCampo("IDENTIFICADOR","PERSONALIZADO");
			     avisoSistema.setCampo("IMPORTANCIA",3);
			     avisoSistema.setCampo("CODUSU", codusuinc);
			     avisoSistema.setCampo("TIPO", "P");
			     avisoSistema.setCampo("DHCRIACAO",TimeUtils.getNow() );
			     avisoSistema.setCampo("CODUSUREMETENTE", codUsu);
			     avisoSistema.save();

			} else {
				contextoAcao.setMensagemRetorno(this.msgErro);
			}
	    	
	    	
	    }
	

	
	
	  private void getinfo (Registro line, ContextoAcao contextoAcao) throws Exception {
		  	
		  QueryExecutor rset1 = contextoAcao.getQuery();
		  QueryExecutor rset2 = contextoAcao.getQuery();
		  QueryExecutor rset3 = contextoAcao.getQuery();
		  QueryExecutor rset4 = contextoAcao.getQuery();
		  QueryExecutor rset5 = contextoAcao.getQuery();
		  QueryExecutor rset6 = contextoAcao.getQuery();
		  QueryExecutor rset7 = contextoAcao.getQuery();
		  QueryExecutor rset8 = contextoAcao.getQuery();
		  QueryExecutor rset9 = contextoAcao.getQuery();
		  ResultSet rset = null;
		  NativeSql sql8 = new NativeSql(jdbc);
		  NativeSql sql2 = new NativeSql(jdbc);
		  NativeSql sql5 = new NativeSql(jdbc);
	       NativeSql sql3 = new NativeSql(jdbc);
	       NativeSql sql4 = new NativeSql(jdbc);
	       NativeSql sql6 = new NativeSql(jdbc);
	       

		  
		  try {
		
			  
			  
		  Nufin = (BigDecimal)line.getCampo("NUFIN");
	    	System.out.println("Nufin é: " + Nufin);
	    	
	    
	    	 
	    	NativeSql sql = new NativeSql(jdbc);
	    	
	    	System.out.println("Inicio da top");
	    	StringBuffer sqlverifica = new StringBuffer();
	    	sqlverifica.append("SELECT 1 AS B");
	    	sqlverifica.append(" FROM ");
	    	sqlverifica.append(" TGFFIN ");
	    	sqlverifica.append(" WHERE ");
	    	sqlverifica.append(" CODTIPOPER NOT IN (9005,9007,9012) AND NUFIN =" + Nufin);
	    	rset8.nativeSelect(sqlverifica.toString());
	    	System.out.println("Meio da top");
	    	if (rset8.next())
	    	{
	    		
	    		V_top = rset8.getString("B");
	    		V_TOP = new BigDecimal(V_top);
	    		System.out.println("TIpo de TOP da verificacoa :"+V_TOP);
	    		
	    	}
	    	System.out.println("Passou da top");
	    	
	    	StringBuffer ab = new StringBuffer();
	    	ab.append("SELECT 1 AS A");
	    	ab.append(" FROM ");
	    	ab.append(" TGFFIN ");
	    	ab.append(" WHERE ");
	    	ab.append(" CODTIPTIT IN (7,01,73,51,16,8,76,62,72,82,9,90,57,75,45,46,52,74,37,42,43,44,38,49,50,17) AND NUFIN =" + Nufin);
	    	rset9.nativeSelect(ab.toString());
	    	System.out.println("Meio do titulo");
	    	if (rset9.next())
	    	{
	    		V_tipo_titulo = rset9.getString("A");
	    		V_TIPO_TITULO = new BigDecimal(V_tipo_titulo);
	    		System.out.println("TIpo de Titulo da verificacoa :"+V_TIPO_TITULO);
	    	}
	    	System.out.println("Fim do titulo");
	    	

	 	  
	    	StringBuffer sqlIt5 = new StringBuffer();
	    	sqlIt5.append(" SELECT NUNOTA");
	    	sqlIt5.append(" FROM ");
	    	sqlIt5.append(" TGFFIN ");
	    	sqlIt5.append(" WHERE ");
	    	sqlIt5.append(" NUFIN =" + Nufin);
		 	rset5.nativeSelect(sqlIt5.toString());
	    	
		 	if (rset5.next())
		 	{
		 		nunota = rset5.getString("NUNOTA");
		 		Nunota = new BigDecimal(nunota);
		 				System.out.println("NUNOTA QUERY1: " + Nunota);	
		 	}
		 	
	
	    	
	    	
	 	   StringBuffer sqlIt1 = new StringBuffer();
	 	  sqlIt1.append(" SELECT CODTIPOPER,DHBAIXA");
	 	 sqlIt1.append(" FROM ");
	 	sqlIt1.append(" TGFFIN ");
	 	sqlIt1.append(" WHERE ");
	 	sqlIt1.append(" NUFIN =" + Nufin);
	 	rset1.nativeSelect(sqlIt1.toString());
		    
	 	rset1.nativeSelect(sqlIt1.toString());
		    if (rset1.next()) {
		    	Top = rset1.getString("CODTIPOPER");
		    	System.out.println("CODIGO DA TOP DO SELECT "+Top);
		    	CodigoTop = new BigDecimal(Top);
		    } 
		    
		    StringBuffer sqlIt2 = new StringBuffer();   
		    sqlIt2.append(" SELECT NUCOMPENS");
		    sqlIt2.append(" FROM ");
		    sqlIt2.append(" TGFFIN ");
		    sqlIt2.append(" WHERE ");
		    sqlIt2.append(" NUFIN = " + Nufin);
		 	rset2.nativeSelect(sqlIt2.toString());
		 	
		 	if (rset2.next()) {
		 		NUCOMP = rset2.getString("NUCOMPENS");
		    	System.out.println("CODIGO DO NUCOMPESS "+NUCOMP);

		    	//NuCompess = new BigDecimal(NUCOMP);
		    	NuCompess = (NUCOMP == null) ? BigDecimal.ZERO : new BigDecimal(NUCOMP);
		 	}
		  
		    
		 	StringBuffer sqlIt4 = new StringBuffer();
		    sqlIt4.append(" SELECT ");
		    sqlIt4.append(" * ");
		    sqlIt4.append(" FROM TGFFIN ");
		    sqlIt4.append(" WHERE NUFIN = " + Nufin);
		    rset4.nativeSelect(sqlIt4.toString());
		    
		    

		    
		    if (rset4.next())
		    {
		        s_codiemp = rset4.getString("CODEMP");
		        s_codnat = rset4.getString("CODNAT");
		        codiemp = new BigDecimal(s_codiemp);
		        CODNAT = new BigDecimal(s_codnat);
		    	numnota = rset4.getString("NUMNOTA");
		    	NUMNOTA = new BigDecimal(numnota);
		    	//dtneg = rset4.getString("DTNEG");
		    	DTNEG =  rset4.getTimestamp("DTNEG");
		    	//dtvenc = rset4.getString("DTVENC");
		    	DTVENC = rset4.getTimestamp("DTVENC");
		    	Codparc_s = rset4.getString("CODPARC");
		    	codparc = new BigDecimal(Codparc_s);
		    	codproj = rset4.getString("CODPROJ");
		    	CODPROJ = new BigDecimal(codproj);
		    	codvend = rset4.getString("CODVEND");
		    	CODVEND = new BigDecimal(codvend);
		    	codmoeda = rset4.getString("CODMOEDA");
		    	CODMOEDA = new BigDecimal(codmoeda);
		    	NOSSONUM = rset4.getString("NOSSONUM");
		    	HISTORICO = rset4.getString("HISTORICO");
		    	DHTIPOPER = rset4.getTimestamp("DHTIPOPER");
		    	vlrdesdob = rset4.getString("VLRDESDOB");
		    	VLRDESDOB = new BigDecimal(vlrdesdob);
		    	PROVISAO = rset4.getString("PROVISAO");
		    	DESDBOBR = rset4.getString("DESDOBRAMENTO");
		    	codcenus = rset4.getString("CODCENCUS");
		    	CODCENCUS = new BigDecimal(codcenus);
		        
		        
		        System.out.println("PEGOU CODIEMP"+s_codiemp);
		    	System.out.println("PEGOU CODNAT"+s_codnat);
		    	
		    }
		 	
		 	
		 	StringBuffer sqlIt3 = new StringBuffer();
		 	sqlIt3.append(" SELECT DHBAIXA");
		    sqlIt3.append(" FROM ");
		    sqlIt3.append(" TGFFIN ");
		    sqlIt3.append(" WHERE ");
		    sqlIt3.append(" NUFIN = " + Nufin);
		    rset3.nativeSelect(sqlIt3.toString());
		    
		    

		    if (rset3.next()) {
		 		DHBAIXA = rset3.getString("DHBAIXA");
		    	System.out.println("CODIGO DO BAIXA "+DHBAIXA);
		    } 
		    
		    StringBuffer sqlIt6 = new StringBuffer();
		    sqlIt6.append(" SELECT CODUSUINC");
		    sqlIt6.append(" FROM ");
		    sqlIt6.append(" TGFCAB ");
		    sqlIt6.append(" WHERE ");
		    sqlIt6.append(" NUNOTA = " + Nunota);
		    rset6.nativeSelect(sqlIt6.toString());
		    
		    if (rset6.next())
		    {
		    	CODUSUINC = rset6.getString("CODUSUINC");
		    	codusuinc = new BigDecimal(CODUSUINC);
		    	System.out.println("COD USUARIO INCLUIDO QUERY6 "+codusuinc);
		    }
		    
		    StringBuffer sqlit7 = new StringBuffer();
		    sqlit7.append(" SELECT AD_REG");
		    sqlit7.append(" FROM ");
		    sqlit7.append(" TGFFIN ");
		    sqlit7.append(" WHERE ");
		    sqlit7.append(" NUFIN = " + Nufin);
		    rset7.nativeSelect(sqlit7.toString());
		    
		    if (rset7.next())
		    {
		    	ad_reg = rset7.getString("AD_REG");
		    	AD_REG = (ad_reg == null) ? BigDecimal.ZERO : new BigDecimal(ad_reg);
		    	System.out.println("AD REG INCLUIDO QUERY7 "+AD_REG);
		    }
		    
		   
		    
		   // sql8.appendSql(" SELECT MAX(NUACERTO)+1 AS MAX");
		  //  sql8.appendSql(" FROM ");
		  //  sql8.appendSql(" TGFFRE ");
		  //  rset = sql8.executeQuery();
		    
		   // if (rset.next()) {
		   //   nuacerto = rset.getString("MAX");
		    //	NUACERTO = new BigDecimal(nuacerto);
		    	System.out.println("nuacerto é :"+ NUACERTO);
		  //  } 
		    
		    
		   // sql5.setNamedParameter("P_C", NUACERTO);
		   // sql5.executeUpdate(" UPDATE TGFNUM SET ULTCOD = :P_C WHERE ARQUIVO='TGFFRE'");
		    
		    if (CodigoTop != new BigDecimal(9008) )
		    {
		    	System.out.println("If codigo top está funcionando");
		    }
		    if (NuCompess != new BigDecimal(0))
		    {
		    	System.out.println("If nucompess está funcionando");
		    }
		    
		    if (DHBAIXA == null)
		    {
		    	System.out.println("If DHBAIXA está funcionando");
		    	VerificaDH = new BigDecimal(0);
		    }
		    
		    if (AD_REG.equals(Nufin))
		    {
		    	System.out.println("If AD_REG está funcionando");
		    	Reg = new BigDecimal(1);
		    }
		    else {
		    	System.out.println("If AD_REG não está funcionando");
		    	Reg = new BigDecimal(0);
		    }
		    

		     //|| Reg == new BigDecimal(1)
		 	
		    if (V_TOP.equals(new BigDecimal(1)) || NuCompess.compareTo(new BigDecimal(0)) != 0 || VerificaDH.compareTo(new BigDecimal(0)) != 0 || Reg.equals(new BigDecimal(1)) || V_TIPO_TITULO.equals(new BigDecimal(1))   )   {
		    	{
		    	msgErro = ("Lançamento Não pode usar essa Rotina");
		    	}
		    }else {
		    	System.out.println("CAIU NO LOOP");
		    	
		    	criarNuacerto();
		    	newFin=criaFin(codiemp,NUMNOTA);
		    	newFre=criaNFRE(codiemp,NUMNOTA);
		    	newFre2=criaNFRE2(codiemp,NUMNOTA);
		    	System.out.println("NOVO FINANCEIRO "+newFin);
		    	System.out.println("NOVO FRE "+newFre);
		    	System.out.println("NOVO FRE2 "+newFre2);
		    	
		    
			    sql2.setNamedParameter("P_NUMDUPL", newFre);
		        sql2.setNamedParameter("P_NUCOMPENS", newFre);
		        sql2.setNamedParameter("P_DESDOBDUPL", "ZZ");
		        sql2.setNamedParameter("P_NUFIN", newFin);
		        sql2.executeUpdate(" UPDATE TGFFIN SET NUCOMPENS = :P_NUCOMPENS,NUMDUPL=:P_NUCOMPENS,DESDOBDUPL=:P_DESDOBDUPL WHERE NUFIN = :P_NUFIN ");
		    	
		
			    sql3.setNamedParameter("P_NUMDUPL", newFre);
		        sql3.setNamedParameter("P_NUCOMPENS", newFre);
		        sql3.setNamedParameter("P_DESDOBDUPL", "ZZ");
		        sql3.setNamedParameter("P_NUFIN", Nufin);
		        sql3.executeUpdate(" UPDATE TGFFIN SET NUCOMPENS = :P_NUCOMPENS,NUMDUPL=:P_NUCOMPENS,DESDOBDUPL=:P_DESDOBDUPL WHERE NUFIN = :P_NUFIN ");
			   
		       
		        sql4.setNamedParameter("P_NUNOTA", Nunota);
		        sql4.executeUpdate(" UPDATE TGFCAB SET AD_BARCO = 'L' WHERE NUNOTA= :P_NUNOTA");
		    	
		      
		        sql6.setNamedParameter("P_NUFI", Nufin);
		        sql6.executeUpdate(" UPDATE TGFFIN SET AD_REG = :P_NUFI WHERE NUFIN= :P_NUFI");
		        
		    	
		   } 
	  }catch (Exception e) {
		  e.printStackTrace();
		  System.out.println("Deu pau");}
		  finally {
				rset1.close();
				rset1.close();
				rset2.close();
				rset3.close();
				rset4.close();
				rset5.close();
				rset6.close();
				rset7.close();
				rset8.close();
				rset9.close();
		  
		  }
		  		
		    	   	             	  	    
	  }


	  public BigDecimal criaFin(BigDecimal empresa,BigDecimal Numnota) throws Exception {

		  	EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO padraoNPVO = null;
			try {
				padraoNPVO = dwfFacade.getDefaultValueObjectInstance("Financeiro");
			} catch (Exception e) {
				throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
			}


		  
			DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO;
			cabecalhoVO.setProperty("CODEMP",codiemp);
			cabecalhoVO.setProperty("NUMNOTA",NUMNOTA );
			cabecalhoVO.setProperty("DTNEG",DTNEG);
			cabecalhoVO.setProperty("DESDOBRAMENTO", DESDBOBR);
			cabecalhoVO.setProperty("DTVENC", DTVENC);
			cabecalhoVO.setProperty("CODPARC",codparc );
			cabecalhoVO.setProperty("CODTIPOPER",CodigoTop );
			    	cabecalhoVO.setProperty("DHTIPOPER", DHTIPOPER);
			    	cabecalhoVO.setProperty("CODBCO", new BigDecimal(999));
			    	cabecalhoVO.setProperty("CODNAT", CODNAT);
			    	cabecalhoVO.setProperty("CODCTABCOINT", new BigDecimal(25));
			    	cabecalhoVO.setProperty("CODCENCUS", CODCENCUS);
			    	cabecalhoVO.setProperty("CODPROJ", CODPROJ);
			    	cabecalhoVO.setProperty("CODVEND", CODVEND);
			    	cabecalhoVO.setProperty("CODMOEDA", CODMOEDA);
			    	cabecalhoVO.setProperty("CODTIPTIT", new BigDecimal(40));
			    	cabecalhoVO.setProperty("NOSSONUM", NOSSONUM);
			    	cabecalhoVO.setProperty("HISTORICO", HISTORICO);
			    	cabecalhoVO.setProperty("VLRDESDOB", VLRDESDOB);
			    	cabecalhoVO.setProperty("RECDESP", new BigDecimal(-1));
			    	cabecalhoVO.setProperty("PROVISAO", PROVISAO);
			    	cabecalhoVO.setProperty("ORIGEM", "F");
			    	cabecalhoVO.setProperty("DTALTER", TimeUtils.getNow());
			    	cabecalhoVO.setProperty("CODUSU", codUsu);
			    	cabecalhoVO.setProperty("DHMOV", TimeUtils.getNow());
			    	cabecalhoVO.setProperty("CODUSU", codUsu);
			    	dwfFacade.createEntity("Financeiro", (EntityVO) cabecalhoVO);
			    	Numero_Gerado_Fin = cabecalhoVO.asBigDecimal("NUFIN");

			    	System.out.println("Criou o Financeiro" + Numero_Gerado_Fin);
			    	System.out.println("Criou o Financeiro" + cabecalhoVO);
			return Numero_Gerado_Fin;
		}
	  
	  public BigDecimal criaNFRE(BigDecimal empresa,BigDecimal Numnota) throws Exception {

		  	EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO padraoNPVO1 = null;
			try {
				padraoNPVO1 = dwfFacade.getDefaultValueObjectInstance("AcertoFrete");
			} catch (Exception e) {
				throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
			}

		  
			DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO1;
			cabecalhoVO.setProperty("NUACERTO",NUACERTO);
			cabecalhoVO.setProperty("NUFIN",newFin);
			cabecalhoVO.setProperty("CODUSU", codUsu);
			cabecalhoVO.setProperty("TIPACERTO","A" );
			cabecalhoVO.setProperty("DHALTER",TimeUtils.getNow() );
			cabecalhoVO.setProperty("SEQUENCIA",new BigDecimal(1));
			dwfFacade.createEntity("AcertoFrete", (EntityVO) cabecalhoVO);
			
			
			Numero_Gerado_FRE = cabecalhoVO.asBigDecimal("NUACERTO");

			System.out.println("Criou NUFREE" + cabecalhoVO);
			return Numero_Gerado_FRE;
		}
	  
	  public BigDecimal criaNFRE2(BigDecimal empresa,BigDecimal Numnota) throws Exception {

		  	EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			EntityVO padraoNPVO1 = null;
			try {
				padraoNPVO1 = dwfFacade.getDefaultValueObjectInstance("AcertoFrete");
			} catch (Exception e) {
				throw new Exception("Nfoi possencontrar um nvalido.\nVerifique se o modelo cadastrado no parametro NOTAMODMED, estcom um numero de nota v");
			}

		  
			DynamicVO cabecalhoVO = (DynamicVO) padraoNPVO1;
			cabecalhoVO.setProperty("NUACERTO",NUACERTO);
			cabecalhoVO.setProperty("NUFIN",Nufin);
			cabecalhoVO.setProperty("CODUSU", codUsu);
			cabecalhoVO.setProperty("TIPACERTO","A" );
			cabecalhoVO.setProperty("DHALTER",TimeUtils.getNow() );
			cabecalhoVO.setProperty("SEQUENCIA",new BigDecimal(2) );
			dwfFacade.createEntity("AcertoFrete", (EntityVO) cabecalhoVO);
			
			
			Numero_Gerado_FRE2 = cabecalhoVO.asBigDecimal("NUACERTO");

			System.out.println("Criou NUFREE" + cabecalhoVO);
			return Numero_Gerado_FRE2;
		}
	  
	  
	  private void criarNuacerto() {
			SessionHandle hnd = null;
			JdbcWrapper jdbc = null;

			try {
				hnd = JapeSession.open();
				EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

				jdbc = dwfFacade.getJdbcWrapper();
				jdbc.openSession();
				
				Integer nuacerto = 0;

				CallableStatement cstmt = jdbc.getConnection().prepareCall("{call STP_KEYGEN_TGFNUM(?,?,?,?,?,?)}");
				cstmt.setQueryTimeout(60);

				cstmt.setString(1, "TGFFRE");
				cstmt.setBigDecimal(2, BigDecimal.ONE);
				cstmt.setString(3, "TGFFRE");
				cstmt.setString(4, "NUACERTO");
				cstmt.setBigDecimal(5, BigDecimal.ZERO);
		    
				cstmt.registerOutParameter(6, nuacerto);

				cstmt.execute();

				 NUACERTO = (BigDecimal) cstmt.getObject(6);

				System.out.println("Saida: " + nunota);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				JdbcWrapper.closeSession(jdbc);
				JapeSession.close(hnd);
			}
		}
	  
	  
	  
	  

	  
	

}
	  
	  
	  
	  
	  
	  
	  


