package src.gmad;

import java.sql.Timestamp;

import org.apache.lucene.util.fst.Util;

import com.ibm.icu.math.BigDecimal;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;

public class envioemail {
	
	
	public String mensagem = "";
	public BigDecimal codfila = new BigDecimal(0);
	
	
	
	public void envio_email()
	{
	JapeWrapper email = JapeFactory.dao("MSDFilaMensagem");

	DynamicVO cabApoVO = email.create()
			.set("CODFILA", codfila)
			.set("CODMSG", null)
			.set("DTENTRADA", new Timestamp(System.currentTimeMillis()))
			.set("STATUS", "Pendente")
			.set("CODCON", BigDecimal.valueOf(0))
			.set("TENTENVIO", BigDecimal.valueOf(1))
			.set("MENSAGEM", mensagem.toCharArray())
			.set("TIPOENVIO", "E")
			.set("MAXTENTENVIO", BigDecimal.valueOf(3))
			.set("NUANEXO", null)
			.set("ASSUNTO", "TESTE")
			.set("EMAIL", "alerrandro.barreto@centrodoaluminio.com.br")
			.set("MIMETYPE", null)
			.set("TIPODOC", null)
			.set("CODUSU", BigDecimal.valueOf(0))
			.set("NUCHAVE", null)
			.set("CODUSUREMET", null)
			.set("REENVIAR", "N")
			.set("MSGERRO", null)
			.set("CODSMTP", null)
			.set("DHULTTENTA", null)
			.set("DBHASHCODE", null)
			.set("CODCONTASMS", null)
			.set("CELULAR", null)
			.save();
	}catch (Exception e)
	{
		System.out.println(e.getMessage());
	}


}
