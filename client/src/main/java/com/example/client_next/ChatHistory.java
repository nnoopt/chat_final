package com.example.client_next;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ChatHistory implements AutoCloseable {
        private static final String FILENAME = "./history/history_%s.txt";

        private final String nickname;
        private PrintWriter printWriter;
        private File historyFile;

    public ChatHistory(String nickname) {
        this.nickname = nickname;
    }

    public void init(){
        try {
            historyFile = createHistoryFile();
            this.printWriter = new PrintWriter(new BufferedWriter(new FileWriter(historyFile, StandardCharsets.UTF_8, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendText (String text){
        printWriter.print(text);
        printWriter.flush();
    }

    private File createHistoryFile () throws IOException {
        String filePath = String.format(FILENAME, nickname);
        File file = new File(filePath);
        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    @Override
    public void close() throws Exception {
        if (printWriter!=null){
            printWriter.close();
        }
    }
}
