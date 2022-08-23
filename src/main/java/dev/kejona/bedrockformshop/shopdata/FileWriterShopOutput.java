package dev.kejona.bedrockformshop.shopdata;

import dev.kejona.bedrockformshop.BedrockFormShop;
import org.bukkit.Material;

import java.io.*;
import java.util.Date;

public class FileWriterShopOutput {
    private final File folder;
    private final Material item;
    private final String playername;
    private final double price;
    private final int amount;

    public FileWriterShopOutput(String playername, double price, int amount, Material item) throws IOException {

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

        pw.println(playername + " Bought: " + amount + " " + item.name() + " for: " + price + " on: " + new Date());
        pw.close();
    }
}
