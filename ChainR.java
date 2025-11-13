import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

class LogMessage {
    private String nivel;
    private String mensagem;
    private LocalDateTime timestamp;

    public LogMessage(String nivel, String mensagem, LocalDateTime timestamp) {
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.timestamp = timestamp;
    }

    public String getNivel() { return nivel; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] [" + nivel + "] " + mensagem;
    }
}

interface LogHandler {
    void processarLog(LogMessage log);
    void setNext(LogHandler next);
}

class InfoHandler implements LogHandler {
    private LogHandler next;
    private List<LogMessage> logsInfo = new ArrayList<>();

    @Override
    public void processarLog(LogMessage log) {
        if ("INFO".equalsIgnoreCase(log.getNivel())) {
            logsInfo.add(log);
        } else if (next != null) {
            next.processarLog(log);
        }
    }
    @Override
    public void setNext(LogHandler next) {
        this.next = next;
    }
    public List<LogMessage> getLogsInfo() {
        return logsInfo;
    }
}

class WarningHandler implements LogHandler {
    private LogHandler next;
    private String filePath = "warnings.log";

    @Override
    public void processarLog(LogMessage log) {
        if ("WARNING".equalsIgnoreCase(log.getNivel())) {
            try (FileWriter fw = new FileWriter(filePath, true)) {
                fw.write(log.toString() + "\n");
            } catch (IOException e) {
                System.err.println("Erro ao escrever no arquivo de warnings: " + e.getMessage());
            }
        } else if (next != null) {
            next.processarLog(log);
        }
    }
    @Override
    public void setNext(LogHandler next) {
        this.next = next;
    }
}

class ErrorHandler implements LogHandler {
    private LogHandler next;

    @Override
    public void processarLog(LogMessage log) {
        if ("ERROR".equalsIgnoreCase(log.getNivel())) {
            System.out.println("!! Notificação por e-mail enviada: " + log);
        } else if (next != null) {
            next.processarLog(log);
        }
    }
    @Override
    public void setNext(LogHandler next) {
        this.next = next;
    }
}

class DebugHandler implements LogHandler {
    private LogHandler next;

    @Override
    public void processarLog(LogMessage log) {
        if ("DEBUG".equalsIgnoreCase(log.getNivel())) {
            System.out.println("[DEBUG] " + log);
        } else if (next != null) {
            next.processarLog(log);
        }
    }
    @Override
    public void setNext(LogHandler next) {
        this.next = next;
    }
}

public class ChainR {
    public static void main(String[] args) {
        InfoHandler info = new InfoHandler();
        WarningHandler warning = new WarningHandler();
        ErrorHandler error = new ErrorHandler();
        DebugHandler debug = new DebugHandler();

        debug.setNext(info);
        info.setNext(warning);
        warning.setNext(error);

        LogHandler cadeia = debug;

        LogMessage log1 = new LogMessage("DEBUG", "Mensagem de depuração", LocalDateTime.now());
        LogMessage log2 = new LogMessage("INFO", "Informação relevante", LocalDateTime.now());
        LogMessage log3 = new LogMessage("WARNING", "Atenção! Possível problema", LocalDateTime.now());
        LogMessage log4 = new LogMessage("ERROR", "Erro crítico, aplicação parou", LocalDateTime.now());

        cadeia.processarLog(log1);
        cadeia.processarLog(log2);
        cadeia.processarLog(log3);
        cadeia.processarLog(log4);

        System.out.println("\nLogs INFO armazenados:");
        for (LogMessage m : info.getLogsInfo()) {
            System.out.println(m);
        }
    }
}