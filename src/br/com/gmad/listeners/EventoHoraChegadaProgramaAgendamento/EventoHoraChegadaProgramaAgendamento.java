package br.com.gmad.listeners.EventoHoraChegadaProgramaAgendamento;

import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class EventoHoraChegadaProgramaAgendamento implements EventoProgramavelJava {
  public void afterDelete(PersistenceEvent arg0) throws Exception {}
  
  public void afterInsert(PersistenceEvent arg0) throws Exception {}
  
  public void afterUpdate(PersistenceEvent ctx) throws Exception {
    System.out.println("INICIO EventoHoraChegadaProgramaAgendamento afterUpdate");
    DynamicVO registro = (DynamicVO)ctx.getVo();
    int NFS = registro.asInt("NF");
    Object horaChegadaObj = registro.getProperty("HORA_CHEGADA");
    Timestamp HORA_CHEGADA = null;
    if (horaChegadaObj != null)
      if (horaChegadaObj instanceof Timestamp) {
        HORA_CHEGADA = (Timestamp)horaChegadaObj;
      } else if (horaChegadaObj instanceof BigDecimal) {
        BigDecimal bigDecimalHoraChegada = (BigDecimal)horaChegadaObj;
        HORA_CHEGADA = new Timestamp(bigDecimalHoraChegada.longValue());
      } else {
        System.out.println("Tipo inesperado para HORA_CHEGADA: " + horaChegadaObj.getClass().getName());
        throw new IllegalArgumentException("Tipo invpara HORA_CHEGADA");
      }  
    if (HORA_CHEGADA != null) {
      JdbcWrapper jdbc = null;
      EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
      jdbc = dwfEntityFacade.getJdbcWrapper();
      System.out.println("EventoHoraChegadaProgramaAgendamento HORA_CHEGADA: " + HORA_CHEGADA);
      String titulo = "Recebimento da NFS: " + NFS;
      String descricao = "A NFS foi recebida.";
      String identificador = "PERSONALIZADO";
      int importancia = 3;
      String tipo = "P";
      int[] codUsuArray = { 634, 484, 87, 9, 450, 455 };
      int codUsuRemetente = 0;
      try {
        byte b;
        int i;
        int[] arrayOfInt;
        for (i = (arrayOfInt = codUsuArray).length, b = 0; b < i; ) {
          int codUsu = arrayOfInt[b];
          NativeSql getNextNuAvisoSql = new NativeSql(jdbc);
          getNextNuAvisoSql.appendSql("SELECT ISNULL(MAX(NUAVISO), 0) + 1 AS NEXT_NUAVISO FROM TSIAVI");
          int nextNuAviso = 1;
          ResultSet rs = getNextNuAvisoSql.executeQuery();
          if (rs.next())
            nextNuAviso = rs.getInt("NEXT_NUAVISO"); 
          rs.close();
          NativeSql insertSql = new NativeSql(jdbc);
          String insertSQL = String.format(
              "INSERT INTO TSIAVI (NUAVISO, TITULO, DESCRICAO, IDENTIFICADOR, IMPORTANCIA, CODUSU, CODGRUPO, TIPO, DHCRIACAO, CODUSUREMETENTE) VALUES (%d, '%s', '%s', '%s', %d, %d, NULL, '%s', GETDATE(), %d);", new Object[] { Integer.valueOf(nextNuAviso), titulo, descricao, identificador, Integer.valueOf(importancia), Integer.valueOf(codUsu), tipo, Integer.valueOf(codUsuRemetente) });
          System.out.println("Executando SQL: " + insertSQL);
          insertSql.appendSql(insertSQL);
          insertSql.executeUpdate();
          NativeSql atualizaNum = new NativeSql(jdbc);
          atualizaNum.setNamedParameter("PULTCOD", nextNuAviso);
          atualizaNum.executeUpdate("UPDATE TGFNUM SET ULTCOD = :PULTCOD WHERE ARQUIVO = 'TSIAVI'");
          b++;
        } 
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("[EventoHoraChegadaProgramaAgendamento] " + e.getMessage());
      } finally {
        if (jdbc != null)
          try {
            jdbc.closeSession();
          } catch (Exception e) {
            e.printStackTrace();
          }  
      } 
    } 
  }
  
  public void beforeCommit(TransactionContext arg0) throws Exception {}
  
  public void beforeDelete(PersistenceEvent arg0) throws Exception {}
  
  public void beforeInsert(PersistenceEvent arg0) throws Exception {}
  
  public void beforeUpdate(PersistenceEvent arg0) throws Exception {}
}
