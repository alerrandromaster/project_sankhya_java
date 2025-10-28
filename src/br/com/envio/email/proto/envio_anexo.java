package br.com.envio.email.proto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;

import org.apache.commons.io.FileUtils;

import com.sankhya.util.SessionFile;
import com.sankhya.util.UIDGenerator;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.extensions.actionbutton.Registro;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.ws.ServiceContext;

public class envio_anexo implements AcaoRotinaJava {
	

String pkRegistro;
String chaveArquivo;
String nomeArquivo;

BigDecimal nuattach;
BigDecimal ida;

@Override
public void doAction(ContextoAcao ctx) throws Exception {
	// TODO Auto-generated method stub

	for (int i = 0; i < ctx.getLinhas().length; i++) {
		Registro line = ctx.getLinhas()[i];

		System.out.println("AbrirAnexo Inicio");

		try {

			gerarRelatorio(ctx, line);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

private void gerarRelatorio(ContextoAcao ctx, Registro line) throws Exception {

	EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
	JdbcWrapper jdbc = dwfEntityFacade.getJdbcWrapper();
	NativeSql nativeSql = new NativeSql(jdbc);

	//ConcatenatePDF concatenate = new ConcatenatePDF();

	try {

		jdbc.openSession();

		BigDecimal codContaCta = (BigDecimal) line.getCampo("CODCTABCOINT");
		System.out.println("Cod conta :" + codContaCta);

		BigDecimal nuFin = (BigDecimal) line.getCampo("NUFIN");
		System.out.println("nufin :" + nuFin);

		ResultSet rsIda = nativeSql.executeQuery("SELECT CODCTABCOINT, NUFIN, IDA," + "	 FROM AD_FAEPRCDA "
				+ "	WHERE " + "	CODCTABCOINT = " + codContaCta + " AND NUFIN = " + nuFin);

		while (rsIda.next()) {
			
			ida = rsIda.getBigDecimal("IDA");

			ResultSet rsPRCD = nativeSql.executeQuery(
					"SELECT CODCTABCOINT, NUFIN, IDA," + "  CAST(" + codContaCta + " AS VARCHAR(10))+'_'+CAST("
							+ nuFin + " AS varchar(10))+'_'+CAST("+ ida +" AS varchar(10))+'_'+'AD_FAEPRCDA' as PKREGISTRO "
							+ " FROM AD_FAEPRCDA" + " WHERE \r\n" + "CODCTABCOINT = " + codContaCta
							+ " AND NUFIN = " + nuFin);

			while (rsPRCD.next()) {
				pkRegistro = rsPRCD.getString("PKREGISTRO");
				System.out.println("pkRegistro :" + pkRegistro);
			}

			ResultSet rsNuattach = nativeSql
					.executeQuery("SELECT NUATTACH, CHAVEARQUIVO, NOMEARQUIVO FROM TSIANX  where PKREGISTRO IN (\'"
							+ pkRegistro + "\')");

			while (rsNuattach.next()) {
				nuattach = rsNuattach.getBigDecimal("NUATTACH");
				chaveArquivo = rsNuattach.getString("CHAVEARQUIVO");
				nomeArquivo = rsNuattach.getString("NOMEARQUIVO");

				System.out
						.println("C:\\.sw_file_repository\\Sistema\\Sistema\\Anexos\\AD_FAEPRCDA\\" + chaveArquivo);

				// File arquivoRepositorio = new
				// File("C:\\Sankhya\\Sistema\\Anexos\\TcfDocumento\\" + chaveString);
				File arquivoRepositorio = new File(
						"C:\\.sw_file_repository\\Sistema\\Sistema\\Anexos\\AD_FAEPRCDA\\" + chaveArquivo);
				
				//ByteArrayOutputStream bytes = concatenate.run();

				String chaveSessaoArquivo = "ARQUIVOANEXO" + UIDGenerator.getNextID();
				SessionFile sessionFile = SessionFile.createSessionFile(nomeArquivo, "",
						FileUtils.readFileToByteArray(arquivoRepositorio));

				ServiceContext.getCurrent().putHttpSessionAttribute(chaveSessaoArquivo, (Serializable) sessionFile);

				ctx.setMensagemRetorno(String.format("Arquivo gerado.\n %s",
						getLinkBaixar("Clique aqui para baixar.", chaveSessaoArquivo)));
			}
		}

	} catch (Exception e) {
		jdbc.closeSession();
		e.printStackTrace();
	} finally {
		jdbc.closeSession();
	}

}

private String getLinkBaixar(String descricao, String chave) {
	String url = "<a title=\"Visualizar Arquivo\" href=\"/mge/visualizadorArquivos.mge?chaveArquivo=" + chave
			+ "\" target=\"_blank\"><u><b>" + descricao + "</b></u></a>";

	return url;
}
}
