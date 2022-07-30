package dev.kejona.bedrockformshop.utils;

import dev.kejona.bedrockformshop.BedrockFormShop;

import java.io.*;
import java.util.Date;

public class ShopData {
    private final File folder;
    private final String item;
    private final String playername;
    private final double price;
    private final int amount;

    public ShopData(String playername, double price, int amount, String item) throws IOException {
        this.folder = BedrockFormShop.getInstance().getDataFolder();
        this.playername = playername;
        this.price = price;
        this.amount = amount;
        this.item = item;

        writeData();
    }

    //Write the transactions' data to the transactions.txt file.
    public void writeData () throws IOException {
        File transactionFile = new File(folder, "transactions.txt");
        FileWriter writeTransaction = new FileWriter(transactionFile, true);
        BufferedWriter bw = new BufferedWriter(writeTransaction);
        PrintWriter pw = new PrintWriter(bw);

        pw.println(playername + " Bought: " + amount + " " + item + " for: " + price + " on: " + new Date());
        pw.close();
    }
}
