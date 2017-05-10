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

    private final String[][] gridArray = new String[20][20];


     public void generatemap(Chunk playerChunk, List<Chunk> chunkList, Player p){


         // Fill array
         for(int x=0;x<gridArray.length;x++)
             for(int y=0;y<gridArray[x].length;y++)
                 gridArray[x][y] = Character.toString('-');


         int playerCornerX = calculateCorner(playerChunk) / 16;
         int playerCornerZ = calculateCorner(playerChunk) / 16;

         // x10z10 is origin
        gridArray[10][10] = "^";


        int calcChunkX;
        int calcChunkZ;

         // Get each chunk and divide the NorthWest corner by 16 in order to get each individual chunk in its representable form(Hard to explain ik)
         // After that make it relative to the player's position by subtracting the player's chunk by the calculated chunk
         // Then align it to the origin by adding 10 each.

         for(int i = 0; i < chunkList.size(); i++){
          for(int i = 0; i < chunkList.size(); i++){
           	calcChunkX = ((calculateCorner(chunkList.get(i)) / 16) - playerCornerX) + 10;
           }

          /*
            calcChunkX = calculateCornerXOfChunk(chunkList.get(i));
            calcChunkZ = calculateCornerZOfChunk(chunkList.get(i));

            calcChunkX = calcChunkX / 16;
            calcChunkZ = calcChunkZ / 16;

            // I know this could be simplified but i'm too dumb to visualize the math without it being expanded
            calcChunkX = calcChunkX - playerCornerX;
            calcChunkZ = calcChunkZ - playerCornerZ;

            calcChunkX += 10;
            calcChunkZ += 10;
          */
            gridArray[calcChunkX][calcChunkZ] = "+";

            // Once we have the entire map stored in the array send it
            sendGridMap(p);
         }

    }


    private void sendGridMap(Player p){

         String gridRow;
         StringBuilder sb = new StringBuilder(gridRow);

         // Z = Y in this case, but in order to be consistent I kept it at Z
         for(int z = 0; z < 20; z++) {
            // The row will be appended into a string then sent
            gridRow = "";
            for (int x = 0; x < 20; x++) {
                sb.append(gridArray[x][z]);
            }
            p.sendMessage(sb.toString());

        }
    }

    calculateCorner(Chunk c){
         // Untested code, meant to replace chunk.getX
	        return c.getX() - (c.getX() % 16)
    }
    /* TEMPORARILY COMMENTED OUT FOR OPTIMIZATION TEST CODE(Can't test it ATM will do at home)
    // North of chunk
    private int calculateCornerXOfChunk(Chunk chunk){
        int x = chunk.getX();
       return x << 4;
    }


    // West of chunk
    private int calculateCornerZOfChunk(Chunk chunk){
        int z = chunk.getZ();
        return z << 4;
    }
    */ 
}


