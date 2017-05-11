package me.TheDigitalDev.ContactsMap;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;

 /**
  * Class created by TheDigitalDev on 5/5/2017.
  *
  * Plugin extension for BaccaYarro/Lorax42's Movecraft plugin
  */

public class GenerateGrid {


     int chunkMultiplier = 1; // Zoom settings
     int chunkSize = 16;

    // [x][z]
    private final String[][] gridArray = new String[21][20];


    public void generatemap(Chunk playerChunk, List<Chunk> chunkList, Player p, int zoomsetting){

        chunkMultiplier = zoomsetting; // Zoom settings
        chunkSize = 16;

        chunkMultiplier = chunkMultiplier * chunkSize;

         // Fill array
        for(int x=0;x<gridArray.length;x++)
             for(int y=0;y<gridArray[x].length;y++)
                 gridArray[x][y] = Character.toString('-');


        int playerCornerX = calculateCornerX(playerChunk) / chunkMultiplier;
        int playerCornerZ = calculateCornerZ(playerChunk) / chunkMultiplier;



        int calcChunkX;
        int calcChunkZ;

        // Get each chunk and divide the NorthWest corner by 16 in order to get each individual chunk in its representable form(Hard to explain ik)
        // After that make it relative to the player's position by subtracting the player's chunk by the calculated chunk
        // Then align it to the origin by adding 10 each.

	    for(int i = 0; i < chunkList.size(); i++){
           	calcChunkX = ((calculateCornerX(chunkList.get(i)) / chunkMultiplier) - playerCornerX) + 10;
           	calcChunkZ = ((calculateCornerZ(chunkList.get(i)) / chunkMultiplier) - playerCornerZ) + 10;

           	p.sendMessage("cX" + calcChunkX + " cZ" + calcChunkZ);
            if(calcChunkX < 21 && calcChunkZ < 20 && calcChunkX > -1 && calcChunkZ > -1) {
                gridArray[calcChunkX][calcChunkZ] = "+";
            }
        }

        // Setup the origin
        gridArray[10][10] = "^";

        // Once we have the entire map stored in the array send it
        sendGridMap(p);
    }


     private void sendGridMap(Player p){

        String gridRow;

         // Z = Y in this case, but in order to be consistent I kept it at Z
         for(int z = 0; z < 20; z++) {
            // The row will be appended into a string then sent
            gridRow = "";
            StringBuilder sb = new StringBuilder(gridRow);
            for (int x = 0; x < 21; x++) {
                sb.append(gridArray[x][z]);
            }
            p.sendMessage( ": " + sb.toString() + " :");

         }
    }

    private int calculateCornerX(Chunk c){
        return c.getX() + (c.getX() % chunkMultiplier);
    }

     private int calculateCornerZ(Chunk c){
         return c.getZ() + (c.getZ() % chunkMultiplier);
     }
}


